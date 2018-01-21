package sonar.logistics.api.tiles.readers;

import java.util.List;
import java.util.Map;

import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.lists.types.AbstractChangeableList;
import sonar.logistics.api.networks.INetworkHandler;
import sonar.logistics.api.tiles.INetworkTile;
import sonar.logistics.api.tiles.nodes.NodeConnection;
import sonar.logistics.api.viewers.ILogicListenable;

public interface IListReader<T extends IInfo> extends ILogicListenable, INetworkTile {

	AbstractChangeableList<T> sortMonitoredList(AbstractChangeableList<T> updateInfo, int channelID);	
	
	AbstractChangeableList<T> getViewableList(AbstractChangeableList<T> updateList, InfoUUID uuid, Map<NodeConnection, AbstractChangeableList<T>> channels, List<NodeConnection> usedChannels);	

	List<INetworkHandler> getValidHandlers();
}