package com.rperce.compactstuff.boh;

import java.util.List;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.client.CSIcons;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBagOfHolding extends Item {
	public static final int[][] COLOR_CRAFTING = {
		//      0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 },
		{/*0*/ -1, -1, -1, -1, -1, -1, -1,  8,  0, -1, -1, -1, -1, -1, -1,  7 },
		{/*1*/ -1, -1,  3, -1,  5,  1, -1, -1, -1,  1, -1, 14, -1, -1,  1,  9 },
		{/*2*/ -1,  3, -1, -1,  6, -1,  2, -1, -1, -1,  2, -1, -1, -1, -1, 10 },
		{/*3*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  3 },
		{/*4*/ -1,  5,  6, -1, -1,  4,  4, -1, -1, -1, -1, -1,  4, -1,  3, 12 },
		{/*5*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, 13, -1,  3, -1,  5, -1,  5 },
		{/*6*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  6 },
		{/*7*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  7 },
		{/*8*/ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  8 },
		{/*9*/ -1, -1, -1, -1, -1, 13, -1, -1, -1, -1, -1, -1, -1,  9, -1,  9 },
		{/*10*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10 },
		{/*11*/-1, 14, -1, -1, -1,  3, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11 },
		{/*12*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12 },
		{/*13*/-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 13 },
		{/*14*/-1, -1, -1, -1,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14 },
		{/*15*/ 8,  9, 10, 15, 12, 15, 15, 15,  7, 15, 15, 15, 15, 15, 15, -1 },
	};
	private static Icon[] icons = new Icon[16];
	static int rows;
	public ItemBagOfHolding(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(1);
		this.rows = 3;
		this.setUnlocalizedName("Bag of Holding");
		this.setCreativeTab(CompactStuff.compactTab);
	}
	@Override public void registerIcons(IconRegister ir) {
		for(int i=0; i<16; i++) {
			icons[i] = ir.registerIcon(CSIcons.PREFIX+"boh"+i);
		}
	}
	@Override public Icon getIconFromDamage(int meta) {
		return icons[meta];
	}	
	@Override public ItemStack onItemRightClick(ItemStack thisStack, World world, EntityPlayer player) {
		player.openGui(CompactStuff.instance, 2, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return thisStack;
	}	
	public static InventoryBagOfHolding getInventory(EntityPlayer player) {
		InventoryBagOfHolding out = null;
		ItemStack held = player.getHeldItem();
		if(held!=null && held.getItem() instanceof ItemBagOfHolding) {
			out = new InventoryBagOfHolding(rows,held);
		}
		if(out==null) System.err.println("Bag of holding is null!");
		return out;		
	}	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for(int meta=0; meta<16; meta++)
			list.add(new ItemStack(id,1,meta));
	}
}
