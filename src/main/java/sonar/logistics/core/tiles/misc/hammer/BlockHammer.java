package sonar.logistics.core.tiles.misc.hammer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.common.block.SonarBlockContainer;
import sonar.core.common.block.SonarMaterials;
import sonar.core.network.FlexibleGuiHandler;
import sonar.logistics.PL2Blocks;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockHammer extends SonarBlockContainer {

	public BlockHammer() {
		super(SonarMaterials.machine, true);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity tile = world.getTileEntity(pos);
		if (!world.isRemote && tile != null) {
			FlexibleGuiHandler.instance().openBasicTile(player, world, pos, 0);
			return true;
		}
		return false;
	}

	//// CREATE \\\\

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int i) {
		return new TileEntityHammer();
	}

	@Override
	public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
        return world.isAirBlock(pos.offset(EnumFacing.UP, 1)) && world.isAirBlock(pos.offset(EnumFacing.UP, 2));
    }

	//// EVENTS \\\\

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		forEachPosition(pos, world::setBlockToAir);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		forEachPosition(pos, p -> world.setBlockState(p, PL2Blocks.hammer_air.getDefaultState(), 2));
	}

	public void forEachPosition(BlockPos pos, Consumer<BlockPos> consumer) {
		consumer.accept(pos.offset(EnumFacing.UP, 1));
		consumer.accept(pos.offset(EnumFacing.UP, 2));
	}

	//// RENDERING \\\\

	@Nonnull
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
