package mods.CompactStuff.tools;

import java.util.List;

import mods.CompactStuff.CSIcons;
import mods.CompactStuff.CompactStuff;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompactPick extends ItemPickaxe {
	private String path;
	public CompactPick(int id, EnumToolMaterial material, String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void updateIcons(IconRegister ir) {
		iconIndex = ir.registerIcon(CSIcons.PREFIX + path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Pick");
    }
	@Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLiving holder) {
		if(thisStack.itemID!=CompactStuff.heatPick.itemID || !(holder instanceof EntityPlayer) || world.isAirBlock(x, y, z) || world.isRemote)
			return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
		Block block = Block.blocksList[blockSlot];
		if(blockSlot==Block.oreIron.blockID || blockSlot==Block.oreGold.blockID || blockSlot==Block.stone.blockID) {
			ItemStack drop = FurnaceRecipes.smelting().getSmeltingResult(
				new ItemStack(
					block.idDropped(blockSlot, world.rand, 0),
					block.quantityDropped(world.rand),
					block.damageDropped(world.getBlockMetadata(x, y, z))));
			drop.stackSize=(blockSlot!=Block.stone.blockID && world.rand.nextInt(10)<4?2:1);
			world.setBlockToAir(x, y, z);
			thisStack.damageItem(1, holder);
			EntityItem item = new EntityItem(world, x+.5, y+.5, z+.5, drop.copy());
			world.spawnEntityInWorld(item);
			return true;
		} else if(blockSlot==Block.netherrack.blockID) {
			ItemStack drop = new ItemStack(
				block.idDropped(blockSlot, world.rand, 0),
				(world.rand.nextInt(10)<4?2:1)*block.quantityDropped(world.rand),
				block.damageDropped(world.getBlockMetadata(x, y, z)));
			world.setBlockToAir(x, y, z);
			thisStack.damageItem(1, holder);
			EntityItem item = new EntityItem(world, x+.5, y+.5, z+.5, drop.copy());
			world.spawnEntityInWorld(item);
			return true;
		} return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
	}
	@SideOnly(Side.CLIENT)
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=CompactStuff.heatPick.itemID) return;
		list.add("Smelts iron, gold, and stone");
		list.add("40% double iron, gold, netherrack");
	}
}