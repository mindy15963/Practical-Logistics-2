package sonar.logistics.core.items.guide.pages.pages;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.core.items.guide.GuiGuide;
import sonar.logistics.core.items.guide.GuidePageHelper;
import sonar.logistics.core.items.guide.pages.elements.ElementInfo;
import sonar.logistics.core.items.guide.pages.elements.ElementInfoFormatted;
import sonar.logistics.core.items.guide.pages.elements.ElementLink;
import sonar.logistics.core.items.guide.pages.elements.IGuidePageElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.renderer.GlStateManager.*;

public abstract class BaseInfoPage implements IGuidePage {

	public int pageID;
	public int pageCount = 1;
	public int currentSubPage = 0;
	public Map<String, ElementInfoFormatted> pageInfo = new HashMap<>();
	public List<ElementLink> currentLinks = new ArrayList<>();
	public List<ElementInfoFormatted> currentData = new ArrayList<>();
	public List<IGuidePageElement> elements = new ArrayList<>();
	private GuiButton selectedButton;
	public List<GuiButton> guideButtons = new ArrayList<>();
	public static final int topOffset = 18;
	// public Pair<List<String>, List<PageLink>> current = new Pair(new ArrayList<>(), new ArrayList<>());

	public BaseInfoPage(int pageID) {
		this.pageID = pageID;
	}

	@Override
	public int pageID() {
		return pageID;
	}

	@Override
	public int getPageCount() {
		return pageCount;
	}
	
	//// CREATE \\\\

	public List<IGuidePageElement> getElements(GuiGuide gui, List<IGuidePageElement> elements) {
		return elements;
	}
	
	public void initGui(GuiGuide gui, int subPage) {
		currentSubPage = subPage;
		pageInfo.clear();
		guideButtons.clear();
		elements = getElements(gui, new ArrayList<>());
		List<ElementLink> newLinks = new ArrayList<>();
		List<ElementInfoFormatted> newData = new ArrayList<>();
		boolean newPage = false;
		int ordinal = 0;
		int lineTally = 0;
		List<ElementInfo> pgInfo = getPageInfo(gui, new ArrayList<>());
		int currentInfoPos = 0;

		for (ElementInfo info : pgInfo) {

			int to = 0;
			int from = 0;
			if (!newPage && info.newPage && lineTally != 0) {
				ordinal++; // gives the info a new sub page
				lineTally = 0;
				newPage = false;
			} else if (newPage) {
				//lineTally = 0;
				newPage = false;
			}

			List<ElementLink> links = new ArrayList<>();
			List<String> lines = GuidePageHelper.getLines(this, ordinal, lineTally, info, links);

			int numPagesNeeded = ((lines.size() + lineTally) / GuidePageHelper.maxLinesPerPage) + 1;
			int currentPages = numPagesNeeded;
			while (currentPages > 0) {
				// newPage=false;
				boolean firstPage = numPagesNeeded == currentPages;
				from = Math.min(GuidePageHelper.maxLinesPerPage * (numPagesNeeded - currentPages), to);
				to = Math.min((GuidePageHelper.maxLinesPerPage * ((numPagesNeeded + 1) - currentPages)) - lineTally, lines.size());

				List<ElementLink> pageLinks = new ArrayList<>();
				for (ElementLink link : links) {
					if (link.lineNum >= from && link.lineNum <= to) {
						int linePos = lineTally + link.lineNum - 1;
						link.setDisplayPosition(ordinal, this.getLineOffset(linePos, ordinal) + link.index, topOffset+9 + ((linePos-((numPagesNeeded - currentPages)*GuidePageHelper.maxLinesPerPage)) * 12));
						pageLinks.add(link);
					} else {
						break;
					}
				}
				links.removeAll(pageLinks);

				List<String> wrapLines = lines.subList(from, to);
				if (!wrapLines.isEmpty()) {
					ElementInfo infoSource = new ElementInfo(info.key, info.additionals);
					ElementInfoFormatted infoFormatted = new ElementInfoFormatted(ordinal, infoSource, wrapLines, pageLinks);
					infoFormatted.setDisplayPosition(8, lineTally == 0 ? 0 : (lineTally) * 12);

					if (ordinal == subPage) {
						newData.add(infoFormatted);
						newLinks.addAll(infoFormatted.links);
					}

					pageInfo.put(info.key, infoFormatted);
					lineTally += wrapLines.size();
					// if (to != lines.size()-1 || currentInfoPos != pgInfo.size()-1) {
					if (to != lines.size() || !(currentInfoPos + 1 >= pgInfo.size())) {
						if (lineTally + 1 >= GuidePageHelper.maxLinesPerPage) {
							ordinal++;
							lineTally = 0;
							newPage = true;
						} else {
							lineTally++;
						}
					}
				}
				currentPages--;
			}
			currentInfoPos++;
		}
		pageCount = Math.max(1 + ordinal, !elements.isEmpty() ? (elements.get(elements.size() - 1).getDisplayPage() + 1) : 0);
		currentLinks = newLinks;
		currentData = newData;
	}
	
	//// DRAWING \\\\

