package sonar.logistics.network.packets.gsi;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.logistics.base.ClientInfoHandler;
import sonar.logistics.core.tiles.displays.gsi.DisplayGSI;

public class PacketGSIInvalidate implements IMessage {

	public int GSI_IDENTITY = -1;
	
	public PacketGSIInvalidate() {}

	public PacketGSIInvalidate(DisplayGSI gsi) {
		GSI_IDENTITY = gsi.getDisplayGSIIdentity();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		GSI_IDENTITY = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(GSI_IDENTITY);
	}

	public static class Handler implements IMessageHandler<PacketGSIInvalidate, IMessage> {

		@Override
		public IMessage onMessage(PacketGSIInvalidate message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					if (player != null) {
						DisplayGSI gsi = ClientInfoHandler.instance().getGSIMap().get(message.GSI_IDENTITY);
						if (gsi != null) {
							gsi.invalidate();
						}
					}
				});

			}
			return null;
		}

	}

}
