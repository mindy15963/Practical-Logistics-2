package sonar.logistics.connections.monitoring;

import sonar.logistics.PL2Constants;
import sonar.logistics.api.asm.TileMonitorHandler;
import sonar.logistics.api.connecting.ILogisticsNetwork;
import sonar.logistics.api.info.ITileMonitorHandler;
import sonar.logistics.api.nodes.BlockConnection;

@TileMonitorHandler(handlerID = ChannelMonitorHandler.id, modid = PL2Constants.MODID)
public class ChannelMonitorHandler extends LogicMonitorHandler<MonitoredBlockCoords> implements ITileMonitorHandler<MonitoredBlockCoords> {

	public static final String id = "channels";
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredBlockCoords> updateInfo(ILogisticsNetwork network, MonitoredList<MonitoredBlockCoords> info, BlockConnection connection) {
		
		return info;
	}

}
