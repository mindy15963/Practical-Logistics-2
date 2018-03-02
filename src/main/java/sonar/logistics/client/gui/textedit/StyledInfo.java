package sonar.logistics.client.gui.textedit;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.SonarHelper;

public class StyledInfo implements IStyledString, INBTSyncable {

	public String string;
	public SonarStyling style;

	private String formattingString;

	public StyledInfo() {}

	public StyledInfo(String string) {
		this(string, new SonarStyling());
	}

	public StyledInfo(String string, SonarStyling style) {
		this.string = string;
		this.style = style;
	}

	public String setUnformattedString(String s) {
		return string = s;
	}

	public String getUnformattedString() {
		return string;
	}

	public String getTextFormattingStyle() {
		return style.getTextFormattingString();
	}

	@Override
	public String getFormattedString() {
		return getTextFormattingStyle() + getUnformattedString();
	}

	@Override
	public SonarStyling setStyle(SonarStyling f) {
		return style = f;
	}

	@Override
	public SonarStyling getStyle() {
		return style;
	}

	@Override
	public String toString() {
		return getFormattedString();
	}

	@Override
	public int getStringLength() {
		return getUnformattedString().length();
	}

	@Override
	public int getStringWidth() {
		return RenderHelper.fontRenderer.getStringWidth(getFormattedString());
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		string = nbt.getString("s");
		(style = new SonarStyling()).readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setString("s", string);
		style.writeData(nbt, type);
		return nbt;
	}

	@Override
	public boolean canCombine(IStyledString ss) {
		return ss instanceof StyledInfo && ss.getStyle().matching(style);
	}

	public void combine(IStyledString ss) {
		int previousLength = getStringLength();
		this.setUnformattedString(this.getUnformattedString() + ss.getUnformattedString());
	}
}
