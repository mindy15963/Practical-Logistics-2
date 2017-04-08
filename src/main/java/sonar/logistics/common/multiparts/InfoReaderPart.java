package sonar.logistics.common.multiparts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.PL2Items;
import sonar.logistics.PL2Multiparts;
import sonar.logistics.PL2Translate;
import sonar.logistics.api.cabling.ChannelType;
import sonar.logistics.api.info.IProvidableInfo;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.generic.GuiChannelSelection;
import sonar.logistics.common.containers.ContainerChannelSelection;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.common.multiparts.generic.LogisticsReader;
import sonar.logistics.connections.monitoring.InfoMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

public class InfoReaderPart extends LogisticsReader<IProvidableInfo> implements IByteBufTile {

	public InfoReaderPart() {
		super(InfoMonitorHandler.id);
	}

	//// ILogicReader \\\\
	@Override
	public MonitoredList<IProvidableInfo> sortMonitoredList(MonitoredList<IProvidableInfo> updateInfo, int channelID) {
		updateInfo.setInfo(InfoHelper.sortInfoList(updateInfo));
		return updateInfo;
	}

	//// IChannelledTile \\\\
	@Override
	public ChannelType channelType() {
		return ChannelType.SINGLE;
	}

	//// GUI \\\\
	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerInfoReader(player, this);
		case 1:
			return new ContainerChannelSelection(this);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiInfoReader(player, this);
		case 1:
			return new GuiChannelSelection(player, this, 0);
		}
		return null;
	}

	@Override
	public PL2Multiparts getMultipart() {
		return PL2Multiparts.INFO_READER;
	}

}
