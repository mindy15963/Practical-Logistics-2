package sonar.logistics.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.FontHelper;
import sonar.core.network.FlexibleGuiHandler;
import sonar.logistics.api.info.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.gui.GuiInventoryReader.FilterButton;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.common.multiparts.InfoReaderPart;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.info.types.LogicInfo;

public class GuiInfoReader extends GuiSelectionList<LogicInfo> {

	public InfoReaderPart part;
	public EntityPlayer player;

	public GuiInfoReader(EntityPlayer player, InfoReaderPart tile) {
		super(new ContainerInfoReader(player, tile), tile);
		this.player = player;
		this.part = tile;
		this.xSize = 182 + 66;
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new LogisticsButton(this, 1, guiLeft + 9, guiTop + 7, 32, 96 + 16, "Channels"));
	}

	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button != null) {
			if (button.id == 1) {
				FlexibleGuiHandler.changeGui(part, 1, 0, player.getEntityWorld(), player);
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("item.InfoReader.name"), xSize, 6, LogisticsColours.white_text);
		FontHelper.textCentre(String.format("Select the data you wish to monitor"), xSize, 18, LogisticsColours.grey_text);
	}

	public void setInfo() {
		if (part.getChannels(0).isEmpty()) {
			infoList = MonitoredList.newMonitoredList(part.getNetworkID());
		} else {
			infoList = part.getMonitoredList().cloneInfo();
		}
	}

	@Override
	public void selectionPressed(GuiButton button, int buttonID, LogicInfo info) {
		if (info.isValid() && !info.isHeader()) {
			part.selectedInfo.setInfo(info);
			part.sendByteBufPacket(buttonID == 0 ? -9 : -10);
		}
	}

	@Override
	public boolean isCategoryHeader(LogicInfo info) {
		return info.isHeader();
	}

	@Override
	public boolean isSelectedInfo(LogicInfo info) {
		if (!info.isValid() || info.isHeader()) {
			return false;
		}
		ArrayList<IMonitorInfo> selectedInfo = part.getSelectedInfo();
		for (IMonitorInfo selected : selectedInfo) {
			if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo((LogicInfo) selected)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPairedInfo(LogicInfo info) {
		if (!info.isValid() || info.isHeader()) {
			return false;
		}
		ArrayList<IMonitorInfo> pairedInfo = part.getPairedInfo();
		for (IMonitorInfo selected : pairedInfo) {
			if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo((LogicInfo) selected)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void renderInfo(LogicInfo info, int yPos) {
		InfoRenderer.renderMonitorInfoInGUI(info, yPos + 1, LogisticsColours.white_text.getRGB());
	}

	@Override
	public int getColour(int i, int type) {
		IMonitorInfo info = (IMonitorInfo) infoList.get(i + start);
		if (info == null || info.isHeader()) {
			return LogisticsColours.layers[1].getRGB();
		}
		ArrayList<IMonitorInfo> selectedInfo = type == 0 ? part.getSelectedInfo() : part.getPairedInfo();
		int pos = 0;
		for (IMonitorInfo selected : selectedInfo) {
			if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo(selected)) {
				return LogisticsColours.infoColours[pos].getRGB();
			}
			pos++;
		}
		return LogisticsColours.layers[1].getRGB();
	}

}
