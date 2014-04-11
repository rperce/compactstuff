package com.rperce.compactstuff;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.rperce.compactstuff.boh.ItemBagOfHolding;
import com.rperce.compactstuff.compactor.BlockCompactor;
import com.rperce.compactstuff.furnace.BlockBlazeFurnace;
import com.rperce.compactstuff.furnace.BlockCompactFurnace;
import com.rperce.compactstuff.tmog.BlockTmog;
import com.rperce.compactstuff.tools.CompactAxe;
import com.rperce.compactstuff.tools.CompactHoe;
import com.rperce.compactstuff.tools.CompactPick;
import com.rperce.compactstuff.tools.CompactSpade;
import com.rperce.compactstuff.tools.CompactSword;
import com.rperce.compactstuff.tools.Paxel;
import com.rperce.compactstuff.tools.SmeltOnAStick;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="robertwan_compactstuff", name="CompactStuff", version="1.5.2")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, packetHandler=PacketHandler.class, channels={Metas.CH_COMPCRAFT, Metas.CH_COMPOUT, Metas.CH_COMPMAKE})
public class CompactStuff {
		@Instance("robertwan_compactstuff")
		public static CompactStuff instance;
		
		@SidedProxy(clientSide="com.rperce.compactstuff.client.ClientProxy", serverSide="com.rperce.compactstuff.CommonProxy")
		public static CommonProxy proxy;
			
		public static Random rand = new Random();
		
		public static EnumToolMaterial 
			comCobToolMaterial 		= 	EnumHelper.addToolMaterial("comCobbleTool",	2,	512,	6.5f,	3,	15),
			steelToolMaterial		=	EnumHelper.addToolMaterial("csSteelTool",	2,	1024,	7.5f,	4,	17),
			heatCarbToolMaterial	=	EnumHelper.addToolMaterial("heatCarbTool",	3,	2048,	8.5f,	5,	20),
			paxelMaterial			=	EnumHelper.addToolMaterial("compactPaxelTool", 3, 4096, 10.5f,  9, 	0);

		public static EnumArmorMaterial 
			comCobArmorMaterial 	=	EnumHelper.addArmorMaterial("comCobArmor", 	16, new int[] {2, 6,5,2},10),
			heatCarbArmorMaterial	=	EnumHelper.addArmorMaterial("heatCarbArmor",35,	new int[] {4, 8,7,4},25),
			wovnCarbArmorMaterial	=	EnumHelper.addArmorMaterial("wovnCarbArmor",40, new int[] {6,10,9,6}, 0),
			pureCarbArmorMaterial	=	EnumHelper.addArmorMaterial("pureCarbArmor",50, new int[] {10,10,10,10},0);

		public static Item 
			plantBall, carbon, paxel, bagOfHolding,
			comCobSword, comCobPick, comCobHoe, comCobAxe, 	comCobSpade,
			heatSword, 	 heatPick, 	 heatHoe, 	heatAxe, 	heatSpade,
			steelSword,	steelPick,	steelHoe,	steelAxe,	steelSpade,
			cobHelmt, cobPlate, cobPants, cobBoots,
			carbHelmt, carbPlate, carbPants, carbBoots,
			wovnHelmt, wovnPlate, wovnPants, wovnBoots,
			pureHelmt, purePlate, purePants, pureBoots,
			itemStuff,
			smeltOnAStick;

		public static Block comBlock, furnace, blazeFurn, comGlass, compactor, transmog;	
		
		public static CreativeTabs compactTab;
		
		private final int specEnchant = 5;
		private final String[] toolnames = {"Sword","Pickaxe","Axe","Hoe","Shovel",};
		private final String[] armornames = {"Helmet", "Chestplate", "Leggings", "Boots"};
		
