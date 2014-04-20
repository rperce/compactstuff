package com.rperce.compactstuff.tools;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.Ref;
import com.rperce.compactstuff.client.CSIcons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompactPick extends ItemPickaxe {
	private String path;
	public CompactPick(int id, EnumToolMaterial material, String path) {
		super(id,material);
		this.path = path;
		setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void registerIcons(IconRegister ir) {
		this.itemIcon = ir.registerIcon(CSIcons.PREFIX + this.path);
	}
	@Override public boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne) {
        return CompactTool.getIsRepairable(thisOne,otherOne,"Pick");
    }
	@Override public boolean onBlockDestroyed(ItemStack thisStack, World world, int blockSlot, int x, int y, int z, EntityLivingBase holder) {
		if(thisStack.itemID!=Ref.METCARB_PICK.id() || !(holder instanceof EntityPlayer) || world.isAirBlock(x, y, z) || world.isRemote)
			return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
		Block block = Block.blocksList[blockSlot];
		if(Arrays.asList(Block.oreIron.blockID, Block.oreGold.blockID, Block.stone.blockID, Block.netherrack.blockID, Block.cobblestone.blockID)
		.contains(blockSlot)) {
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
		} return super.onBlockDestroyed(thisStack, world, blockSlot, x, y, z, holder);
	}

	/** Warnings suppressed due to override constraints */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean boo) {
		if(thisStack.itemID!=Ref.METCARB_PICK.id()) return;
		list.add("Smelts iron, gold, stone, and netherrack");
	}
}