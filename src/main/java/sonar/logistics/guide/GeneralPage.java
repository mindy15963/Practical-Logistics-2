package sonar.logistics.guide;

import java.util.ArrayList;

import sonar.core.helpers.FontHelper;
import sonar.logistics.client.gui.GuiGuide;
import sonar.logistics.guide.elements.ElementInfo;

public class GeneralPage extends BaseInfoPage {
	public String displayKey;
	public String[] descKey;

	public GeneralPage(int pageID, String displayKey, String... descKey) {
		super(pageID);
		this.displayKey = displayKey;
		this.descKey = descKey;
	}

	@Override
	public String getDisplayName() {
		return FontHelper.translate(displayKey);
	}

	@Override
	public ArrayList<ElementInfo> getPageInfo(GuiGuide gui, ArrayList<ElementInfo> pageInfo) {
		for (String desc : descKey)
			pageInfo.add(new ElementInfo(desc, new String[0]));
		return pageInfo;
	}
}