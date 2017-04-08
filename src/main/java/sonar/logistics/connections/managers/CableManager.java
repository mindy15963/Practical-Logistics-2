package sonar.logistics.connections.managers;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.utils.Pair;
import sonar.logistics.api.cabling.ConnectableType;
import sonar.logistics.api.cabling.IDataCable;
import sonar.logistics.api.connecting.ILogisticsNetwork;
import sonar.logistics.common.multiparts.DataCablePart;
import sonar.logistics.helpers.CableHelper;

public class CableManager extends AbstractConnectionManager<IDataCable> {

	@Override
	public Pair<ConnectableType, Integer> getConnectionType(IDataCable source, World world, BlockPos pos, EnumFacing dir, ConnectableType cableType) {
		return CableHelper.getConnectionType(source, world, pos, dir, cableType);
	}

	public ILogisticsNetwork addCable(IDataCable cable) {
		return NetworkManager().getOrCreateNetwork(addConnection(cable));
	}

	@Override
	public void onNetworksConnected(int newID, int oldID) {
		NetworkManager().connectNetworks(oldID, newID);		
	}

	@Override
	public void onConnectionAdded(int registryID, IDataCable added) {}

	@Override
	public void onConnectionRemoved(int registryID, IDataCable added) {}

}