		@EventHandler
		public void preInit(FMLPreInitializationEvent e) {
			proxy.registerRenderers();
			NBTTagCompound testList = new NBTTagCompound();
			Commons.writeStacksToNBT(testList, new ItemStack[] { null, new ItemStack(Block.dirt),
				null, new ItemStack(Block.stone), null, null, new ItemStack(Block.grass), null
			});
			System.out.printf("IMPORTANT: %s%n", Arrays.toString(Commons.readStacksFromNBT(testList)));
			
			compactTab = new CreativeTabs("compactTab") {
				@Override public ItemStack getIconItemStack() {
					return new ItemStack(comBlock,1,Metas.COMCOBBLE);
				}
				@Override public String getTranslatedTabLabel() {
					return "Compact Stuff";
				}
			};
			
			Configuration c = new Configuration(e.getSuggestedConfigurationFile());
			c.load();
			
			int idCompressed= c.getBlock("compressed",		500).getInt(),
				idFurnaces	= c.getBlock("furnaces",		501).getInt(),
				idComGlass	= c.getBlock("comglass",		502).getInt(),
				idCompactor	= c.getBlock("compactor",		503).getInt(),
				idTransmog	= c.getBlock("transmogrifier", 	504).getInt(),
				idBlazeFurn	= c.getBlock("blazeFurnace",	505).getInt(),
								
				idPlantBall = c.getItem("plantBall", 		9337).getInt(),
				idCarbon	= c.getItem("carbon",			9338).getInt(),
				idCobSword	= c.getItem("comCobbleSword", 	9339).getInt(),
				idCobPick	= c.getItem("comCobblePick",	9340).getInt(),
				idCobHoe	= c.getItem("comCobbleHoe",		9341).getInt(),
				idCobAxe	= c.getItem("comCobbleAxe",		9342).getInt(),
				idCobSpade	= c.getItem("comCobbleSpade", 	9343).getInt(),
				idHeatSword	= c.getItem("heatCarbSword",	9344).getInt(),
				idHeatPick	= c.getItem("heatCarbPick",		9345).getInt(),
				idHeatHoe	= c.getItem("heatCarbHoe",		9346).getInt(),
				idHeatAxe	= c.getItem("heatCarbAxe",		9347).getInt(),
				idHeatSpade	= c.getItem("heatCarbSpade",	9348).getInt(),
				idCobHelmt	= c.getItem("comCobbleHelmet",	9349).getInt(),
				idCobPlate	= c.getItem("comCobblePlate",	9350).getInt(),
				idCobPants	= c.getItem("comCobblePants",	9351).getInt(),
				idCobBoots	= c.getItem("comCobbleBoots",	9352).getInt(),
				idCarbHelmt	= c.getItem("heatCarbonHelmet",	9353).getInt(),
				idCarbPlate	= c.getItem("heatCarbonPlate",	9354).getInt(),
				idCarbPants	= c.getItem("heatCarbonPants",	9355).getInt(),
				idCarbBoots	= c.getItem("heatCarbonBoots",	9356).getInt(),
				idWovnHelmt = c.getItem("wovenCarbonHelmet",9357).getInt(),
				idWovnPlate = c.getItem("wovenCarbonPlate",	9358).getInt(),
				idWovnPants	= c.getItem("wovenCarbonPants",	9359).getInt(),
				idWovnBoots	= c.getItem("wovenCarbonBoots",	9360).getInt(),
				idPureHelmt	= c.getItem("pureCarbonHelmet",	9361).getInt(),
				idPurePlate	= c.getItem("pureCarbonPlate",	9362).getInt(),
				idPurePants	= c.getItem("pureCarbonPants",	9363).getInt(),
				idPureBoots	= c.getItem("pureCarbonBoots",	9364).getInt(),
				idPaxel		= c.getItem("paxel",			9365).getInt(),
				idBoH		= c.getItem("bagOfHolding",		9366).getInt(),
				idCompact	= c.getItem("assortedItems",	9367).getInt(),
				idSteelSword= c.getItem("steelSword",		9368).getInt(),
				idSteelPick	= c.getItem("steelPick",		9369).getInt(),
				idSteelHoe	= c.getItem("steelHoe",			9370).getInt(),
				idSteelAxe	= c.getItem("steelAxe",			9371).getInt(),
				idSteelSpade= c.getItem("steelSpade",		9372).getInt(),
				idSmeltStick= c.getItem("smeltOnAStick", 	9373).getInt(),
								
				cobRender 	= proxy.addArmor("Compressed Cobblestone"),
				carbRender	= proxy.addArmor("Heated Compressed Carbon"),
				wovnRender	= proxy.addArmor("Carbon Fiber"),
				pureRender	= proxy.addArmor("Advanced Carbon");
			
			boolean fancyglass = c.get(Configuration.CATEGORY_GENERAL, "connectedComGlassTextures", true).getBoolean(true);
			itemStuff = new ItemStuff(idCompact);
			
			furnace 	= new BlockCompactFurnace(	idFurnaces);
			blazeFurn	 = new BlockBlazeFurnace(	idBlazeFurn);
			comBlock 	= new BlockCompressed(		idCompressed);
			comGlass 	= new BlockComGlass(		idComGlass, fancyglass);			
			compactor 	= new BlockCompactor(		idCompactor);
			transmog	= new BlockTmog(			idTransmog);
			
			plantBall	= new ItemPlantBall(idPlantBall);
			carbon 		= new ItemCarbon(	idCarbon);
			
		 	comCobSword = new CompactSword(	idCobSword,	comCobToolMaterial, "dioritesword").setUnlocalizedName("comCobSword");
			comCobPick  = new CompactPick(	idCobPick,	comCobToolMaterial, "dioritepick").setUnlocalizedName("comCobPick");
			comCobHoe	= new CompactHoe(	idCobHoe,	comCobToolMaterial, "dioritehoe").setUnlocalizedName("comCobHoe");
			comCobAxe	= new CompactAxe(	idCobAxe,	comCobToolMaterial, "dioriteaxe").setUnlocalizedName("comCobAxe");
			comCobSpade	= new CompactSpade(	idCobSpade,	comCobToolMaterial, "dioritespade").setUnlocalizedName("comCobSpade");
			
			heatSword	= new CompactSword(	idHeatSword,heatCarbToolMaterial, "heatcarbsword").setUnlocalizedName("heatSword");
			heatPick	= new CompactPick(	idHeatPick,	heatCarbToolMaterial, "heatcarbpick").setUnlocalizedName("heatPick");
			heatHoe		= new CompactHoe(	idHeatHoe,	heatCarbToolMaterial, "heatcarbhoe").setUnlocalizedName("heatHoe");
			heatAxe		= new CompactAxe(	idHeatAxe,	heatCarbToolMaterial, "heatcarbaxe").setUnlocalizedName("heatAxe");
			heatSpade	= new CompactSpade(	idHeatSpade,heatCarbToolMaterial, "heatcarbspade").setUnlocalizedName("heatSpade");
			
			steelSword	= new CompactSword(	idSteelSword,	steelToolMaterial, "steelsword").setUnlocalizedName("csSteelSword");
			steelPick	= new CompactPick(	idSteelPick,	steelToolMaterial, "steelpick").setUnlocalizedName("csSteelPick");
			steelHoe	= new CompactHoe(	idSteelHoe,		steelToolMaterial, "steelhoe").setUnlocalizedName("csSteelHoe");
			steelAxe	= new CompactAxe(	idSteelAxe,		steelToolMaterial, "steelaxe").setUnlocalizedName("csSteelAxe");
			steelSpade	= new CompactSpade(	idSteelSpade,	steelToolMaterial, "steelspade").setUnlocalizedName("csSteelSpade");
			
			paxel		= new Paxel( idPaxel, paxelMaterial).setUnlocalizedName("compactPaxel");
			bagOfHolding= new ItemBagOfHolding(idBoH);
			smeltOnAStick=new SmeltOnAStick(idSmeltStick);
			
			cobHelmt = new ItemCompactArmor(idCobHelmt,comCobArmorMaterial,cobRender,0,"dioritehelm").setUnlocalizedName("helmtComCobble");
			cobPlate = new ItemCompactArmor(idCobPlate,comCobArmorMaterial,cobRender,1,"dioriteplate").setUnlocalizedName("plateComCobble");
			cobPants = new ItemCompactArmor(idCobPants,comCobArmorMaterial,cobRender,2,"dioritepants").setUnlocalizedName("pantsComCobble");
			cobBoots = new ItemCompactArmor(idCobBoots,comCobArmorMaterial,cobRender,3,"dioriteboots").setUnlocalizedName("bootsComCobble");
		
			carbHelmt = new ItemCompactArmor(idCarbHelmt,heatCarbArmorMaterial,carbRender,0,"heatcarbhelm").setUnlocalizedName("helmtHeatCarb");
			carbPlate = new ItemCompactArmor(idCarbPlate,heatCarbArmorMaterial,carbRender,1,"heatcarbplate").setUnlocalizedName("plateHeatCarb");
			carbPants = new ItemCompactArmor(idCarbPants,heatCarbArmorMaterial,carbRender,2,"heatcarbpants").setUnlocalizedName("pantsHeatCarb");
			carbBoots = new ItemCompactArmor(idCarbBoots,heatCarbArmorMaterial,carbRender,3,"heatcarbboots").setUnlocalizedName("bootsHeatCarb");
		
			wovnHelmt = new ItemCompactArmor(idWovnHelmt,wovnCarbArmorMaterial,wovnRender,0,"fiberhelm").setUnlocalizedName("helmtWovnCarb");
			wovnPlate = new ItemCompactArmor(idWovnPlate,wovnCarbArmorMaterial,wovnRender,1,"fiberplate").setUnlocalizedName("plateWovnCarb");
			wovnPants = new ItemCompactArmor(idWovnPants,wovnCarbArmorMaterial,wovnRender,2,"fiberpants").setUnlocalizedName("pantsWovnCarb");
			wovnBoots = new ItemCompactArmor(idWovnBoots,wovnCarbArmorMaterial,wovnRender,3,"fiberboots").setUnlocalizedName("bootsWovnCarb");
			
			pureHelmt = new ItemCompactArmor(idPureHelmt,pureCarbArmorMaterial,pureRender,0,"advancedhelm").setUnlocalizedName("helmtPureCarb");
			purePlate = new ItemCompactArmor(idPurePlate,pureCarbArmorMaterial,pureRender,1,"advancedplate").setUnlocalizedName("platePureCarb");
			purePants = new ItemCompactArmor(idPurePants,pureCarbArmorMaterial,pureRender,2,"advancedpants").setUnlocalizedName("pantsPureCarb");
			pureBoots = new ItemCompactArmor(idPureBoots,pureCarbArmorMaterial,pureRender,3,"advancedboots").setUnlocalizedName("bootsPureCarb");
			c.save();
		}
		
