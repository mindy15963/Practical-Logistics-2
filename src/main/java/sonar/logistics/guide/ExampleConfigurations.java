package sonar.logistics.guide;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.common.block.properties.SonarProperties;
import sonar.logistics.PL2Blocks;
import sonar.logistics.PL2Items;
import sonar.logistics.PL2Properties;
import sonar.logistics.api.cabling.CableRenderType;
import sonar.logistics.api.displays.DisplayGSI;
import sonar.logistics.api.displays.elements.types.NetworkItemElement;
import sonar.logistics.api.displays.elements.types.NetworkItemGridElement;
import sonar.logistics.api.displays.elements.types.ProgressBarElement;
import sonar.logistics.api.displays.storage.DisplayElementContainer;
import sonar.logistics.api.displays.tiles.DisplayConnections;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.register.RegistryType;
import sonar.logistics.api.lists.types.ItemChangeableList;
import sonar.logistics.common.multiparts.cables.TileDataCable;
import sonar.logistics.common.multiparts.displays.BlockLargeDisplay;
import sonar.logistics.common.multiparts.displays.TileDisplayScreen;
import sonar.logistics.common.multiparts.displays.TileLargeDisplayScreen;
import sonar.logistics.common.multiparts.nodes.TileNode;
import sonar.logistics.common.multiparts.readers.TileInfoReader;
import sonar.logistics.common.multiparts.readers.TileInventoryReader;
import sonar.logistics.info.types.LogicInfo;
import sonar.logistics.info.types.LogicInfoList;
import sonar.logistics.info.types.MonitoredItemStack;
import sonar.logistics.info.types.ProgressInfo;

import java.util.List;

public class ExampleConfigurations {

	public static class FurnaceProgress extends Logistics3DRenderer {
		public static final FurnaceProgress instance2 = new FurnaceProgress();

