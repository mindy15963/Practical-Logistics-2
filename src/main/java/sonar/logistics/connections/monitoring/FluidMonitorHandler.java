package sonar.logistics.connections.monitoring;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.logistics.PL2ASMLoader;
import sonar.logistics.PL2Constants;
import sonar.logistics.api.asm.TileMonitorHandler;
import sonar.logistics.api.connecting.ILogisticsNetwork;
import sonar.logistics.api.info.ITileMonitorHandler;
import sonar.logistics.api.nodes.BlockConnection;

@TileMonitorHandler(handlerID = FluidMonitorHandler.id, modid = PL2Constants.MODID)
public class FluidMonitorHandler extends LogicMonitorHandler<MonitoredFluidStack> implements ITileMonitorHandler<MonitoredFluidStack> {

	public static final String id = "fluid";
	public static FluidMonitorHandler handler;

	public static FluidMonitorHandler instance() {
		if (handler == null)
			handler = (FluidMonitorHandler) PL2ASMLoader.tileMonitorHandlers.get(id);
		return handler;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredFluidStack> updateInfo(ILogisticsNetwork network, MonitoredList<MonitoredFluidStack> previousList, BlockConnection connection) {
		MonitoredList<MonitoredFluidStack> list = MonitoredList.<MonitoredFluidStack>newMonitoredList(network.getNetworkID());
		List<ISonarFluidHandler> providers = SonarCore.fluidHandlers;
		for (ISonarFluidHandler provider : providers) {
			TileEntity fluidTile = connection.coords.getTileEntity();
			if (fluidTile != null && provider.canHandleFluids(fluidTile, connection.face)) {
				List<StoredFluidStack> info = new ArrayList();
				StorageSize size = provider.getFluids(info, fluidTile, connection.face);
				list.sizing.add(size);
				for (StoredFluidStack fluid : info) {
					list.addInfoToList(new MonitoredFluidStack(fluid, network.getNetworkID()), previousList);
				}
				break;
			}
		}
		return list;
	}

}
