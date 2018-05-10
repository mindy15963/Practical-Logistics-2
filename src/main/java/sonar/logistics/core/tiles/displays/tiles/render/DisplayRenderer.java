package sonar.logistics.core.tiles.displays.tiles.render;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.core.tiles.displays.tiles.ILargeDisplay;
import sonar.logistics.core.tiles.displays.gsi.DisplayGSI;
import sonar.logistics.core.tiles.displays.info.InfoRenderHelper;
import sonar.logistics.core.tiles.displays.info.types.InfoError;
import sonar.logistics.core.tiles.displays.tiles.TileAbstractDisplay;
import sonar.logistics.core.tiles.misc.hammer.render.RenderHammer;

import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.translate;

public class DisplayRenderer extends TileEntitySpecialRenderer<TileAbstractDisplay> {

	public ResourceLocation hologram = new ResourceLocation(RenderHammer.modelFolder + "hologram.png");

	@Override
	public void render(TileAbstractDisplay part, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		/*
		DisplayGSI container = part.getGSI();
		if(container == null){
			return;
		}
        if (part instanceof ILargeDisplay && (!((ILargeDisplay) part).shouldRender() || ((ILargeDisplay) part).getConnectedDisplay() == null || !((ILargeDisplay) part).getConnectedDisplay().canBeRendered.getObject())) {
			boolean bool = ((ILargeDisplay) part).getConnectedDisplay() != null && !((ILargeDisplay) part).getConnectedDisplay().canBeRendered.getObject();

			if (bool) {
				RenderHelper.offsetRendering(part.getPos(), partialTicks);
				InfoRenderHelper.rotateDisplayRendering(container.getFacing(), container.getRotation(), 0, 0);

				translate(-1, -1, -0.01);
				translate(part.getDisplayType().xPos, part.getDisplayType().yPos, 0);
				InfoRenderHelper.renderCenteredStringsWithAdaptiveScaling(part.getDisplayType().width, part.getDisplayType().height, 0.06, 0, 0.75, -1, Lists.newArrayList(InfoError.incompleteDisplay.error));
			
				popMatrix();
			}

			return;
		}

		RenderHelper.offsetRendering(part.getPos(), partialTicks);
		InfoRenderHelper.rotateDisplayRendering(container.getFacing(), container.getRotation(), 0, 0);
		translate(-1, -1, -0.005);
		translate(part.getDisplayType().xPos, part.getDisplayType().yPos, 0);
		container.render();
		popMatrix();
		*/

		DisplayGSI container = part.getGSI();
		if(container == null || (part instanceof ILargeDisplay && !((ILargeDisplay) part).shouldRender()) && !((ILargeDisplay) part).getConnectedDisplay().get().canBeRendered.getObject()){
			return;
		}
		pushMatrix();
		Vec3d origin = part.getScreenOrigin();
		Vec3d rotation = part.getScreenRotation();
		Vec3d scaling = part.getScreenScaling();
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		double vX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
		double vY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
		double vZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;
		translate(origin.x - vX, origin.y - vY, origin.z - vZ);
		GlStateManager.rotate(-(float)rotation.y, 0, 1, 0);
		GlStateManager.rotate((float)rotation.x, 1, 0, 0);
		GlStateManager.rotate((float)rotation.z, 0, 0, 1);
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.translate(-scaling.x/2,-scaling.y/2,-0.005);
		container.render();
		popMatrix();
	}
}
