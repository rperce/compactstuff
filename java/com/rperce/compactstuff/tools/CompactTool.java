package com.rperce.compactstuff.tools;

import java.lang.reflect.Field;

import com.rperce.compactstuff.CompactStuff;
import com.rperce.compactstuff.ItemStuff;
import com.rperce.compactstuff.Metas;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CompactTool {
	public static boolean getIsRepairable(ItemStack thisOne, ItemStack otherOne, String itemName) {
		Field cob,met,steel;
		try {
			cob 	= CompactStuff.class.getField("comCob"+itemName);
			met		= CompactStuff.class.getField("heat"+itemName);
			steel	= CompactStuff.class.getField("steel"+itemName);
		} catch(NoSuchFieldException e) {
			return false;
		}
		
		try {
			return (thisOne.itemID== ((Item)cob.get(null)).itemID &&
				otherOne.itemID==CompactStuff.comBlock.blockID &&
				otherOne.getItemDamage()==Metas.DIORITE) ||
			(thisOne.itemID==((Item)met.get(null)).itemID &&
				otherOne.itemID==CompactStuff.carbon.itemID &&
				otherOne.getItemDamage()==4) ||
			(thisOne.itemID==((Item)steel.get(null)).itemID &&
				otherOne.itemID==CompactStuff.itemStuff.itemID &&
				otherOne.getItemDamage()==ItemStuff.STEEL_INGOT) ||
			(thisOne.itemID==otherOne.itemID);
		} catch(IllegalAccessException e) {
			return false;
		}
	}
}
