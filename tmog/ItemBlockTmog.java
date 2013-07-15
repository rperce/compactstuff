package mods.CompactStuff.tmog;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockTmog extends ItemBlock {

	public ItemBlockTmog(int id) {
		super(id);
		setHasSubtypes(true);
	}
	@Override public int getMetadata(int d) {
		return d;
	}
	
	@SideOnly(Side.CLIENT)
	@Override public void getSubItems(int id, CreativeTabs tab, List list) {
		for(int i=0; i<3; i++) {
			//if(i==3) continue; //0011 is not a thing
			list.add(new ItemStack(id,1,i));
		}
	}
	
	@Override public String getUnlocalizedName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0: case 4: return "Transmogrifier Core";
			case 1: case 5: return "Transmogrifier Shielding";
			case 2: case 6: return "Transmogrifier Frame";
		} return "+++ OUT OF CHEESE ERROR +++";
	}

}
