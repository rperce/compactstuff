package compactstuff.tools;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

import compactstuff.ImageFiles;
import compactstuff.Metas;
import compactstuff.CompactStuff;

public class CompactHoe extends ItemHoe {
	public CompactHoe(int id, EnumToolMaterial material, int icon) {
		super(id,material);
		this.setIconIndex(icon);
		setCreativeTab(CompactStuff.compactTab);
	}
	public String getTextureFile() { return ImageFiles.ITEMS.path; }
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Hoe");
    }
}
