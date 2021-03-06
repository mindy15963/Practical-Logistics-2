package sonar.logistics.core.tiles.misc.hammer;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

import javax.annotation.Nonnull;

public class TileEntityHammer extends TileEntityInventory implements ISidedInventory, IByteBufTile, IFlexibleGui {

	public SyncTagType.INT progress = new SyncTagType.INT(0);
	public SyncTagType.INT coolDown = new SyncTagType.INT(1);
	public static int speed = 100;

	public TileEntityHammer() {
		super.inv.setSize(2);
		syncList.addParts(progress, coolDown);
	}
	
	public SyncType getUpdateTagType(){
		return SyncType.SAVE;
	}
	
	public void update() {
		super.update();
		if (coolDown.getObject() != 0) {
			coolDown.increaseBy(-1);
		} else if (canProcess()) {
			if (progress.getObject() < speed) {
				if(progress.getObject()==0){
					SonarCore.sendFullSyncAround(this, 64);
				}
				progress.increaseBy(1);				
			} else {
				coolDown.setObject(speed * 2);
				progress.setObject(0);
				if (!this.getWorld().isRemote) {
					finishProcess();	
					SonarCore.sendFullSyncAround(this, 64);
				}
			}
		} else {
			if (progress.getObject() != 0) {
				this.progress.setObject(0);
			}
		}
	}

	public boolean canProcess() {
		if (slots().get(0).isEmpty()) {
			return false;
		}

		ISonarRecipe recipe = HammerRecipes.instance().getRecipeFromInputs(null, new Object[] { slots().get(0) });
		if (recipe == null) {
			return false;
		}
		ItemStack outputStack = RecipeHelperV2.getItemStackFromList(recipe.outputs(), 0);
		if (outputStack.isEmpty()) {
			return false;
		} else if (!slots().get(1).isEmpty()) {
			if (!slots().get(1).isItemEqual(outputStack)) {
				return false;
			} else return slots().get(1).getCount() + outputStack.getCount() <= slots().get(1).getMaxStackSize();
		}

		return true;
	}

	public void finishProcess() {
		ISonarRecipe recipe = HammerRecipes.instance().getRecipeFromInputs(null, new Object[] { slots().get(0) });
		if (recipe == null) {
			return;
		}
		ItemStack outputStack = RecipeHelperV2.getItemStackFromList(recipe.outputs(), 0);
		if (!outputStack.isEmpty()) {
			if (this.slots().get(1).isEmpty()) {
				this.slots().set(1, outputStack.copy());
			} else if (this.slots().get(1).isItemEqual(outputStack)) {
				this.slots().get(1).grow(outputStack.getCount());
			}
			this.slots().get(0).shrink(recipe.inputs().get(0).getStackSize());
			if (this.slots().get(0).getCount() <= 0) {
				this.slots().set(0, ItemStack.EMPTY);
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return slot == 0 && HammerRecipes.instance().isValidInput(stack);

    }

	public boolean maxRender() {
		return true;
	}

	@Nonnull
    @Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
		return slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
		return slot == 1;
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		if (i == 1) {
			markBlockForUpdate();
			SonarCore.sendFullSyncAround(this, 64);
		}
	}

	public int getSpeed() {
		return speed;
	}

	public int getProgress() {
		return progress.getObject();
	}

	public int getCoolDown() {
		return coolDown.getObject();
	}

	public int getCoolDownSpeed() {
		return speed * 2;
	}

	//// PACKETS \\\\

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			progress.writeToBuf(buf);
			break;
		case 1:
			coolDown.writeToBuf(buf);
			break;
		}

	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			progress.readFromBuf(buf);
			break;
		case 1:
			coolDown.readFromBuf(buf);
			break;
		}
	}
	
	//// GUI \\\\

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerHammer(player, this);
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new GuiHammer(player, this);
	}
}
