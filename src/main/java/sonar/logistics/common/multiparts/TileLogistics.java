package sonar.logistics.common.multiparts;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.TileSonarMultipart;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.PL2;
import sonar.logistics.PL2Multiparts;
import sonar.logistics.api.cabling.INetworkTile;
import sonar.logistics.api.lists.types.InfoChangeableList;
import sonar.logistics.api.networks.EmptyLogisticsNetwork;
import sonar.logistics.api.networks.ILogisticsNetwork;
import sonar.logistics.api.networks.INetworkListener;
import sonar.logistics.api.operator.IOperatorProvider;
import sonar.logistics.api.states.TileMessage;
import sonar.logistics.api.utils.CacheType;
import sonar.logistics.api.viewers.ILogicListenable;
import sonar.logistics.common.multiparts.readers.TileInfoReader;
import sonar.logistics.info.types.MonitoredBlockCoords;
import sonar.logistics.networking.info.InfoHelper;
import sonar.logistics.packets.PacketChannels;
import sonar.logistics.packets.sync.SyncTileMessages;

public abstract class TileLogistics extends TileSonarMultipart implements INetworkTile, INetworkListener, IOperatorProvider {

	public static final TileMessage[] defaultValidStates = new TileMessage[] { TileMessage.NO_NETWORK };
	public ILogisticsNetwork network = EmptyLogisticsNetwork.INSTANCE;
	private SyncTagType.INT identity = (INT) new SyncTagType.INT("identity").setDefault((int) -1);
	public SyncTagType.INT networkID = (INT) new SyncTagType.INT(0).setDefault(-1);
	public SyncTileMessages states = new SyncTileMessages(this, 101);

	{
		syncList.addParts(networkID, identity, states);
		states.markAllMessages(true);
	}

	public TileLogistics() {
		super();
	}

	public abstract EnumFacing getCableFace();

	public PL2Multiparts getMultipart() {
		if (this.getBlockType() instanceof BlockLogistics) {
			return ((BlockLogistics) getBlockType()).getMultipart();
		}
		return null;
	}

	public void sendNetworkCoordMap(EntityPlayer player) {
		if (isClient() || !network.isValid() || getNetworkID() == -1) {
			return;
		}
		InfoChangeableList<MonitoredBlockCoords> coords = network.createConnectionsList(CacheType.ALL);
		NBTTagCompound coordTag = InfoHelper.writeMonitoredList(new NBTTagCompound(), coords, SyncType.DEFAULT_SYNC);
		if (!coordTag.hasNoTags()) {
			PL2.network.sendTo(new PacketChannels(getNetworkID(), coordTag), (EntityPlayerMP) player);
		}
	}

	//// ILogicTile \\\\

	public int getIdentity() {
		if (identity.getObject() == -1 && this.isServer()) {
			identity.setObject(PL2.getServerManager().getNextIdentity());
		}
		return identity.getObject();
	}

	@Override
	public boolean isValid() {
		return !tileEntityInvalid;
	}

	@Override
	public void onFirstTick() {
		super.onFirstTick();
		if (this instanceof ILogicListenable)
			PL2.getInfoManager(world.isRemote).addIdentityTile((ILogicListenable) this);
	}

	public void invalidate() {
		super.invalidate();
		if (this instanceof ILogicListenable)
			PL2.getInfoManager(world.isRemote).removeIdentityTile((ILogicListenable) this);
	}

	@Override
	public void onNetworkConnect(ILogisticsNetwork network) {
		if (!this.network.isValid() || networkID.getObject() != network.getNetworkID()) {
			this.network = network;
			this.networkID.setObject(network.getNetworkID());
			states.markTileMessage(TileMessage.NO_NETWORK, false);
		}
	}

	@Override
	public void onNetworkDisconnect(ILogisticsNetwork network) {
		if (networkID.getObject() == network.getNetworkID()) {
			this.network = EmptyLogisticsNetwork.INSTANCE;
			this.networkID.setObject(-1);
			states.markTileMessage(TileMessage.NO_NETWORK, true);
		} else if (networkID.getObject() != -1) {
			PL2.logger.info("%s : attempted to disconnect from the wrong network with ID: %s expected %s", this, network.getNetworkID(), networkID.getObject());
		}
	}

	//// LISTENERS \\\\

	public void onListenerAdded(ListenerTally<PlayerListener> tally) {}

	public void onListenerRemoved(ListenerTally<PlayerListener> tally) {}

	public void onSubListenableAdded(ISonarListenable<PlayerListener> listen) {}

	public void onSubListenableRemoved(ISonarListenable<PlayerListener> listen) {}

	public ILogisticsNetwork getNetwork() {
		return network;
	}

	public int getNetworkID() {
		return networkID.getObject();
	}

	//// IOperatorProvider \\\\

	public void updateOperatorInfo() {
		this.requestSyncPacket();
	}

	public void addInfo(List<String> info) {
		PL2Multiparts multipart = getMultipart();
		if (multipart != null)
			info.add(TextFormatting.UNDERLINE + multipart.getDisplayName());
		info.add("Network ID: " + networkID.getObject());
		info.add("Has channels: " + (this instanceof TileInfoReader));
		info.add("IDENTITY: " + identity.getObject());
	}

	@Override
	public TileMessage[] getValidMessages() {
		return defaultValidStates;
	}
}