	public int getLineWidth(int linePos, int page) {
		int wrapWidth = 342;
		int pos = topOffset + (linePos * 12);

		for (IGuidePageElement e : elements) {
			if (e.getDisplayPage() == page) {
				int[] position = e.getSizing();
				if ((position[1] + position[3]) > pos) {
					wrapWidth -= ((position[2] + position[0]));
					break;
				}
			}
		}
		return wrapWidth;
	}

	public int getLineOffset(int linePos, int page) {
		int pos = topOffset + linePos * 12;
		int offset = 0;
		for (IGuidePageElement e : elements) {
			if (e.getDisplayPage() == page) {
				int[] position = e.getSizing();
				if (/* position[1] <= pos && */position[1] + position[3] >= pos) {
					if ((position[0] + position[2]) > offset) {
						offset = position[0] + position[2];
					}
				}
			}
		}
		return offset;
	}
	
	public void drawPageInGui(GuiGuide gui, int yPos) {
		FontHelper.text(getDisplayName(), 28, yPos + 3, -1);
	}

	public void drawPage(GuiGuide gui, int x, int y, int page) {
		disableDepth();
		for (ElementLink pageLink : currentLinks) {
			if (pageLink != null && pageLink.isMouseOver(gui, x - gui.getGuiLeft(), y - gui.getGuiTop())) {
				gui.drawSonarCreativeTabHoveringText(TextFormatting.BLUE + "Open: " + TextFormatting.RESET + (pageLink.getGuidePage() == null ? "ERROR" : pageLink.getGuidePage().getDisplayName()), x, y);
				
				break;
			}
		}
		enableDepth();
		for (IGuidePageElement element : elements) {
			if (element.getDisplayPage() == page) {
				RenderHelper.saveBlendState();
				element.drawElement(gui, gui.getGuiLeft() + element.getSizing()[0], gui.getGuiTop() + element.getSizing()[1], page, x, y);
				RenderHelper.restoreBlendState();
			}
		}
	}

	public void drawBackgroundPage(GuiGuide gui, int x, int y, int page) {
		for (IGuidePageElement element : elements) {
			if (element.getDisplayPage() == page) {
				RenderHelper.saveBlendState();
				element.drawBackgroundElement(gui, gui.getGuiLeft() + element.getSizing()[0], gui.getGuiTop() + element.getSizing()[1], page, x, y);
				RenderHelper.restoreBlendState();
			}
		}
	}

	public void drawForegroundPage(GuiGuide gui, int x, int y, int page, float partialTicks) {
		color(1.0F, 1.0F, 1.0F, 1.0F);
		for (GuiButton button : guideButtons) {
			button.drawButtonForegroundLayer(x, y);
		}
		int listTally = 0;
		//net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		//GlStateManager.enableLighting();
		for (ElementInfoFormatted guidePage : currentData) {
			List<String> info = guidePage.formattedList;
			for (int i = 0; i < Math.min(GuidePageHelper.maxLinesPerPage, info.size()); i++) {
				String s = info.get(i);
				FontHelper.text(s, guidePage.displayX + getLineOffset(i + listTally, currentSubPage), topOffset + (i + listTally) * 12, -1);
			}
			listTally += (listTally == 0 ? 1 : 1) + guidePage.formattedList.size();
		}
		for (IGuidePageElement element : elements) {
			if (element.getDisplayPage() == currentSubPage) {
				RenderHelper.saveBlendState();
				element.drawForegroundElement(gui, element.getSizing()[0], element.getSizing()[1], page, x, y);
				RenderHelper.restoreBlendState();
			}
		}

		for (GuiButton button : this.guideButtons) {
			int left = x + gui.getGuiLeft(), top = y + gui.getGuiTop();
			button.drawButton(gui.mc, left, top, partialTicks);
		}
	}
	
	//// INTERACTION \\\\

	public void mouseClicked(GuiGuide gui, int x, int y, int button) {
		if (button == 0) {
			for (int i = 0; i < this.guideButtons.size(); ++i) {
				GuiButton guibutton = this.guideButtons.get(i);

				if (guibutton.mousePressed(gui.mc, x - gui.getGuiLeft(), y - gui.getGuiTop())) {
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(gui, guibutton, this.guideButtons);
					if (MinecraftForge.EVENT_BUS.post(event))
						break;
					guibutton = event.getButton();
					this.selectedButton = guibutton;
					guibutton.playPressSound(gui.mc.getSoundHandler());
					this.actionPerformed(guibutton);
					if (this.equals(gui.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(gui, event.getButton(), this.guideButtons));
				}
			}
		}
		for (ElementLink pageLink : currentLinks) {
			if (pageLink.isMouseOver(gui, x - gui.getGuiLeft(), y - gui.getGuiTop())) {
				gui.setCurrentPage(pageLink.guidePageLink, 0);
			}
		}
		for (IGuidePageElement element : elements) {
			if (element.getDisplayPage() == currentSubPage && element.mouseClicked(gui, this, x, y, button)) {
				return;
			}
		}
	}

	public void actionPerformed(GuiButton button) {}
}