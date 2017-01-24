package sonar.logistics.api.info.monitor;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.utils.IUUIDIdentity;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.viewers.IViewersList;
import sonar.logistics.api.viewers.MonitorTally;

public interface ILogicViewable extends ILogicTile, IUUIDIdentity {

	public IViewersList getViewersList();
	
	public void onViewerAdded(EntityPlayer player, List<MonitorTally> arrayList);
	
	public void onViewerRemoved(EntityPlayer player, List<MonitorTally> arrayList);
				
}
