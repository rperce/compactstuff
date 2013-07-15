package mods.CompactStuff;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;

public class CompactFuelHandler implements IFuelHandler {

	@Override
	public int getBurnTime(ItemStack fuel) {
		if(fuel.itemID == CompactStuff.plantBall.itemID) {
			if(fuel.getItemDamage()==4) return 200;
			return 800;
		} else if(fuel.itemID == CompactStuff.comBlock.blockID) {
			if(fuel.getItemDamage()==Metas.COMCOAL) return 1600*8;
		}
		return 0;
	}
}
