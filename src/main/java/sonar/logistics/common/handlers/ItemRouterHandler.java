package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.InventoryHelper;
import sonar.core.utils.helpers.InventoryHelper.IInventoryFilter;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;

public class ItemRouterHandler extends InventoryTileHandler implements ISidedInventory, IByteBufTile {

	// 0=nothing, 1=input, 2=output
	public SyncInt[] sideConfigs = new SyncInt[6];
	public SyncInt listType = new SyncInt(7);
	public SyncInt side = new SyncInt(8);
	public SyncInt filterPos = new SyncInt(9);

	public List<BlockCoords>[] coords = new List[6];
	public List<ItemFilter>[] lastWhitelist = new List[6];
	public List<ItemFilter>[] whitelist = new List[6];

	public List<ItemFilter>[] lastBlacklist = new List[6];
	public List<ItemFilter>[] blacklist = new List[6];

	public int update = 0;
	public int updateTime = 20;

	public int clientClick = -1;
	public int editStack = -1, editOre = -1;
	public ItemStackFilter clientStackFilter = new ItemStackFilter();
	public OreDictionaryFilter clientOreFilter = new OreDictionaryFilter();

	public ItemRouterHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		super.slots = new ItemStack[9];
		for (int i = 0; i < 6; i++) {
			sideConfigs[i] = new SyncInt(i + 1);
			lastWhitelist[i] = new ArrayList();
			whitelist[i] = new ArrayList();
			lastBlacklist[i] = new ArrayList();
			blacklist[i] = new ArrayList();
			coords[i] = new ArrayList();
		}
	}

	public List<ItemFilter> getFilters() {
		return listType.getInt() == 0 ? whitelist[side.getInt()] : blacklist[side.getInt()];

	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		if (update < updateTime) {
			update++;
		} else {
			update = 0;
			updateConnections(te);
		}
		if (update < 6) {
			int config = this.sideConfigs[update].getInt();
			if (config != 0) {
				TileEntity target = null;
				ForgeDirection dir = null;
				if (this.coords[update] != null) {
					for (BlockCoords coords : this.coords[update]) {
						TileEntity tile = coords.getTileEntity();
						if (tile != null && tile instanceof TileEntityBlockNode) {
							TileEntityBlockNode node = (TileEntityBlockNode) tile;
							dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
							target = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);

						} else {
							dir = ForgeDirection.getOrientation(update);
							target = te.getWorldObj().getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);
						}
						routeInventory(te, config, target, dir);
					}
				}
			}

		}
	}

	public void routeInventory(TileEntity te, int config, TileEntity target, ForgeDirection dir) {
		if (target != null && dir != null && target instanceof IInventory) {
			if (config == 1) {
				InventoryHelper.extractItems(target, te, ForgeDirection.OPPOSITES[dir.ordinal()], dir.ordinal(), null);
			} else {
				for (int i = 0; i < slots.length; i++) {
					if (slots[i] != null && matchesFilters(slots[i], whitelist[update], blacklist[update])) {
						slots[i] = InventoryHelper.addItems(target, slots[i], ForgeDirection.OPPOSITES[dir.ordinal()], null);
					}
				}
			}
		}
	}

	public void addItemFilter(ItemFilter filter) {
		if (filter == null) {
			return;
		}
		this.getFilters().add(filter);
	}

	public void replaceItemFilter(int i, ItemFilter filter) {
		List<ItemFilter> filters = this.getFilters();
		if (filter == null || filters == null || filters.isEmpty() || (i > filters.size()) || i == -1) {
			return;
		}
		if (filters.get(i).getID() == filter.getID()) {
			if (!filter.getFilters().isEmpty()) {
				this.getFilters().set(i, filter);
			} else {
				this.getFilters().remove(i);
			}

		}
	}

	public void resetClientStackFilter() {
		this.clientStackFilter = new ItemStackFilter();
		if (editStack != -1) {
			List<ItemFilter> filters = this.getFilters();
			if (editStack < filters.size() && filters.get(editStack) != null) {
				this.clientStackFilter = (ItemStackFilter) filters.get(editStack);
			}
		}

	}

	public void resetClientOreFilter() {
		this.clientOreFilter = new OreDictionaryFilter();
		if (editOre != -1) {
			List<ItemFilter> filters = this.getFilters();
			if (editOre < filters.size() && filters.get(editOre) != null) {
				this.clientStackFilter = (ItemStackFilter) filters.get(editOre);
			}
		}
	}

	public void updateConnections(TileEntity te) {
		for (int i = 0; i < 6; i++) {
			int config = sideConfigs[i].getInt();
			if (config != 0) {
				List<BlockCoords> connections = CableHelper.getConnections(te, ForgeDirection.getOrientation(i));
				if (!connections.isEmpty()) {
					this.coords[i] = connections;
				}else{
					this.coords[i]=new ArrayList();
					this.coords[i].add(BlockCoords.translateCoords(new BlockCoords(te), ForgeDirection.getOrientation(i)));
				}
			}
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return sideConfigs[dir.ordinal()].getInt() != 0;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.PACKET || type == SyncType.DROP) {
			for (int l = 0; l < 6; l++) {

				whitelist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("white" + l, nbt, Logistics.itemFilters);
				blacklist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("black" + l, nbt, Logistics.itemFilters);
			}
		}
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			listType.readFromNBT(nbt, type);
			side.readFromNBT(nbt, type);
			filterPos.readFromNBT(nbt, type);
			NBTTagList sideList = nbt.getTagList("Sides", 10);
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = sideList.getCompoundTagAt(i);
				sideConfigs[i].readFromNBT(compound, type);
			}
		}
		if (type == SyncType.SPECIAL) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.readSyncedNBTObjectList("white" + l, nbt, Logistics.itemFilters, whitelist[l]);
				NBTHelper.readSyncedNBTObjectList("black" + l, nbt, Logistics.itemFilters, blacklist[l]);
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.PACKET || type == SyncType.DROP) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.writeNBTObjectList("white" + l, nbt, whitelist[l]);
				NBTHelper.writeNBTObjectList("black" + l, nbt, blacklist[l]);
			}
		}

		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			listType.writeToNBT(nbt, type);
			side.writeToNBT(nbt, type);
			filterPos.writeToNBT(nbt, type);
			NBTTagList sideList = new NBTTagList();
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = new NBTTagCompound();
				sideConfigs[i].writeToNBT(compound, type);
				sideList.appendTag(compound);
			}
			nbt.setTag("Sides", sideList);
		}
		if (type == SyncType.SPECIAL) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.writeSyncedNBTObjectList("white" + l, nbt, Logistics.itemFilters, whitelist[l], lastWhitelist[l]);
				NBTHelper.writeSyncedNBTObjectList("black" + l, nbt, Logistics.itemFilters, blacklist[l], lastBlacklist[l]);
			}
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return side != -1 ? (sideConfigs[side].getInt() == 1 && matchesFilters(item, whitelist[side], blacklist[side])) : true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return sideConfigs[side].getInt() == 2 && matchesFilters(item, whitelist[side], blacklist[side]);
	}

	public static boolean matchesFilters(ItemStack stack, List<ItemFilter> whitelist, List<ItemFilter> blacklist) {
		if (stack == null) {
			return false;
		}
		if (blacklist != null && !blacklist.isEmpty()) {
			for (ItemFilter filter : blacklist) {
				if (filter != null) {
					if (filter.matchesFilter(stack)) {
						return false;
					}

				}
			}
		}
		if (whitelist == null || whitelist.isEmpty()) {
			return true;
		}
		for (ItemFilter filter : whitelist) {
			if (filter != null) {
				if (filter.matchesFilter(stack)) {
					return true;
				}
			}
		}

		return false;
	}

	public static class Filter implements IInventoryFilter {

		public List<ItemFilter> whitelist, blacklist;

		public Filter(List<ItemFilter> whitelist, List<ItemFilter> blacklist) {
			this.whitelist = whitelist;
		}

		@Override
		public boolean matches(ItemStack stack) {
			return matchesFilters(stack, whitelist, blacklist);
		}

	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -2:
			writePacket(buf, 9);
			clientOreFilter.writeToBuf(buf);
			break;
		case -1:
			writePacket(buf, 9);
			clientStackFilter.writeToBuf(buf);
			break;
		case 0:
			if (side.getInt() + 1 < 6) {
				side.increaseBy(1);
			} else {
				side.setInt(0);
			}
			buf.writeInt(side.getInt());
			break;
		case 1:
			if (sideConfigs[side.getInt()].getInt() < 2) {
				sideConfigs[side.getInt()].increaseBy(1);
			} else {
				sideConfigs[side.getInt()].setInt(0);
			}
			buf.writeInt(sideConfigs[side.getInt()].getInt());
			break;
		case 2:
			if (listType.getInt() == 0) {
				listType.setInt(1);
			} else {
				listType.setInt(0);
			}
			buf.writeInt(listType.getInt());
			break;
		case 5:

			break;
		case 6:

			break;
		case 7:

			break;
		case 8:
			boolean clicked = false;
			if (listType.getInt() == 0 && clientClick != -1) {
				if (clientClick < whitelist[side.getInt()].size()) {
					if (whitelist[side.getInt()].get(clientClick) != null) {
						buf.writeInt(clientClick);
						clicked = true;
					}
				}
			} else if (listType.getInt() == 1 && clientClick != -1) {
				if (clientClick < blacklist[side.getInt()].size()) {
					if (blacklist[side.getInt()].get(clientClick) != null) {
						buf.writeInt(clientClick);
						clicked = true;
					}
				}
			}
			if (!clicked) {
				buf.writeInt(-1);
			}
			break;
		case 9:
			writePacket(buf, 8);
			buf.writeInt(editStack);
			buf.writeInt(editOre);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -2:
			readPacket(buf, 9);
			clientOreFilter = new OreDictionaryFilter();
			clientOreFilter.readFromBuf(buf);
			if (clientOreFilter != null) {
				if (editOre == -1 && clientOreFilter.oreDict != null && !clientOreFilter.oreDict.isEmpty())
					addItemFilter(clientOreFilter);
				else
					replaceItemFilter(editOre, clientOreFilter);
			}
			editOre = -1;
			break;
		case -1:
			readPacket(buf, 9);
			clientStackFilter = new ItemStackFilter();
			clientStackFilter.readFromBuf(buf);
			if (clientStackFilter != null) {
				if (editStack == -1 && clientStackFilter.filters[0] != null) {
					addItemFilter(clientStackFilter);
				} else {
					replaceItemFilter(editStack, clientStackFilter);
				}

			}
			editStack = -1;
			break;
		case 0:
			side.setInt(buf.readInt());
			filterPos.setInt(-1);
			break;
		case 1:
			sideConfigs[side.getInt()].setInt(buf.readInt());
			SonarCore.sendFullSyncAround(tile, 64);
			break;
		case 2:
			listType.setInt(buf.readInt());
			filterPos.setInt(-1);
			break;
		case 5:
			if (filterPos.getInt() != -1 && filterPos.getInt() != 0) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() - 1 < whitelist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() - 1);
						filterPos.setInt(filterPos.getInt() - 1);
					}
				} else {
					if (filterPos.getInt() - 1 < blacklist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() - 1);
						filterPos.setInt(filterPos.getInt() - 1);
					}
				}
			}
			break;
		case 6:
			if (filterPos.getInt() != -1) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() + 1 < whitelist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() + 1);
						filterPos.setInt(filterPos.getInt() + 1);
					}
				} else {
					if (filterPos.getInt() + 1 < blacklist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() + 1);
						filterPos.setInt(filterPos.getInt() + 1);
					}
				}
			}
			break;
		case 7:
			if (filterPos.getInt() != -1) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() < whitelist[side.getInt()].size())
						whitelist[side.getInt()].remove(filterPos.getInt());
				} else {
					if (filterPos.getInt() < blacklist[side.getInt()].size())
						blacklist[side.getInt()].remove(filterPos.getInt());
				}
				filterPos.setInt(-1);
			}
			break;
		case 8:
			filterPos.setInt(buf.readInt());
			break;
		case 9:
			readPacket(buf, 8);
			editStack = buf.readInt();
			editOre = buf.readInt();
			break;
		}
	}
}