		public FurnaceProgress() {
			super(4);
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.node.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.DOWN), new TileNode());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.info_reader.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH), new TileInfoReader());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.data_cable.getDefaultState().withProperty(PL2Properties.DOWN, CableRenderType.INTERNAL), new TileDataCable());
			TileDisplayScreen screen = new TileDisplayScreen();
			screen.container = new DisplayGSI(screen, screen.getWorld(), screen.getInfoContainerID());
			LogicInfo info1 = LogicInfo.buildDirectInfo("TileEntityFurnace.cookTime", RegistryType.TILE, 100);
			LogicInfo info2 = LogicInfo.buildDirectInfo("TileEntityFurnace.totalCookTime", RegistryType.TILE, 200);
			double[] displayScaling = new double[] { screen.getDisplayType().width, screen.getDisplayType().height, 1 };
			screen.getGSI().currentScaling = displayScaling;
			
			DisplayElementContainer container = new DisplayElementContainer(screen.getGSI(), new double[] { 0, 0, 0 }, displayScaling, 1, 1);
			ProgressInfo progressInfo = new ProgressInfo(info1, info2);
			container.getElements().addElement(new ProgressBarElement(){
				public void render() {
					info = progressInfo;
					render((ProgressInfo) info);
				}
			});
			addDisplayContainer(new BlockPos(0, 0, 0), container);			
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.display_screen.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH).withProperty(SonarProperties.ROTATION, EnumFacing.NORTH), screen);
			addBlock(new BlockPos(0, -1, 0), Blocks.LIT_FURNACE.getDefaultState());
		}
	}

	public static class InventoryExample extends Logistics3DRenderer {

		public static final InventoryExample instance = new InventoryExample();

		public InventoryExample() {
			super(4);
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.node.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.DOWN), new TileNode());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.inventory_reader.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH), new TileInventoryReader());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.data_cable.getDefaultState().withProperty(PL2Properties.DOWN, CableRenderType.INTERNAL), new TileDataCable());

			TileDisplayScreen screen = new TileDisplayScreen();
			screen.container = new DisplayGSI(screen, screen.getWorld(), screen.getInfoContainerID());
			double[] displayScaling = new double[] { screen.getDisplayType().width, screen.getDisplayType().height, 1 };
			screen.getGSI().currentScaling = displayScaling;
			DisplayElementContainer container = new DisplayElementContainer(screen.getGSI(), new double[] { 0, 0, 0 }, displayScaling, 1, 1);	
			MonitoredItemStack itemInfo = new MonitoredItemStack(new StoredItemStack(new ItemStack(Blocks.COBBLESTONE), 256), -1);
			container.getElements().addElement(new NetworkItemElement(){
				public void render() {
					info = itemInfo;
					render((MonitoredItemStack) info);
				}
			});
			addDisplayContainer(new BlockPos(0, 0, 0), container);	
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.display_screen.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH), screen);
			addBlock(new BlockPos(0, -1, 0), Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH), new TileEntityChest());
		}
	}

	public static class MultipleInventory extends Logistics3DRenderer {

		public MultipleInventory() {
			super(16);
			TileLargeDisplayScreen screen1 = new TileLargeDisplayScreen();
			
			double connected_width = 2;
			double connected_height = 1;
			double max = Math.min(connected_height + 1.3, connected_width + 1);
			double[] screenScale = new double[] { screen1.getDisplayType().width + connected_width, screen1.getDisplayType().height + connected_height, max / 100 };
			DisplayGSI fakeGSI = new DisplayGSI(screen1, null, 1);
			fakeGSI.currentScaling = screenScale;
			DisplayElementContainer container = new DisplayElementContainer(fakeGSI, new double[] { 0, 0, 0 }, screenScale, 1, 1);			
			
			ItemChangeableList list = new ItemChangeableList();
			list.add(new StoredItemStack(new ItemStack(PL2Items.sapphire), 512));
			list.add(new StoredItemStack(new ItemStack(PL2Items.stone_plate), 256));
			list.add(new StoredItemStack(new ItemStack(Blocks.COBBLESTONE), 256));
			list.add(new StoredItemStack(new ItemStack(Blocks.SAND), 256));
			list.add(new StoredItemStack(new ItemStack(Blocks.LOG, 1, 0), 128));
			list.add(new StoredItemStack(new ItemStack(Blocks.LOG, 1, 1), 128));
			list.add(new StoredItemStack(new ItemStack(Blocks.LOG, 1, 2), 128));
			list.add(new StoredItemStack(new ItemStack(Blocks.LOG, 1, 3), 128));
			list.add(new StoredItemStack(new ItemStack(Blocks.PLANKS, 1, 0), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.PLANKS, 1, 1), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.PLANKS, 1, 2), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.PLANKS, 1, 3), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.DIRT), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.GLOWSTONE), 64));
			list.add(new StoredItemStack(new ItemStack(Blocks.IRON_ORE), 64));
			list.add(new StoredItemStack(new ItemStack(PL2Blocks.data_cable), 16));
			list.add(new StoredItemStack(new ItemStack(PL2Blocks.info_reader), 4));
			list.add(new StoredItemStack(new ItemStack(PL2Items.operator), 1));

			LogicInfoList itemList = new LogicInfoList() {
				{
					infoID.setObject(MonitoredItemStack.id);
					listChanged = false;
				}

				public List<MonitoredItemStack> getCachedList(InfoUUID id) {
					return list.createSaveableList();
				}
			};
			container.getElements().addElement(new NetworkItemGridElement(){
				public void render() {
					info = itemList;
					render((LogicInfoList) info);
				}
			});
			

			addDisplayContainer(new BlockPos(1, 0, 0), container);	
			
			addBlock(new BlockPos(1, 0, 0), PL2Blocks.node.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.DOWN), new TileNode());
			addBlock(new BlockPos(1, 0, 0), PL2Blocks.large_display_screen.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH).withProperty(BlockLargeDisplay.TYPE, DisplayConnections.ONE_E), screen1);
			addBlock(new BlockPos(1, 0, 0), PL2Blocks.data_cable.getDefaultState().withProperty(PL2Properties.DOWN, CableRenderType.INTERNAL).withProperty(PL2Properties.WEST, CableRenderType.CABLE).withProperty(PL2Properties.NORTH, CableRenderType.INTERNAL), new TileDataCable());

			addBlock(new BlockPos(-1, 0, 0), PL2Blocks.node.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.DOWN), new TileNode());
			addBlock(new BlockPos(-1, 0, 0), PL2Blocks.large_display_screen.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH).withProperty(BlockLargeDisplay.TYPE, DisplayConnections.ONE_W), new TileLargeDisplayScreen());
			addBlock(new BlockPos(-1, 0, 0), PL2Blocks.data_cable.getDefaultState().withProperty(PL2Properties.DOWN, CableRenderType.INTERNAL).withProperty(PL2Properties.EAST, CableRenderType.CABLE).withProperty(PL2Properties.NORTH, CableRenderType.INTERNAL), new TileDataCable());

			addBlock(new BlockPos(0, 0, 0), PL2Blocks.inventory_reader.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH), new TileInventoryReader());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.node.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.DOWN), new TileNode());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.large_display_screen.getDefaultState().withProperty(SonarProperties.ORIENTATION, EnumFacing.NORTH).withProperty(BlockLargeDisplay.TYPE, DisplayConnections.OPPOSITE_2), new TileLargeDisplayScreen());
			addBlock(new BlockPos(0, 0, 0), PL2Blocks.data_cable.getDefaultState().withProperty(PL2Properties.DOWN, CableRenderType.INTERNAL).withProperty(PL2Properties.EAST, CableRenderType.CABLE).withProperty(PL2Properties.WEST, CableRenderType.CABLE).withProperty(PL2Properties.NORTH, CableRenderType.INTERNAL), new TileDataCable());

			IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
			addBlock(new BlockPos(0, -1, 0), chestState, new TileEntityChest());
			addBlock(new BlockPos(1, -1, 0), chestState, new TileEntityChest());
			addBlock(new BlockPos(-1, -1, 0), chestState, new TileEntityChest());

		}

	}

}
