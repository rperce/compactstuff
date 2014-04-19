package com.rperce.compactstuff;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CompactTickHandler implements ITickHandler {

	private EnumSet<TickType> tickSet;
	private boolean fastmode = false;
	private static CompactTickHandler instance;
	public static CompactTickHandler getHandler() { return instance; }
	private HashMap<EntityPlayer,Boolean> tickEvens;
	public CompactTickHandler(EnumSet<TickType> type) {
		tickSet = type;
		instance = this;
		tickEvens = new HashMap<EntityPlayer,Boolean>();
	}
	
	public void toggleFastMode() {
		fastmode = !fastmode;
		System.out.println("Fastmode: "+fastmode);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		for(Object playerObject : MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).playerEntityList) {
			if(playerObject instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)playerObject;
				ItemStack[] armor = player.inventory.armorInventory;
				if(armor[3]!=null) { //helmet
					if(Ref.matches(armor[3], Ref.WOVEN_HELM)) {
						Boolean is = tickEvens.get(player);
						is=(is==null)?false:is;
						tickEvens.put(player,!is);
						if(is) player.setAir(player.getAir()+1);
					} else if(Ref.matches(armor[3], Ref.ADV_HELM)) {
						player.setAir(300);
						armor[3].setItemDamage(0);
					}
				} if(armor[2]!=null) { //chestplate
					if(Ref.matches(armor[2], Ref.WOVEN_PLATE)) {
						doThornyThings(player,.25d,6);
						alsoCheckForSlimes(player,.3d,4);
					} else if(Ref.matches(armor[2], Ref.ADV_PLATE)) {
						doThornyThings(player, 2d,8);
						alsoCheckForSlimes(player,2d,8);
						armor[2].setItemDamage(0);
					}
				} if(armor[1]!=null) { //leggings
					if(Ref.matches(armor[1], Ref.WOVEN_PANTS)) {
						if(!player.isInsideOfMaterial(Material.fire)) player.extinguish();
					} else if(Ref.matches(armor[1], Ref.ADV_PANTS)) {
						if(!setPlayerIsImmuneToFire(player,true)) continue;		
						armor[1].setItemDamage(0);
					} else {
						if(!setPlayerIsImmuneToFire(player,false)) continue;
					}
				} else { 
					if(!setPlayerIsImmuneToFire(player,false)) continue;
				} if(armor[0]!=null) {//boots
					if(Ref.matches(armor[0], Ref.ADV_BOOTS)) {
						armor[0].setItemDamage(0);
					}
				}
				// boots fall damage canceling handled in ItemCompactArmor
			}
		}
	}
	
	private void doThornyThings(EntityPlayer player,double d,int dmg) {
		List l = new ArrayList(player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(d, d, d)));
		for(Object o : l) {			
			if(o instanceof EntityLivingBase) {
				EntityLivingBase e = (EntityLivingBase)o;
				
				if(e instanceof EntityPlayer) {
					EntityPlayer p = (EntityPlayer)e;
					if(p.getLastAttacker()!=null && p.getLastAttacker().equals(player)) {
						p.attackEntityFrom(DamageSource.causePlayerDamage(player), (int)(2./3*dmg));
						p.setLastAttacker(new EntityZombie(p.worldObj));
					}
				} else if(e instanceof EntityMob) {
					EntityPlayer attack = findPlayerToAttack((EntityMob)e);
					if(attack!=null && attack.equals(player))
						e.attackEntityFrom(DamageSource.causePlayerDamage(player), dmg);
				}
			}
		}
	} private void alsoCheckForSlimes(EntityPlayer player, double d,int dmg) {
		List l = new ArrayList(player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(d, d, d)));
		for(Object o : l) {			
			if(o instanceof EntitySlime)				
				((EntitySlime)o).attackEntityFrom(DamageSource.causePlayerDamage(player), dmg);
		}
	}
	private EntityPlayer findPlayerToAttack(EntityMob e) {
		EntityPlayer var1 = e.worldObj.getClosestVulnerablePlayerToEntity(e, 16.0D);
        return var1 != null && e.canEntityBeSeen(var1) ? var1 : null;
	}
	private double distance(Entity a, Entity b) {
		double dx=a.posX-b.posX, dy=a.posY-b.posY, dz=a.posZ-b.posZ;
		return Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	
	private boolean setPlayerIsImmuneToFire(EntityPlayer player, boolean is) {
		Field fireResist = null;
		try {
			fireResist = Entity.class.getDeclaredField("isImmuneToFire");
		} catch(NoSuchFieldException e) { try {
			fireResist = Entity.class.getDeclaredField("field_70178_ae");
		} catch(NoSuchFieldException e1) { try {
			fireResist = Entity.class.getDeclaredField("af");
		} catch(NoSuchFieldException e2) {
			FMLLog.warning("CompactStuff could not %s fire damage.",is?"prevent":"enable");
			e2.printStackTrace();
		}}}
		if(fireResist==null) return false;
		fireResist.setAccessible(true);
		try { fireResist.setBoolean(player, is); }
		catch(IllegalAccessException e) {
			FMLLog.warning("CompactStuff could not %s fire damage.",is?"prevent":"enable");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return tickSet;
	}

	@Override
	public String getLabel() {
		return "CompactStuffServerTick";
	}

}
