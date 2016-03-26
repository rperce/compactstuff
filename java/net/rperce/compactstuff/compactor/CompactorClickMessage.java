package net.rperce.compactstuff.compactor;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.rperce.compactstuff.MouseButtonType;

public class CompactorClickMessage implements IMessage {
    private int slot;
    private MouseButtonType clickType;

    public CompactorClickMessage() {}
    public CompactorClickMessage(int slot, MouseButtonType clickType) {
        this.slot = slot;
        this.clickType = clickType;
    }
    public int getSlot() { return slot; }
    public MouseButtonType getClickType() { return clickType; }
    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        clickType = MouseButtonType.fromByte(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        buf.writeByte(clickType.toByte());
    }

    public static class Handler implements IMessageHandler<CompactorClickMessage, IMessage> {
        @Override
        public IMessage onMessage(CompactorClickMessage message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Container container = player.openContainer;
                if (container instanceof ContainerCompactor) {
                    ContainerCompactor containerCompactor = (ContainerCompactor)container;
                    TileEntityCompactor tec = containerCompactor.getTileEntity();
                    tec.acceptCompactorMessage(message);
                }
            });
            return null;
        }
    }
}
