package sonar.logistics.api.displays.elements;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.PL2Constants;
import sonar.logistics.api.asm.DisplayElementType;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.tiles.displays.DisplayScreenClick;

@DisplayElementType(id = ButtonElement.REGISTRY_NAME, modid = PL2Constants.MODID)
//only works for square buttons atm
public class ButtonElement extends AbstractDisplayElement implements IClickableElement, ILookableElement {

	public static final ResourceLocation BUTTON_TEX = new ResourceLocation(PL2Constants.MODID + ":textures/gui/filter_buttons.png");
	public int buttonID;
	// the x/y position of this button on the filter button texture, (counted in )
	public int buttonX, buttonY;
	public String hoverString;

	public int unscaledWidth = 4;
	public int unscaledHeight = 4;

	public ButtonElement() {}

	public ButtonElement(int buttonID, int buttonX, int buttonY, String hoverString) {
		this.buttonID = buttonID;
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		this.hoverString = hoverString;
	}

	@Override
	public void render() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(BUTTON_TEX);
		GlStateManager.disableLighting();
		RenderHelper.drawModalRectWithCustomSizedTexture(0, 0, (buttonX * 16) * 0.0625, (buttonY * 16) * 0.0625, 1, 1, 16, 16);
		/* double x= 0; double y=0; double u=0; double v=0; double width=4; double height=4; double textureWidth=64; double textureHeight=64; double f = 1.0 / textureWidth; double f1 = 1.0 / textureHeight; Tessellator tessellator = Tessellator.getInstance(); BufferBuilder bufferbuilder = tessellator.getBuffer(); bufferbuilder.begin(7, DefaultVertexFormats.TEX_2F); bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + height) * f1).endVertex(); bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + width) * f, (v + height) * f1).endVertex(); bufferbuilder.pos(x + width, y, 0).tex((u + width) * f, v * f1).endVertex(); bufferbuilder.pos(x, y, 0).tex(u * f, v * f1).endVertex(); tessellator.draw(); */
		// if (shouldRenderClick(buttonID)) {
		/** renders a blue select box around the button if has been clicked */
		// RenderHelper.drawModalRectWithCustomSizedTexture(posX, posY, (double) 15 / ((double) texPixelWidth / (button.width / pixel)), (double) 15 / ((double) texPixelWidth / (button.height / pixel)), button.width, button.height, button.width / pixel, button.width / pixel);
		// }
		if (isPlayerLooking()) {
			/** renders a blue select box around the button if has been clicked */
			RenderHelper.drawModalRectWithCustomSizedTexture(0, 0, (15 * 16) * 0.0625, (15 * 16) * 0.0625, 1, 1, 16, 16);
		}
	}

	@Override
	public List<InfoUUID> getInfoReferences() {
		return Lists.newArrayList();
	}

	@Override
	public String getRepresentiveString() {
		return hoverString;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		return nbt;
	}

	@Override
	int[] createUnscaledWidthHeight() {
		return new int[] { 1, 1 };
	}

	@Override
	public int onGSIClicked(DisplayScreenClick click, EntityPlayer player, double subClickX, double subClickY) {
		/* switch(buttonID){ case 0: GSIHelper.sendGSIPacket(GSIHelper.createBasicPacket(GSIPackets.SOURCE_BUTTON), getHolder().getContainer(), click); return true; } */
		return -1;
	}

	public static final String REGISTRY_NAME = "button_element";

	@Override
	public String getRegisteredName() {
		return REGISTRY_NAME;
	}

}