package compactstuff.tools;

import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import compactstuff.ImageFiles;
import compactstuff.Metas;
import compactstuff.CompactStuff;

public class CompactSword extends ItemSword {
	public CompactSword(int id, EnumToolMaterial material,int icon) {
		super(id,material);
		this.setIconIndex(icon);
		setCreativeTab(CompactStuff.compactTab);
	}
	public String getTextureFile() { return ImageFiles.ITEMS.path; }
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Sword");
    }
}
