package sonar.logistics.client.gui.textedit;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class SonarStyling implements INBTSyncable {

	public int rgb = -1;
	public int bgd = -1;
	public boolean bold = false;
	public boolean italic = false;
	public boolean underlined = false;
	public boolean strikethrough = false;
	public boolean obfuscated = false;

	public SonarStyling() {}

	public SonarStyling copy() {
		SonarStyling style = new SonarStyling();
		style.rgb = rgb;
		style.bgd = bgd;
		style.bold = bold;
		style.italic = italic;
		style.underlined = underlined;
		style.strikethrough = strikethrough;
		style.obfuscated = obfuscated;
		return style;
	}

	public void setFontColour(int colour) {
		rgb = colour;
	}

	public int getFontColour() {
		return rgb;
	}

	public void setBackgroundColour(int colour) {
		bgd = colour;
	}

	public int getBackgroundColour() {
		return bgd;
	}

	public String getTextFormattingString() {
		StringBuilder s = new StringBuilder();
		if (bold) {
			s.append(TextFormatting.BOLD);
		}
		if (italic) {
			s.append(TextFormatting.ITALIC);
		}
		if (underlined) {
			s.append(TextFormatting.UNDERLINE);
		}
		if (strikethrough) {
			s.append(TextFormatting.STRIKETHROUGH);
		}
		if (obfuscated) {
			s.append(TextFormatting.OBFUSCATED);
		}
		return s.toString();
	}

	public void toggleSpecialFormatting(List<TextFormatting> formatting, boolean enable) {
		for (TextFormatting format : formatting) {
			if (format.isFancyStyling()) {
				switch (format) {
				case BOLD:
					bold = enable;
					break;
				case ITALIC:
					italic = enable;
					break;
				case UNDERLINE:
					underlined = enable;
					break;
				case STRIKETHROUGH:
					strikethrough = enable;
					break;
				case OBFUSCATED:
					obfuscated = enable;
					break;
				default:
					break;

				}
			}
		}

	}

	public boolean needsSave() {
		return rgb != -1 || bgd != -1 || bold || italic || underlined || strikethrough || obfuscated;
	}

	public boolean matching(SonarStyling ss) {
		int[] colour = this.getColourArray();
		int[] compareColour = ss.getColourArray();
		for (int i = 0; i < colour.length; i++) {
			if (colour[i] != compareColour[i]) {
				return false;
			}
		}

		byte[] format = this.getByteFormatting();
		byte[] compareFormat = ss.getByteFormatting();
		for (int f = 0; f < format.length; f++) {
			if (format[f] != compareFormat[f]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (!nbt.getBoolean("def")) {
			fromColourArray(nbt.getIntArray("c"));
			fromByteFormatting(nbt.getByteArray("f"));
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		boolean save = needsSave();
		nbt.setBoolean("def", !save);
		if (save) {
			nbt.setIntArray("c", getColourArray());
			nbt.setByteArray("f", getByteFormatting());
		}
		return nbt;
	}

	public int[] getColourArray() {
		int[] colours = new int[2];
		colours[0] = rgb;
		colours[1] = bgd;
		return colours;
	}

	public void fromColourArray(int[] colours) {
		rgb = colours[0];
		bgd = colours[1];
	}

	public byte[] getByteFormatting() {
		byte[] format = new byte[5];
		format[0] = (byte) (bold ? 1 : 0);
		format[1] = (byte) (italic ? 1 : 0);
		format[2] = (byte) (underlined ? 1 : 0);
		format[3] = (byte) (strikethrough ? 1 : 0);
		format[4] = (byte) (obfuscated ? 1 : 0);
		return format;
	}

	public void fromByteFormatting(byte[] format) {
		if (format.length != 5) {
			return;
		}
		bold = format[0] == 1 ? true : false;
		italic = format[1] == 1 ? true : false;
		underlined = format[2] == 1 ? true : false;
		strikethrough = format[3] == 1 ? true : false;
		obfuscated = format[4] == 1 ? true : false;
	}

}