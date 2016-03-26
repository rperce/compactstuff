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

public class CompactorClearMessage implements IMessage {

    public CompactorClearMessage() {}
    @Override
    public void fromBytes(ByteBuf buf) {
        // no information needed
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // no information needed
    }

    public static class Handler implements IMessageHandler<CompactorClearMessage, IMessage> {
        @Override
        public IMessage onMessage(CompactorClearMessage message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Container container = player.openContainer;
                if (container instanceof ContainerCompactor) {
                    ContainerCompactor containerCompactor = (ContainerCompactor)container;
                    containerCompactor.clearCraftingGrid();
                }
            });
            return null;
        }
    }
}
