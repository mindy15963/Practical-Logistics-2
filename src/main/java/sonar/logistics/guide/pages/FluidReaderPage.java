package sonar.logistics.guide.pages;

import net.minecraft.item.ItemStack;
import sonar.logistics.PL2Blocks;
import sonar.logistics.client.gui.GuiGuide;
import sonar.logistics.guide.BaseItemPage;
import sonar.logistics.guide.IGuidePage;
import sonar.logistics.guide.elements.ElementInfo;

import java.util.List;

public class FluidReaderPage extends BaseItemPage implements IGuidePage {

	public FluidReaderPage(int pageID) {
		super(pageID, new ItemStack(PL2Blocks.fluid_reader));
	}

	public List<ElementInfo> getPageInfo(GuiGuide gui, List<ElementInfo> pageInfo) {
		pageInfo.add(new ElementInfo("guide." + unlocalizedName.substring(5) + ".name", new String[0]));
		pageInfo.add(new ElementInfo("guide." + "ChannelsSTART" + ".name", new String[0]));
		pageInfo.add(new ElementInfo("guide." + "ChannelsUNLIMITED" + ".name", new String[0]));
		pageInfo.add(new ElementInfo("guide." + "ChannelsEND" + ".name", new String[0]));		
		return pageInfo;
	}

}
