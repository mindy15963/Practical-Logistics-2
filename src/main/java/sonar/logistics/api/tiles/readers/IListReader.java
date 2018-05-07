package sonar.logistics.api.tiles.readers;

import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.lists.types.AbstractChangeableList;
import sonar.logistics.api.networking.INetworkHandler;
import sonar.logistics.api.tiles.nodes.NodeConnection;
import sonar.logistics.api.viewers.ILogicListenable;

import java.util.List;
import java.util.Map;

public interface IListReader<T extends IInfo> extends ILogicListenable {

	AbstractChangeableList<T> sortMonitoredList(AbstractChangeableList<T> updateInfo, int channelID);	
	
	AbstractChangeableList<T> getViewableList(AbstractChangeableList<T> updateList, InfoUUID uuid, Map<NodeConnection, AbstractChangeableList<T>> channels, List<NodeConnection> usedChannels);	

	List<NodeConnection> getUsedChannels(Map<NodeConnection, AbstractChangeableList<T>> channels);
	
	List<INetworkHandler> getValidHandlers();
}
