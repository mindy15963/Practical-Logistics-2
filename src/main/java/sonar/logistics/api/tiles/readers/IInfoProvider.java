package sonar.logistics.api.tiles.readers;

import sonar.logistics.api.info.IInfo;
import sonar.logistics.api.networks.ILogisticsNetwork;
import sonar.logistics.api.viewers.ILogicListenable;

public interface IInfoProvider extends ILogicListenable {
	
	public IInfo getMonitorInfo(int pos);

	public int getMaxInfo();

	public ILogisticsNetwork getNetwork();
}