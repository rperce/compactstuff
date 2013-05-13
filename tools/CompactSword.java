package mods.CompactStuff.tools;

import mods.CompactStuff.CSIcons;
import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.ImageFiles;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class CompactSword extends ItemSword {
	private String path;
	public CompactSword(int id, EnumToolMaterial material,String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void updateIcons(IconRegister i) {
		iconIndex = i.registerIcon(CSIcons.PREFIX + path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Sword");
    }
}