		@EventHandler
		public void init(FMLInitializationEvent e) {
			CompactRecipes.setUpRecipes();
			setUpTools();
			setUpArmor();
			
			MinecraftForge.EVENT_BUS.register(this);
			
			EntityRegistry.registerModEntity(EntityFallingCompact.class, "entityFallingCompact", 0, this, 64, 5, true);
			NetworkRegistry.instance().registerGuiHandler(this, proxy);
			TickRegistry.registerTickHandler(new CompactTickHandler(EnumSet.of(TickType.SERVER)), Side.SERVER);
			
			GameRegistry.registerFuelHandler(new CompactFuelHandler());
		}
		
		public void setUpTools() {
			setUpAToolSet(new Item[] {
				comCobSword,comCobPick,comCobAxe,comCobHoe,comCobSpade},
				new ItemStack(comBlock,1,Metas.DIORITE),"Diorite",2,null,null);
			setUpAToolSet(new Item[] {
				heatSword,heatPick,heatAxe,heatHoe,heatSpade},
				new ItemStack(carbon,1,4),"Metamorphic Carbon",3,Enchantment.fireAspect,Enchantment.fortune,new ItemStack(Item.blazeRod));
			setUpAToolSet(new Item[] {
				steelSword,steelPick,steelAxe,steelHoe,steelSpade},
				new ItemStack(itemStuff,1,ItemStuff.STEEL_INGOT),"CS Steel",2,null,null);
						
			//set up paxel, below:
			
			ItemStack paxel = new ItemStack(this.paxel);
			LanguageRegistry.addName(paxel, "CompactStuff Paxel");
			
			GameRegistry.addRecipe(paxel,"asp","cbc","cbc",
					'a', new ItemStack(Item.axeDiamond),
					'p', new ItemStack(Item.pickaxeDiamond),
					's', new ItemStack(Item.shovelDiamond),
					'c', ItemStuff.stack(ItemStuff.STEEL_INGOT),
					'b', new ItemStack(Item.blazeRod));
		}

