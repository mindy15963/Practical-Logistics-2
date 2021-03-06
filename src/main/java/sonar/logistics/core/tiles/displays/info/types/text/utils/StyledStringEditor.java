package sonar.logistics.core.tiles.displays.info.types.text.utils;

import com.google.common.collect.Lists;
import sonar.logistics.core.tiles.displays.info.types.text.StyledTextElement;
import sonar.logistics.core.tiles.displays.info.types.text.gui.CursorPosition;
import sonar.logistics.core.tiles.displays.info.types.text.styling.IStyledString;
import sonar.logistics.core.tiles.displays.info.types.text.styling.StyledStringLine;

import java.util.List;

public class StyledStringEditor {
	
	public static void addStyledStrings(StyledTextElement text, CursorPosition cursorPosition, List<IStyledString> strings) {
		if (cursorPosition.validPosition()) {
			StyledStringLine newLine = new StyledStringLine(text);
			if (cursorPosition.x != 0) {
				TextSelection before = new TextSelection(0, cursorPosition.x, cursorPosition.y, cursorPosition.y);
				StyledStringFormatter.formatTextSelections(text, Lists.newArrayList(before), (line, ss) -> {
					newLine.addWithCombine(ss);
					return ss;
				});
			}
			strings.forEach(newLine::addWithCombine);
			if (cursorPosition.x != text.getLineLength(cursorPosition.y)) {
				TextSelection after = new TextSelection(cursorPosition.x, Integer.MAX_VALUE, cursorPosition.y, cursorPosition.y);
				StyledStringFormatter.formatTextSelections(text, Lists.newArrayList(after), (line, ss) -> {
					newLine.addWithCombine(ss);
					return ss;
				});
			}
			text.setLine(cursorPosition.y, newLine);
		}
	}

	public static void addStyledLines(StyledTextElement text, CursorPosition cursorPosition, List<StyledStringLine> lines, boolean combineFirst) {
		if (cursorPosition.validPosition()) {
			int yPos = cursorPosition.y;
			final StyledStringLine beforeLine = new StyledStringLine(text);
			boolean combinedFirst = cursorPosition.x == 0 || !combineFirst;
			if (cursorPosition.x != 0) {
				TextSelection before = new TextSelection(0, cursorPosition.x, cursorPosition.y, cursorPosition.y);
				StyledStringFormatter.formatTextSelections(text, Lists.newArrayList(before), (line, ss) -> {
					beforeLine.addWithCombine(ss);
					return ss;
				});
				text.addNewLine(yPos, beforeLine);
				yPos++;
			}
			StyledStringLine afterLines = new StyledStringLine(text);
			if (cursorPosition.x != text.getLineLength(cursorPosition.y)) {
				TextSelection after = new TextSelection(cursorPosition.x, Integer.MAX_VALUE, cursorPosition.y, cursorPosition.y);
				StyledStringFormatter.formatTextSelections(text, Lists.newArrayList(after), (line, ss) -> {
					afterLines.addWithCombine(ss);
					return ss;
				});
			}

			for (StyledStringLine line : lines) {
				if (!combinedFirst) {
					line.getStrings().forEach(beforeLine::addWithCombine);
					combinedFirst = true;
				} else {
					text.addNewLine(yPos, line);
					yPos++;
				}
			}
			if (!afterLines.getStrings().isEmpty()) {
				text.addNewLine(yPos, afterLines);
				yPos++;
			}

		}
	}
}
