package compactstuff;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import compactstuff.boh.ItemBagOfHolding;
import compactstuff.client.RenderFallingCompressed;
import compactstuff.furnace.BlockCompactFurnace;
import compactstuff.furnace.ItemCompactFurnace;
import compactstuff.furnace.TileEntityCarbonFurnace;
import compactstuff.furnace.TileEntityCobbleFurnace;
import compactstuff.tools.CompactAxe;
import compactstuff.tools.CompactHoe;
import compactstuff.tools.CompactPick;
import compactstuff.tools.CompactSpade;
import compactstuff.tools.CompactSword;
import compactstuff.tools.Paxel;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
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

@Mod(modid="robertwan_compactstuff", name="CompactStuff", version="1.4.7")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class CompactStuff {
		@Instance("robertwan_compactstuff")
		public static CompactStuff instance;
		
		@SidedProxy(clientSide="compactstuff.client.ClientProxy", serverSide="compactstuff.CommonProxy")
		public static CommonProxy proxy;
				
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
			itemStuff;

		public static Block comBlock, furnace, comGlass;	
		
		public static CreativeTabs compactTab;
		
		private final int specEnchant = 5;
		private final String[] toolnames = {"Sword","Pickaxe","Axe","Hoe","Shovel",};
		private final String[] armornames = {"Helmet", "Chestplate", "Leggings", "Boots"};
		
		@PreInit
		public void preInit(FMLPreInitializationEvent e) {
			proxy.registerRenderers();
			
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
								
				cobRender 	= proxy.addArmor("Compressed Cobblestone"),
				carbRender	= proxy.addArmor("Heated Compressed Carbon"),
				wovnRender	= proxy.addArmor("Carbon Fiber"),
				pureRender	= proxy.addArmor("Advanced Carbon");
			
			boolean fancyglass = c.get(Configuration.CATEGORY_GENERAL, "connectedComGlassTextures", true).getBoolean(true);
			itemStuff = new ItemStuff(idCompact);
			
			furnace = new BlockCompactFurnace(idFurnaces,16)
				.setHardness(3.5f).setStepSound(Block.soundStoneFootstep)
				.setBlockName("compactfurnace").setCreativeTab(compactTab);
			
			comBlock = new BlockCompressed(	idCompressed);
			comGlass = new BlockComGlass(	idComGlass, fancyglass);
			
			plantBall	= new ItemPlantBall(idPlantBall);
			carbon 		= new ItemCarbon(	idCarbon);
			
		 	comCobSword = new CompactSword(	idCobSword,	comCobToolMaterial, 15).setItemName("comCobSword");
			comCobPick  = new CompactPick(	idCobPick,	comCobToolMaterial, 14).setItemName("comCobPick");
			comCobHoe	= new CompactHoe(	idCobHoe,	comCobToolMaterial, 13).setItemName("comCobHoe");
			comCobAxe	= new CompactAxe(	idCobAxe,	comCobToolMaterial, 12).setItemName("comCobAxe");
			comCobSpade	= new CompactSpade(	idCobSpade,	comCobToolMaterial, 11).setItemName("comCobSpade");
			
			heatSword	= new CompactSword(	idHeatSword,heatCarbToolMaterial, 31).setItemName("heatSword");
			heatPick	= new CompactPick(	idHeatPick,	heatCarbToolMaterial, 30).setItemName("heatPick");
			heatHoe		= new CompactHoe(	idHeatHoe,	heatCarbToolMaterial, 29).setItemName("heatHoe");
			heatAxe		= new CompactAxe(	idHeatAxe,	heatCarbToolMaterial, 28).setItemName("heatAxe");
			heatSpade	= new CompactSpade(	idHeatSpade,heatCarbToolMaterial, 27).setItemName("heatSpade");
			
			steelSword	= new CompactSword(	idSteelSword,	steelToolMaterial, 47).setItemName("csSteelSword");
			steelPick	= new CompactPick(	idSteelPick,	steelToolMaterial, 46).setItemName("csSteelPick");
			steelHoe	= new CompactHoe(	idSteelHoe,		steelToolMaterial, 45).setItemName("csSteelHoe");
			steelAxe	= new CompactAxe(	idSteelAxe,		steelToolMaterial, 44).setItemName("csSteelAxe");
			steelSpade	= new CompactSpade(	idSteelSpade,	steelToolMaterial, 43).setItemName("csSteelSpade");
			
			paxel		= new Paxel( idPaxel, paxelMaterial).setIconIndex(6).setItemName("compactPaxel");
			bagOfHolding= new ItemBagOfHolding(idBoH);
			
			cobHelmt = new ItemCompactArmor(idCobHelmt,comCobArmorMaterial,cobRender,0).setIconIndex(7).setItemName("helmtComCobble");
			cobPlate = new ItemCompactArmor(idCobPlate,comCobArmorMaterial,cobRender,1).setIconIndex(8).setItemName("plateComCobble");
			cobPants = new ItemCompactArmor(idCobPants,comCobArmorMaterial,cobRender,2).setIconIndex(9).setItemName("pantsComCobble");
			cobBoots = new ItemCompactArmor(idCobBoots,comCobArmorMaterial,cobRender,3).setIconIndex(10).setItemName("bootsComCobble");
		
			carbHelmt = new ItemCompactArmor(idCarbHelmt,heatCarbArmorMaterial,carbRender,0).setIconIndex(23).setItemName("helmtHeatCarb");
			carbPlate = new ItemCompactArmor(idCarbPlate,heatCarbArmorMaterial,carbRender,1).setIconIndex(24).setItemName("plateHeatCarb");
			carbPants = new ItemCompactArmor(idCarbPants,heatCarbArmorMaterial,carbRender,2).setIconIndex(25).setItemName("pantsHeatCarb");
			carbBoots = new ItemCompactArmor(idCarbBoots,heatCarbArmorMaterial,carbRender,3).setIconIndex(26).setItemName("bootsHeatCarb");
		
			wovnHelmt = new ItemCompactArmor(idWovnHelmt,wovnCarbArmorMaterial,wovnRender,0).setIconIndex(48).setItemName("helmtWovnCarb");
			wovnPlate = new ItemCompactArmor(idWovnPlate,wovnCarbArmorMaterial,wovnRender,1).setIconIndex(49).setItemName("plateWovnCarb");
			wovnPants = new ItemCompactArmor(idWovnPants,wovnCarbArmorMaterial,wovnRender,2).setIconIndex(50).setItemName("pantsWovnCarb");
			wovnBoots = new ItemCompactArmor(idWovnBoots,wovnCarbArmorMaterial,wovnRender,3).setIconIndex(51).setItemName("bootsWovnCarb");
			
			pureHelmt = new ItemCompactArmor(idPureHelmt,pureCarbArmorMaterial,pureRender,0).setIconIndex(52).setItemName("helmtPureCarb");
			purePlate = new ItemCompactArmor(idPurePlate,pureCarbArmorMaterial,pureRender,1).setIconIndex(53).setItemName("platePureCarb");
			purePants = new ItemCompactArmor(idPurePants,pureCarbArmorMaterial,pureRender,2).setIconIndex(54).setItemName("pantsPureCarb");
			pureBoots = new ItemCompactArmor(idPureBoots,pureCarbArmorMaterial,pureRender,3).setIconIndex(55).setItemName("bootsPureCarb");
			c.save();
		}
		
		@Init
		public void init(FMLInitializationEvent e) {
			setUpPlantBalls();
			setUpcomBlocks();
			setUpFurnaces();
			setUpCarbon();
			setUpBagOfHolding();
			setUpTools();
			setUpArmor();
			setUpGlassAndStuff();
			
			MinecraftForge.EVENT_BUS.register(this);
			
			EntityRegistry.registerModEntity(EntityFallingCompact.class, "entityFallingCompact", 0, this, 64, 5, true);
			NetworkRegistry.instance().registerGuiHandler(this, proxy);
			TickRegistry.registerTickHandler(new CompactTickHandler(EnumSet.of(TickType.SERVER)), Side.SERVER);
			
			ItemStack rack = new ItemStack(Block.netherrack);
			GameRegistry.addShapelessRecipe(new ItemStack(Block.netherBrick),rack,rack,rack,rack);
			GameRegistry.addShapelessRecipe(new ItemStack(Block.netherrack,4),new ItemStack(Block.netherBrick));
			
			GameRegistry.addShapelessRecipe(new ItemStack(Item.coal),
					new ItemStack(Item.coal,1,1), //charcoal
					new ItemStack(Block.cobblestone));
			GameRegistry.addShapelessRecipe(new ItemStack(Item.coal,32),new ItemStack(Item.diamond));
			
			GameRegistry.registerFuelHandler(new CompactFuelHandler());
		}
		
		private void setUpGlassAndStuff() {
			ItemStack slag 	= new ItemStack(itemStuff,1,ItemStuff.GLASS_SLAG),
					fibr	= new ItemStack(itemStuff,1,ItemStuff.GLASS_FIBER),
					dplt	= new ItemStack(itemStuff,1,ItemStuff.DIAMOND_PLATE),
					ally	= new ItemStack(itemStuff,1,ItemStuff.ALLOY_PLATE),
					iron	= new ItemStack(itemStuff,1,ItemStuff.IRON_PLATE),
					splt	= new ItemStack(itemStuff,1,ItemStuff.STEEL_PLATE),
					stel	= new ItemStack(itemStuff,1,ItemStuff.STEEL_INGOT),
					stack;
			
			for(int i=0; i<ItemStuff.names.length; i++) {
				if(ItemStuff.names[i]==null) continue;
				stack = new ItemStack(itemStuff,1,i);
				LanguageRegistry.addName(stack, stack.getItem().getItemNameIS(stack));
			}			
			GameRegistry.addRecipe(fibr,"xxx",'x',slag);
			GameRegistry.addRecipe(ally,"sds","dhd","sds",
					's',splt,
					'd',dplt,
					'h',new ItemStack(carbon,1,4));
			
			GameRegistry.addShapelessRecipe(new ItemStack(itemStuff,5,ItemStuff.DIAMOND_PLATE),
					new ItemStack(comBlock,1,Metas.COMDIAMOND));
			GameRegistry.addShapelessRecipe(new ItemStack(Item.diamond,16),dplt);
			
			GameRegistry.addShapelessRecipe(new ItemStack(itemStuff,5,ItemStuff.IRON_PLATE),
					new ItemStack(comBlock,1,Metas.COMIRON));
			GameRegistry.addShapelessRecipe(new ItemStack(Item.ingotIron,16),iron);
			
			GameRegistry.addShapelessRecipe(new ItemStack(itemStuff,5,ItemStuff.STEEL_PLATE),
					new ItemStack(comBlock,1,Metas.COMSTEEL));
			GameRegistry.addShapelessRecipe(new ItemStack(itemStuff,16,ItemStuff.STEEL_INGOT),splt);
			
			
			GameRegistry.addShapelessRecipe(stel, new ItemStack(Item.ingotIron), new ItemStack(carbon,1,1));
			
			FurnaceRecipes.smelting().addSmelting(slag.itemID,ItemStuff.GLASS_SLAG,new ItemStack(Block.thinGlass),0f);
		}

		public void setUpcomBlocks() {
			GameRegistry.registerBlock(comBlock, ItemBlockCompressed.class, "Compressed Block");
			GameRegistry.registerBlock(comGlass, "Compressed Glass");
			
			MinecraftForge.setBlockHarvestLevel(comBlock,  "pickaxe", 1);
			
			for(Integer i : BlockCompressed.names.keySet())
				LanguageRegistry.addName(new ItemStack(comBlock,1,i),BlockCompressed.names.get(i));
			LanguageRegistry.addName(comGlass, "Compressed Glass");
			
			ItemStack 
				blockCoal = new ItemStack(comBlock,1,Metas.COMCOAL),
				blockReds = new ItemStack(comBlock,1,Metas.COMREDSTONE),
				comIron   = new ItemStack(comBlock,1,Metas.COMIRON),
				blckSteel = new ItemStack(comBlock,1,Metas.STEELBLOCK),
				comSteeel = new ItemStack(comBlock,1,Metas.COMSTEEL),
				cmDiorite = new ItemStack(comBlock,1,Metas.DIORITE),
				comCobble = new ItemStack(comBlock,1,Metas.COMCOBBLE),
				comDiamnd = new ItemStack(comBlock,1,Metas.COMDIAMOND),
				comNeRack = new ItemStack(comBlock,1,Metas.COMRACK),
				comSand	  = new ItemStack(comBlock,1,Metas.COMSAND),
				comDirt	  = new ItemStack(comBlock,1,Metas.COMDIRT),
				comGravel = new ItemStack(comBlock,1,Metas.COMGRAVEL),
				stackGlass= new ItemStack(comGlass,3),
				coal 	  = new ItemStack(Item.coal),
				blockIron = new ItemStack(Block.blockSteel),
				cobb = new ItemStack(Block.cobblestone),
				rack = new ItemStack(Block.netherrack),
				stel = new ItemStack(itemStuff,1,ItemStuff.STEEL_INGOT);
			
			GameRegistry.addShapelessRecipe(blockCoal,coal,coal,coal,coal,coal,coal,coal,coal);
			GameRegistry.addShapelessRecipe(new ItemStack(Item.coal,8),blockCoal);
			
			GameRegistry.addRecipe(blockReds,"rrr","rrr","rrr",'r',new ItemStack(Item.redstone));
			GameRegistry.addShapelessRecipe(new ItemStack(Item.redstone,9),blockReds);
			
			GameRegistry.addRecipe(comIron,"iii","iii","iii",'i',blockIron);
			//plates in setUpGlassAndStuff
			
			GameRegistry.addRecipe(blckSteel,"sss","sss","sss",'s',stel);
			GameRegistry.addShapelessRecipe(new ItemStack(itemStuff,9,ItemStuff.STEEL_INGOT),blckSteel);
			
			GameRegistry.addRecipe(comSteeel,"sss","sss","sss",'s',blckSteel);
			//plates in setUpGlassAndStuff
			
			GameRegistry.addRecipe(comDiamnd,"ddd","ddd","ddd",'d',new ItemStack(Block.blockDiamond));
			//plates in setUpGlassAndStuff
			
			GameRegistry.addRecipe(cmDiorite,"ccc","cic","ccc",'c',comCobble,'i',new ItemStack(Item.ingotIron));
			GameRegistry.addShapelessRecipe(new ItemStack(comBlock,8,Metas.COMCOBBLE),cmDiorite);
			
			GameRegistry.addShapelessRecipe(comCobble,cobb,cobb,cobb,cobb,cobb,cobb,cobb,cobb);
			GameRegistry.addShapelessRecipe(new ItemStack(Block.cobblestone,8),comCobble);
			
			GameRegistry.addShapelessRecipe(comNeRack,	rack,rack,rack,rack,rack,rack,rack,rack);			
			GameRegistry.addShapelessRecipe(new ItemStack(Block.netherrack,8),comNeRack);
			
			GameRegistry.addRecipe(comDirt,"ddd","ddd","ddd",'d',new ItemStack(Block.dirt));
			GameRegistry.addRecipe(comSand,"sss","sss","sss",'s',new ItemStack(Block.sand));
			GameRegistry.addRecipe(comGravel,"ggg","ggg","ggg",'g',new ItemStack(Block.gravel));
			
			GameRegistry.addRecipe(stackGlass,"ici","ggg","ici",'g',new ItemStack(Block.glass),
					'c',comCobble,'i',new ItemStack(Item.ingotIron));
		}
		
		public void setUpPlantBalls() {
			LanguageRegistry.addName(new ItemStack(plantBall,1,0), "Oak Sapling Ball");
			LanguageRegistry.addName(new ItemStack(plantBall,1,1), "Spruce Sapling Ball");
			LanguageRegistry.addName(new ItemStack(plantBall,1,2), "Birch Sapling Ball");
			LanguageRegistry.addName(new ItemStack(plantBall,1,3), "Jungle Sapling Ball");
			LanguageRegistry.addName(new ItemStack(plantBall,1,4), "Seed Ball");
			
			ItemStack seeds = new ItemStack(Item.seeds);
			ItemStack oak = new ItemStack(Block.sapling,1,0);
			ItemStack spruce = new ItemStack(Block.sapling,1,1);
			ItemStack birch = new ItemStack(Block.sapling,1,2);
			ItemStack jungle = new ItemStack(Block.sapling,1,3);
			
			
			GameRegistry.addShapelessRecipe(new ItemStack(plantBall,1,0),
					oak,oak,oak,oak,oak,oak,oak,oak);
			GameRegistry.addShapelessRecipe(new ItemStack(plantBall,1,1),
					spruce,spruce,spruce,spruce,spruce,spruce,spruce,spruce);
			GameRegistry.addShapelessRecipe(new ItemStack(plantBall,1,2),
					birch,birch,birch,birch,birch,birch,birch,birch);
			GameRegistry.addShapelessRecipe(new ItemStack(plantBall,1,3),
					jungle,jungle,jungle,jungle,jungle,jungle,jungle,jungle);
			GameRegistry.addShapelessRecipe(new ItemStack(plantBall,1,4),
					seeds,seeds,seeds,seeds,seeds,seeds,seeds,seeds);
			

			GameRegistry.addShapelessRecipe(new ItemStack(Block.sapling,8,0),new ItemStack(plantBall,1,0));
			GameRegistry.addShapelessRecipe(new ItemStack(Block.sapling,8,1),new ItemStack(plantBall,1,1));
			GameRegistry.addShapelessRecipe(new ItemStack(Block.sapling,8,2),new ItemStack(plantBall,1,2));
			GameRegistry.addShapelessRecipe(new ItemStack(Block.sapling,8,3),new ItemStack(plantBall,1,3));
			GameRegistry.addShapelessRecipe(new ItemStack(Item.seeds,8),new ItemStack(plantBall,1,4));	
		}
		
		
		
		public void setUpFurnaces() {
			GameRegistry.registerBlock(furnace, ItemCompactFurnace.class, "Compact Furnace");
			GameRegistry.registerTileEntity(TileEntityCobbleFurnace.class, "compactstuff.cobblefurnace");
			GameRegistry.registerTileEntity(TileEntityCarbonFurnace.class, "compactstuff.carbonfurnace");
			MinecraftForge.setBlockHarvestLevel(furnace,	"pickaxe", 0);
			
			ItemStack c = new ItemStack(comBlock,1,Metas.COMCOBBLE),
					f	= new ItemStack(Block.stoneOvenIdle),
					n	= new ItemStack(this.carbon,1,4),
				comp 	= new ItemStack(furnace,1,2),
				carb 	= new ItemStack(furnace,1,10);
			
			LanguageRegistry.addName(comp, "Compression Furnace");
			LanguageRegistry.addName(carb, "Carbon Furnace");
						
			GameRegistry.addRecipe(comp, "ccc","c c","ccc",'c',c);
			GameRegistry.addRecipe(comp, "fff","f f","fff",'f',f);
			GameRegistry.addRecipe(carb ,"nnn","nfn","nnn",'n',n,'f',comp);
		}
		
		public void setUpCarbon() {
			ItemStack wafe = new ItemStack(carbon,1,0),
				carb = new ItemStack(carbon,1,1),
				dens = new ItemStack(carbon,1,2),
				comp = new ItemStack(carbon,1,3),
				heat = new ItemStack(carbon,1,4),
				fibr = new ItemStack(carbon,1,5),
				fibr2= new ItemStack(carbon,2,5),
				wovn = new ItemStack(carbon,1,6);
			
			LanguageRegistry.addName(wafe, "Impure Carbon");
			LanguageRegistry.addName(carb, "Carbon");
			LanguageRegistry.addName(dens, "Dense Carbon");	
			LanguageRegistry.addName(comp, "Compressed Carbon");
			LanguageRegistry.addName(heat, "Metamorphic Carbon");
			LanguageRegistry.addName(fibr, "Carbon Fiber");
			LanguageRegistry.addName(wovn, "Woven Carbon Fiber");

			
			ItemStack coal = new ItemStack(Item.coal);
			GameRegistry.addShapelessRecipe(wafe,coal,coal,coal,coal);
			FurnaceRecipes.smelting().addSmelting(wafe.itemID,0,carb,3.0f);
			
			GameRegistry.addShapelessRecipe(dens, carb,carb,carb,carb,carb,carb,carb,carb);
			GameRegistry.addRecipe(comp," x ","xyx"," x ",'x',new ItemStack(comBlock,1,Metas.COMCOBBLE),'y',dens);
			GameRegistry.addRecipe(heat," x ","xyx"," x ",'x',new ItemStack(comBlock,1,Metas.COMRACK),'y',comp);
			GameRegistry.addRecipe(fibr2,"gcg","csc","gcg",
					'g',new ItemStack(itemStuff,1,ItemStuff.GLASS_FIBER), 'c', carb, 's', new ItemStack(Item.slimeBall));
			GameRegistry.addRecipe(fibr2,"cgc","gsg","cgc",
					'g',new ItemStack(itemStuff,1,ItemStuff.GLASS_FIBER), 'c', carb, 's', new ItemStack(Item.slimeBall));
			GameRegistry.addRecipe(wovn,"xx","xx",'x',fibr);
			FurnaceRecipes.smelting().addSmelting(heat.itemID,4,new ItemStack(Item.diamond),15.0f);
		}
		
		public void setUpBagOfHolding() {
			ItemStack bag = new ItemStack(this.bagOfHolding,1,0);
			LanguageRegistry.addName(bag, "Bag of Holding");
			
			GameRegistry.addRecipe(bag, "fef", "fnf", "fff",
					'f', new ItemStack(this.carbon,1,6),
					'e', new ItemStack(Item.emerald),
					'n', new ItemStack(Item.eyeOfEnder));
			
			for(int bagMeta = 0; bagMeta<16; bagMeta++) {
				for(int dyeMeta = 0; dyeMeta<16; dyeMeta++) {
					int newbag = ItemBagOfHolding.COLOR_CRAFTING[dyeMeta][bagMeta];
					if(newbag!=-1) {
						GameRegistry.addShapelessRecipe(new ItemStack(this.bagOfHolding,1,newbag),
							new ItemStack(this.bagOfHolding,1,bagMeta),
							new ItemStack(Item.dyePowder,1,dyeMeta));
					}
				}
			}
		}
		public void setUpTools() {
			//setUpCobbleTools();
			//setUpHeatTools();
			//setUpSteelTools();
			setUpAToolSet(new Item[] {
				comCobSword,comCobPick,comCobAxe,comCobHoe,comCobSpade},
				new ItemStack(comBlock,1,Metas.DIORITE),"Diorite",2,null,null);
			setUpAToolSet(new Item[] {
				heatSword,heatPick,heatAxe,heatHoe,heatSpade},
				new ItemStack(carbon,1,4),"Metamorphic Carbon",3,Enchantment.fireAspect,Enchantment.fortune);
			setUpAToolSet(new Item[] {
				steelSword,steelPick,steelAxe,steelHoe,steelSpade},
				new ItemStack(itemStuff,1,ItemStuff.STEEL_INGOT),"CS Steel",2,null,null);
						
			//set up paxel, below:
			
			ItemStack paxel = new ItemStack(this.paxel);
			LanguageRegistry.addName(paxel, "CompactStuff Paxel");
			
			ItemStack pick 	= new ItemStack(Item.pickaxeDiamond);
			ItemStack axes 	= new ItemStack(Item.axeDiamond);
			ItemStack shvl	= new ItemStack(Item.shovelDiamond);
			ItemStack carb	= new ItemStack(itemStuff,1,ItemStuff.STEEL_INGOT);
			ItemStack blze	= new ItemStack(Item.blazeRod);

			GameRegistry.addRecipe(paxel,"asp","cbc","cbc",
					'a', axes, 'p', pick, 's', shvl, 'c', carb, 'b', blze);
		}

		/**
		 * Sets up a tool set.
		 * 
		 * @param tools		Must be sword, pick, axe, hoe, spade in that order.
		 * @param material	The material the tools are made of (e.g. steel)
		 * @param name		The name of each tool prepended to its type (e.g. "CS Steel" Pickaxe)
		 * @param dmgval	The strength of the tool (0=wood, 1=stone, 2=iron, 3=diamond)
		 */
		public void setUpAToolSet(Item[] tools, ItemStack material, String name, int dmgval, Enchantment swo, Enchantment too) {
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
			if(too!=null) {
				toolstacks[1].addEnchantment(too, specEnchant);
				toolstacks[2].addEnchantment(too, specEnchant);
				toolstacks[4].addEnchantment(too, specEnchant);
			}
			addAllToolRecipes(toolstacks,material);
		}
				
		private void addAllToolRecipes(ItemStack[] tools,ItemStack material) {
			if(tools.length<5) return;
			addAllToolRecipes(tools[0],tools[1],tools[2],tools[3],tools[4],material);
		}
		private void addAllToolRecipes(ItemStack a,ItemStack b,ItemStack c,ItemStack d,ItemStack e,ItemStack m) {
			ItemStack s = new ItemStack(Item.stick);
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
			GameRegistry.addRecipe(armor[0],"sos","cpc",
				's',new ItemStack(itemStuff,1,ItemStuff.STEEL_PLATE),
				'o',new ItemStack(wovnHelmt),
				'c',c,'p',p);
			GameRegistry.addRecipe(armor[1],"d d","coc","cdc",
				'd',d,'c',c,
				'o',new ItemStack(wovnPlate));
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
