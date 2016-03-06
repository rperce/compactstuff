package net.rperce.compactstuff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 3/5/16.
 */
public class GuiHandlerRegistry implements IGuiHandler {
    private static GuiHandlerRegistry guiHandlerRegistry = new GuiHandlerRegistry();
    private Map<Integer, IGuiHandler> registeredHandlers = new HashMap<>();

    public void registerGuiHandler(IGuiHandler handler, int guiID) {
        System.err.printf("Registered handler for gui ID %d\n", guiID);
        registeredHandlers.put(guiID, handler);
    }

    public static GuiHandlerRegistry getInstance() {
        return guiHandlerRegistry;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = registeredHandlers.get(ID);
        if (handler == null) throw new IllegalArgumentException(String.format("Got ID %d, which is unregistered in CompactStuff", ID));
        return handler.getServerGuiElement(ID, player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = registeredHandlers.get(ID);
        if (handler == null) throw new IllegalArgumentException(String.format("Got ID %d, which is unregistered in CompactStuff", ID));
        return handler.getClientGuiElement(ID, player, world, x, y, z);
    }
}
