package compactstuff.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import compactstuff.CompactStuff;
import compactstuff.ImageFiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompactPick extends ItemPickaxe {
	public CompactPick(int id, EnumToolMaterial material, int icon) {
		super(id,material);
		this.setIconIndex(icon);
		setCreativeTab(CompactStuff.compactTab);
		setTextureFile(ImageFiles.ITEMS.path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Pick");
    }
	@Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLiving holder) {
		if(thisStack.itemID!=CompactStuff.heatPick.itemID || !(holder instanceof EntityPlayer))
			return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
		EntityPlayer player = (EntityPlayer)holder;
		if(blockSlot==Block.oreIron.blockID || blockSlot==Block.oreGold.blockID || blockSlot==Block.stone.blockID) {
			ItemStack origDrop = new ItemStack(
					Block.blocksList[blockSlot].idDropped(world.getBlockMetadata(x,y,z), world.rand, 0),
					Block.blocksList[blockSlot].quantityDropped(world.rand),
					Block.blocksList[blockSlot].damageDropped(world.getBlockMetadata(x,y,z)));
			EntityItem drop = new EntityItem(world, x, y, z,
					FurnaceRecipes.smelting().getSmeltingResult(origDrop));
			drop.delayBeforeCanPickup = 10;
			world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
			world.spawnEntityInWorld(drop);
			System.out.println("Just dropped "+FurnaceRecipes.smelting().getSmeltingResult(origDrop).getDisplayName());
			thisStack.damageItem(1, player);
			return true;
		}
		return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
	}
	@SideOnly(Side.CLIENT)
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=CompactStuff.heatPick.itemID) return;
		list.add("Smelts iron, gold, and stone");
		list.add("40% double iron, gold, netherrack");
	}
}