package sonar.logistics.packets;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.network.PacketMultipart;
import sonar.core.network.PacketMultipartHandler;
import sonar.logistics.api.displays.storage.DisplayGSISaveHandler;
import sonar.logistics.common.multiparts.holographic.TileAdvancedHolographicDisplay;

public class PacketHolographicDisplayScaling extends PacketMultipart {


    private Vec3d screenScale;
    private Vec3d screenRotation;
    private Vec3d screenOffset;

    public PacketHolographicDisplayScaling() {}

    public PacketHolographicDisplayScaling(TileAdvancedHolographicDisplay display) {
        super(display.getSlotID(), display.getPos());
        screenScale = display.getScreenScaling();
        screenRotation = display.getScreenRotation();
        screenOffset = display.getScreenOffset();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        NBTTagCompound nbt = ByteBufUtils.readTag(buf);
        screenScale = TileAdvancedHolographicDisplay.readVec3d("scale", nbt, NBTHelper.SyncType.SAVE);
        screenRotation = TileAdvancedHolographicDisplay.readVec3d("rotate", nbt, NBTHelper.SyncType.SAVE);
        screenOffset = TileAdvancedHolographicDisplay.readVec3d("offset", nbt, NBTHelper.SyncType.SAVE);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NBTTagCompound nbt = new NBTTagCompound();
        TileAdvancedHolographicDisplay.writeVec3d(screenScale,"scale", nbt, NBTHelper.SyncType.SAVE);
        TileAdvancedHolographicDisplay.writeVec3d(screenRotation,"rotate", nbt, NBTHelper.SyncType.SAVE);
        TileAdvancedHolographicDisplay.writeVec3d(screenOffset,"offset", nbt, NBTHelper.SyncType.SAVE);
        ByteBufUtils.writeTag(buf, nbt);
    }


    public static class Handler extends PacketMultipartHandler<PacketHolographicDisplayScaling> {
        @Override
        public IMessage processMessage(PacketHolographicDisplayScaling message, EntityPlayer player, World world, IMultipartTile part, MessageContext ctx) {
            if(ctx.side == Side.SERVER && part instanceof TileAdvancedHolographicDisplay){
                SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(()->{
                    TileAdvancedHolographicDisplay display = (TileAdvancedHolographicDisplay) part;
                    display.screenScale = message.screenScale;
                    display.screenRotation = message.screenRotation;
                    display.screenOffset = message.screenOffset;
                    display.getGSI().updateScaling();
                    display.getGSI().sendInfoContainerPacket(DisplayGSISaveHandler.DisplayGSISavedData.ALL_DATA);
                    display.markDirty();
                    display.getGSI().getWatchers().forEach(watcher -> display.sendSyncPacket(watcher, NBTHelper.SyncType.SAVE));
                });
            }
            return null;
        }
    }
}