		/**
		 * Sets up a tool set.
		 * 
		 * @param tools		Must be sword, pick, axe, hoe, spade in that order.
		 * @param material	The material the tools are made of (e.g. steel)
		 * @param name		The name of each tool prepended to its type (e.g. "CS Steel" Pickaxe)
		 * @param dmgval	The strength of the tool (0=wood, 1=stone, 2=iron, 3=diamond)
		 * @param swo		The enchantment to apply to the sword, if applicable.  May be null.
		 * @param too		The enchantment to apply to the pick, axe, and spade, if applicable.  May be null.
		 */
		public void setUpAToolSet(Item[] tools, ItemStack material, String name, int dmgval, Enchantment swo, Enchantment too) {
			setUpAToolSet(tools, material, name, dmgval, swo, too, null);
		}
		private void setUpAToolSet(Item[] tools, ItemStack material, String name, int dmgval, Enchantment swo, Enchantment too, ItemStack handle) {
			if(tools.length<5) return;
			MinecraftForge.setToolClass(tools[1], "pickaxe",	dmgval);
			MinecraftForge.setToolClass(tools[2], "axe",		dmgval);
			MinecraftForge.setToolClass(tools[4], "shovel",		dmgval);
			
			ItemStack[] toolstacks = {
					new ItemStack(tools[0]),
					new ItemStack(tools[1]),
					new ItemStack(tools[2]),
					new ItemStack(tools[3]),
					new ItemStack(tools[4]),
			};
			for(int i=0; i<toolnames.length; i++)
				LanguageRegistry.addName(toolstacks[i], name+" "+toolnames[i]);
			if(swo!=null) toolstacks[0].addEnchantment(swo, specEnchant);
			if(name.equals("Metamorphic Carbon")) toolstacks[0].addEnchantment(Enchantment.looting, specEnchant);
			if(too!=null) {
				toolstacks[1].addEnchantment(too, specEnchant);
				toolstacks[2].addEnchantment(too, specEnchant);
				toolstacks[4].addEnchantment(too, specEnchant);
			}
			addAllToolRecipes(toolstacks,material,handle);
		}
				
