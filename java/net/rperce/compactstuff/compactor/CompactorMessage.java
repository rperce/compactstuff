package net.rperce.compactstuff.compactor;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.rperce.compactstuff.ClickType;

public class CompactorMessage implements IMessage {
    private int slot;
    private ClickType clickType;

    public CompactorMessage() {}
    public CompactorMessage(int slot, ClickType clickType) {
        this.slot = slot;
        this.clickType = clickType;
    }
    public int getSlot() { return slot; }
    public ClickType getClickType() { return clickType; }
    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        clickType = ClickType.fromByte(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        buf.writeByte(clickType.toByte());
    }

    public static class Handler implements IMessageHandler<CompactorMessage, IMessage> {
        @Override
        public IMessage onMessage(CompactorMessage message, MessageContext ctx) {
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
