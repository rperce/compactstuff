package compactstuff.tools;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

import compactstuff.ImageFiles;
import compactstuff.ItemStuff;
import compactstuff.Metas;
import compactstuff.CompactStuff;

public class CompactAxe extends ItemAxe {
	public CompactAxe(int id, EnumToolMaterial material, int icon) {
		super(id,material);
		this.setIconIndex(icon);
		setCreativeTab(CompactStuff.compactTab);
	}
	public String getTextureFile() { return ImageFiles.ITEMS.path; }
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Axe");
    }
}