		private void addAllToolRecipes(ItemStack[] tools,ItemStack material,ItemStack handle) {
			if(tools.length<5) return;
			if(handle==null) addAllToolRecipes(tools[0],tools[1],tools[2],tools[3],tools[4],material);
			else addAllToolRecipes(tools[0],tools[1],tools[2],tools[3],tools[4],material,handle);
		}
		private void addAllToolRecipes(ItemStack a,ItemStack b,ItemStack c,ItemStack d,ItemStack e,ItemStack m) {
			ItemStack s = new ItemStack(Item.stick);
			addAllToolRecipes(a,b,c,d,e,m,s);
		}
		private void addAllToolRecipes(ItemStack a, ItemStack b, ItemStack c, ItemStack d, ItemStack e, ItemStack m, ItemStack s) {
			GameRegistry.addRecipe(a, " c "," c "," s ",'c',m,'s',s);
			GameRegistry.addRecipe(b, "ccc"," s "," s ",'c',m,'s',s);
			GameRegistry.addRecipe(c, "cc ","cs "," s ",'c',m,'s',s);
			GameRegistry.addRecipe(c, " cc"," sc"," s ",'c',m,'s',s);		
			GameRegistry.addRecipe(d, "cc "," s "," s ",'c',m,'s',s);
			GameRegistry.addRecipe(d, " cc"," s "," s ",'c',m,'s',s);
			GameRegistry.addRecipe(e, " c "," s "," s ",'c',m,'s',s);
		}
		public void setUpArmor() { 
			addEnchantedArmor(cobHelmt,cobPlate,cobPants,cobBoots,
				new ItemStack(comBlock,1,Metas.DIORITE), "Diorite",
				Enchantment.blastProtection);
			addEnchantedArmor(carbHelmt,carbPlate,carbPants,carbBoots,
				new ItemStack(carbon,1,4), "Metamorphic Carbon",
				Enchantment.fireProtection);
			setUpFibrArmor(); setUpAdvnArmor();
		}
		
