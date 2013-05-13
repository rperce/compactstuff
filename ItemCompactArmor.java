package mods.CompactStuff;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static mods.CompactStuff.CompactStuff.*;

public class ItemCompactArmor extends ItemArmor implements IArmorTextureProvider {
	private String path;
	public ItemCompactArmor(int id, EnumArmorMaterial material, int rend, int armorType, String path) {
		super(id, material, rend, armorType);
		this.setCreativeTab(CompactStuff.compactTab);
		this.path = CSIcons.PREFIX + path;
	}
	
	@Override public void updateIcons(IconRegister ir) {
		iconIndex = ir.registerIcon(path);
	}
	
	@ForgeSubscribe
	public static void onEntityLivingFallEvent(LivingFallEvent evt) {
		if(evt.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)evt.entity;
			ItemStack boots = player.inventory.armorItemInSlot(0);
			if(boots==null) return;
			if(boots.itemID==CompactStuff.wovnBoots.itemID ||
				boots.itemID==CompactStuff.pureBoots.itemID) {
				if(evt.distance>5) boots.damageItem(1, player);
				evt.setCanceled(true);
				
			}
		}
	}
	@Override @SideOnly(Side.CLIENT)
	public String getArmorTextureFile(ItemStack itemstack) {
		int id = itemstack.itemID; 
		if(id==cobHelmt.itemID || id==cobPlate.itemID || id==cobBoots.itemID)
			return ImageFiles.COBBLE_1.path;
		else if(id==cobPants.itemID)
			return ImageFiles.COBBLE_2.path;
		else if(id==carbHelmt.itemID || id==carbPlate.itemID || id==carbBoots.itemID)
			return ImageFiles.CARBON_1.path;
		else if(id==carbPants.itemID)
			return ImageFiles.CARBON_2.path;
		else if(id==wovnHelmt.itemID || id==wovnPlate.itemID || id==wovnBoots.itemID)
			return ImageFiles.WOVEN_1.path;
		else if(id==wovnPants.itemID)
			return ImageFiles.WOVEN_2.path;
		else if(id==pureHelmt.itemID || id==purePlate.itemID || id==pureBoots.itemID)
			return ImageFiles.ADVANCED_1.path;
		else
			return ImageFiles.ADVANCED_2.path;
	}
	@SideOnly(Side.CLIENT)
	@Override public void addInformation(ItemStack thisStack, EntityPlayer player, List list, boolean wut) {
		int id = thisStack.itemID;
		if(id==wovnHelmt.itemID) list.add("2x water breathing");
		else if(id==wovnPlate.itemID) list.add("Retaliate on melee");
		else if(id==wovnPants.itemID) list.add("Fire extinguishing");
		else if(id==wovnBoots.itemID) list.add("No fall damage");
		else if(id==pureHelmt.itemID) {
			list.add("Infinite water breathing");
			list.add("Infinite durability");
		}
		else if(id==purePlate.itemID) {
			list.add("Force field to hostiles");
			list.add("Infinite durability");
		}
		else if(id==purePants.itemID) {
			list.add("Fire immunity");
			list.add("Infinite durability");
		}
		else if(id==pureBoots.itemID) {
			list.add("No fall damage");
			list.add("Infinite durability");
		}
	}
}