		public void addEnchantedArmor(Item a, Item b, Item c, Item d, ItemStack material, String name, Enchantment enchant) {
			ItemStack[] armor = {
				new ItemStack(a),
				new ItemStack(b),
				new ItemStack(c),
				new ItemStack(d)
			}; 
			if(enchant!=null) {
				for(int i=0; i<armor.length; i++)
					armor[i].addEnchantment(enchant,specEnchant);
			}
			for(int i=0; i<armor.length; i++)
				LanguageRegistry.addName(armor[i], name+" "+armornames[i]);
			
			GameRegistry.addRecipe(armor[0],"ccc","c c",'c',material);
			GameRegistry.addRecipe(armor[1],"c c","ccc","ccc",'c',material);
			GameRegistry.addRecipe(armor[2],"ccc","c c","c c",'c',material);
			GameRegistry.addRecipe(armor[3],"c c","c c",'c',material);
		}
		
		public void setUpFibrArmor() {
			ItemStack[] armor = {
				new ItemStack(wovnHelmt),
				new ItemStack(wovnPlate),
				new ItemStack(wovnPants),
				new ItemStack(wovnBoots)
			};
			
			for(int i=0; i<armor.length; i++) 
				LanguageRegistry.addName(armor[i], "Carbon Fiber "+armornames[i]);
			
			ItemStack c = new ItemStack(carbon,1,6),
					i = new ItemStack(itemStuff,1,ItemStuff.IRON_PLATE);
			GameRegistry.addRecipe(armor[0],"ccc","cgc",		'c',c,'g',new ItemStack(Block.thinGlass));
			GameRegistry.addRecipe(armor[1],"i i","cic","ccc",	'c',c,'i',i);
			GameRegistry.addRecipe(armor[2],"ici","c c","c c",	'c',c,'i',i);
			GameRegistry.addRecipe(armor[3],"c c","c c",		'c',c);
		}
		public void setUpAdvnArmor() {
			ItemStack[] armor = { new ItemStack(pureHelmt),
				new ItemStack(purePlate),
				new ItemStack(purePants),
				new ItemStack(pureBoots) };
			
			for(int i=0; i<armor.length; i++)
				LanguageRegistry.addName(armor[i], "Carbon Alloy "+armornames[i]);
			
			ItemStack c = new ItemStack(carbon,1,6), //woven carbon
				p = new ItemStack(itemStuff,1,ItemStuff.DIAMOND_PLATE),
				d = new ItemStack(itemStuff,1,ItemStuff.ALLOY_PLATE),
				h = new ItemStack(carbon,1,4); //met carbon
			GameRegistry.addRecipe(armor[0],"dod","cgc",
				'd',d,'o',new ItemStack(wovnHelmt),
				'c',c,'g',new ItemStack(comGlass));
			GameRegistry.addRecipe(armor[1],"dod","cic","ccc",
				'd',d,'o',new ItemStack(wovnPlate),
				'c',c,'i',ItemStuff.stack(ItemStuff.GOLD_ALLOYED));
			GameRegistry.addRecipe(armor[2],"dod","c c","c c",
				'd',d,'c',c,
				'o',new ItemStack(wovnPants));
			GameRegistry.addRecipe(armor[3],"c c","dod",
				'd',d,'c',c,
				'o',new ItemStack(wovnBoots));
		}
		@ForgeSubscribe public void onEntityLivingFallEvent(LivingFallEvent evt) {
			ItemCompactArmor.onEntityLivingFallEvent(evt);
		}
		@ForgeSubscribe public boolean onEntityLivingAttackEvent(LivingAttackEvent evt) {
			if(!(evt.entity instanceof EntityPlayer)) return false;
			EntityPlayer player = (EntityPlayer)evt.entity;
			ItemStack boots = player.inventory.armorItemInSlot(0);
			if(boots==null) return false;
			if(boots.itemID==wovnBoots.itemID || boots.itemID==pureBoots.itemID) {
				boolean b = evt.source.equals(DamageSource.fall);
				evt.setCanceled(b);
				boots.damageItem(1, player);
				return b;
			}
			return false;
		}
}
