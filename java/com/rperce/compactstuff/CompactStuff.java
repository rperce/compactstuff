package com.rperce.compactstuff;

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

@Mod(modid = "compactstuff", name = "CompactStuff", version = "1.5.2")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class, channels = {
        Metas.CH_COMPCRAFT, Metas.CH_COMPOUT, Metas.CH_COMPMAKE })
public class CompactStuff {
    @Instance("compactstuff")
    public static CompactStuff instance;

    @SidedProxy(clientSide = "com.rperce.compactstuff.client.ClientProxy", serverSide = "com.rperce.compactstuff.CommonProxy")
    public static CommonProxy  proxy;

    public static Random       rand = new Random();

    public static EnumToolMaterial dioriteToolMaterial = EnumHelper
                                                               .addToolMaterial(
                                                                       "dioriteTool",
                                                                       2, 512,
                                                                       6.5f, 3,
                                                                       15),
            steelToolMaterial = EnumHelper.addToolMaterial("csSteelTool", 2,
                    1024, 7.5f, 4, 17),
            metCarbToolMaterial = EnumHelper.addToolMaterial("heatCarbTool", 3,
                    2048, 8.5f, 5, 20),
            paxelMaterial = EnumHelper.addToolMaterial("compactPaxelTool", 3,
                    4096, 10.5f, 9, 0);

    public static EnumArmorMaterial comCobArmorMaterial = EnumHelper
                                                                .addArmorMaterial(
                                                                        "Diorite Armor",
                                                                        16,
                                                                        new int[] {
            2, 6, 5, 2                                                 }, 10),
            heatCarbArmorMaterial = EnumHelper.addArmorMaterial(
                    "heatCarbArmor", 35, new int[] { 4, 8, 7, 4 }, 25),
            wovnCarbArmorMaterial = EnumHelper.addArmorMaterial(
                    "wovnCarbArmor", 40, new int[] { 6, 10, 9, 6 }, 0),
            pureCarbArmorMaterial = EnumHelper.addArmorMaterial(
                    "pureCarbArmor", 50, new int[] { 10, 10, 10, 10 }, 0);

    public static Item              plantBall, carbon,
                                    // paxel,
            bagOfHolding, itemStuff, smeltOnAStick;

    public static Block             comBlock, furnace, blazeFurn, comGlass,
            compactor, transmog;

    public static CreativeTabs      compactTab;

    private final int               specEnchant         = 5;
    private final String[]          toolnames           = { "Sword", "Pickaxe",
            "Axe", "Hoe", "Shovel",                    };
    private final String[]          armornames          = { "Helmet",
            "Chestplate", "Leggings", "Boots"          };

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.registerRenderers();

        compactTab = new CreativeTabs("compactTab") {
            @Override
            public ItemStack getIconItemStack() {
                return new ItemStack(comBlock, 1, Metas.COMCOBBLE);
            }

            @Override
            public String getTranslatedTabLabel() {
                return "Compact Stuff";
            }
        };

        Configuration c = new Configuration(e.getSuggestedConfigurationFile());
        c.load();

        int idCompressed = c.getBlock("compressed", 500).getInt(), idFurnaces = c
                .getBlock("furnaces", 501).getInt(), idComGlass = c.getBlock(
                "comglass", 502).getInt(), idCompactor = c.getBlock(
                "compactor", 503).getInt(), idTransmog = c.getBlock(
                "transmogrifier", 504).getInt(), idBlazeFurn = c.getBlock(
                "blazeFurnace", 505).getInt(),

        idPlantBall = c.getItem("plantBall", 9337).getInt(), idCarbon = c
                .getItem("carbon", 9338).getInt(),

        idBoH = c.getItem("bagOfHolding", 9366).getInt(), idCompact = c
                .getItem("assortedItems", 9367).getInt(), idSmeltStick = c
                .getItem("smeltOnAStick", 9373).getInt();
        boolean fancyglass = c.get(Configuration.CATEGORY_GENERAL,
                "connectedComGlassTextures", true).getBoolean(true);
        itemStuff = new ItemStuff(idCompact);

        furnace = new BlockCompactFurnace(idFurnaces);
        blazeFurn = new BlockBlazeFurnace(idBlazeFurn);
        comBlock = new BlockCompressed(idCompressed);
        comGlass = new BlockComGlass(idComGlass, fancyglass);
        compactor = new BlockCompactor(idCompactor);
        transmog = new BlockTmog(idTransmog);

        plantBall = new ItemPlantBall(idPlantBall);
        carbon = new ItemCarbon(idCarbon);

        for (Ref r : Ref.values())
            r.resolve(c, proxy);

        bagOfHolding = new ItemBagOfHolding(idBoH);
        smeltOnAStick = new SmeltOnAStick(idSmeltStick);

        c.save();
    }

    /**
     * 
     * @param e
     *            Parameter to make this an init event
     */
    @EventHandler
    public void init(FMLInitializationEvent e) {
        CompactRecipes.setUpRecipes();
        setUpTools();
        setUpArmor();

        MinecraftForge.EVENT_BUS.register(this);

        EntityRegistry.registerModEntity(EntityFallingCompact.class,
                "entityFallingCompact", 0, this, 64, 5, true);
        NetworkRegistry.instance().registerGuiHandler(this, proxy);
        TickRegistry.registerTickHandler(
                new CompactTickHandler(EnumSet.of(TickType.SERVER)),
                Side.SERVER);

        GameRegistry.registerFuelHandler(new CompactFuelHandler());
    }

    public void setUpTools() {
        setUpAToolSet(new Item[] { Ref.DIORITE_SWORD.item,
                Ref.DIORITE_PICK.item, Ref.DIORITE_AXE.item,
                Ref.DIORITE_HOE.item, Ref.DIORITE_SPADE.item }, new ItemStack(
                comBlock, 1, Metas.DIORITE), "Diorite", 2, null, null);
        setUpAToolSet(new Item[] { Ref.METCARB_SWORD.item,
                Ref.METCARB_PICK.item, Ref.METCARB_AXE.item,
                Ref.METCARB_HOE.item, Ref.METCARB_SPADE.item },
                // heatSword,heatPick,heatAxe,heatHoe,heatSpade},
                new ItemStack(carbon, 1, 4), "Metamorphic Carbon", 3,
                Enchantment.fireAspect, Enchantment.fortune, new ItemStack(
                        Item.blazeRod));
        setUpAToolSet(new Item[] { Ref.STEEL_SWORD.item, Ref.STEEL_PICK.item,
                Ref.STEEL_AXE.item, Ref.STEEL_HOE.item, Ref.STEEL_SPADE.item },
                // steelSword,steelPick,steelAxe,steelHoe,steelSpade},
                new ItemStack(itemStuff, 1, ItemStuff.STEEL_INGOT), "CS Steel",
                2, null, null);

        // set up paxel, below:

        ItemStack paxel = Ref.PAXEL.stack();
        LanguageRegistry.addName(paxel, "CompactStuff Paxel");

        GameRegistry.addRecipe(paxel, "asp", "cbc", "cbc", 'a', new ItemStack(
                Item.axeDiamond), 'p', new ItemStack(Item.pickaxeDiamond), 's',
                new ItemStack(Item.shovelDiamond), 'c', ItemStuff
                        .stack(ItemStuff.STEEL_INGOT), 'b', new ItemStack(
                        Item.blazeRod));
    }

    /**
     * Sets up a tool set.
     * 
     * @param tools
     *            Must be sword, pick, axe, hoe, spade in that order.
     * @param material
     *            The material the tools are made of (e.g. steel)
     * @param name
     *            The name of each tool prepended to its type (e.g. "CS Steel"
     *            Pickaxe)
     * @param dmgval
     *            The strength of the tool (0=wood, 1=stone, 2=iron, 3=diamond)
     * @param swo
     *            The enchantment to apply to the sword, if applicable. May be
     *            null.
     * @param too
     *            The enchantment to apply to the pick, axe, and spade, if
     *            applicable. May be null.
     */
    public void setUpAToolSet(Item[] tools, ItemStack material, String name,
            int dmgval, Enchantment swo, Enchantment too) {
        setUpAToolSet(tools, material, name, dmgval, swo, too, null);
    }

    private void setUpAToolSet(Item[] tools, ItemStack material, String name,
            int dmgval, Enchantment swo, Enchantment too, ItemStack handle) {
        if (tools.length < 5) return;
        MinecraftForge.setToolClass(tools[1], "pickaxe", dmgval);
        MinecraftForge.setToolClass(tools[2], "axe", dmgval);
        MinecraftForge.setToolClass(tools[4], "shovel", dmgval);

        ItemStack[] toolstacks = { new ItemStack(tools[0]),
                new ItemStack(tools[1]), new ItemStack(tools[2]),
                new ItemStack(tools[3]), new ItemStack(tools[4]), };
        for (int i = 0; i < this.toolnames.length; i++)
            LanguageRegistry.addName(toolstacks[i], name + " "
                    + this.toolnames[i]);
        if (swo != null) toolstacks[0].addEnchantment(swo, this.specEnchant);
        if (name.equals("Metamorphic Carbon"))
            toolstacks[0].addEnchantment(Enchantment.looting, this.specEnchant);
        if (too != null) {
            toolstacks[1].addEnchantment(too, this.specEnchant);
            toolstacks[2].addEnchantment(too, this.specEnchant);
            toolstacks[4].addEnchantment(too, this.specEnchant);
        }
        addAllToolRecipes(toolstacks, material, handle);
    }

    private static void addAllToolRecipes(ItemStack[] tools,
            ItemStack material, ItemStack handle) {
        if (tools.length < 5) return;
        if (handle == null)
            addAllToolRecipes(tools[0], tools[1], tools[2], tools[3], tools[4],
                    material);
        else
            addAllToolRecipes(tools[0], tools[1], tools[2], tools[3], tools[4],
                    material, handle);
    }

    private static void addAllToolRecipes(ItemStack a, ItemStack b,
            ItemStack c, ItemStack d, ItemStack e, ItemStack m) {
        ItemStack s = new ItemStack(Item.stick);
        addAllToolRecipes(a, b, c, d, e, m, s);
    }

    private static void addAllToolRecipes(ItemStack a, ItemStack b,
            ItemStack c, ItemStack d, ItemStack e, ItemStack m, ItemStack s) {
        GameRegistry.addRecipe(a, " c ", " c ", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(b, "ccc", " s ", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(c, "cc ", "cs ", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(c, " cc", " sc", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(d, "cc ", " s ", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(d, " cc", " s ", " s ", 'c', m, 's', s);
        GameRegistry.addRecipe(e, " c ", " s ", " s ", 'c', m, 's', s);
    }

    public void setUpArmor() {
        addEnchantedArmor(Ref.DIORITE_HELM.item, Ref.DIORITE_PLATE.item,
                Ref.DIORITE_PANTS.item, Ref.DIORITE_BOOTS.item, new ItemStack(
                        comBlock, 1, Metas.DIORITE), "Diorite",
                Enchantment.blastProtection);
        addEnchantedArmor(Ref.METCARB_HELM.item, Ref.METCARB_PLATE.item,
                Ref.METCARB_PANTS.item, Ref.METCARB_BOOTS.item, new ItemStack(
                        carbon, 1, 4), "Metamorphic Carbon",
                Enchantment.fireProtection);
        setUpFibrArmor();
        setUpAdvnArmor();
    }

    public void addEnchantedArmor(Item a, Item b, Item c, Item d,
            ItemStack material, String name, Enchantment enchant) {
        ItemStack[] armor = { new ItemStack(a), new ItemStack(b),
                new ItemStack(c), new ItemStack(d) };
        if (enchant != null) {
            for (int i = 0; i < armor.length; i++)
                armor[i].addEnchantment(enchant, this.specEnchant);
        }
        for (int i = 0; i < armor.length; i++)
            LanguageRegistry.addName(armor[i], name + " " + this.armornames[i]);

        GameRegistry.addRecipe(armor[0], "ccc", "c c", 'c', material);
        GameRegistry.addRecipe(armor[1], "c c", "ccc", "ccc", 'c', material);
        GameRegistry.addRecipe(armor[2], "ccc", "c c", "c c", 'c', material);
        GameRegistry.addRecipe(armor[3], "c c", "c c", 'c', material);
    }

    public void setUpFibrArmor() {
        ItemStack[] armor = { Ref.WOVEN_HELM.stack(), Ref.WOVEN_PLATE.stack(),
                Ref.WOVEN_PANTS.stack(), Ref.WOVEN_BOOTS.stack() };

        for (int i = 0; i < armor.length; i++)
            LanguageRegistry.addName(armor[i], "Carbon Fiber "
                    + this.armornames[i]);

        ItemStack c = new ItemStack(carbon, 1, 6), i = new ItemStack(itemStuff,
                1, ItemStuff.IRON_PLATE);
        GameRegistry.addRecipe(armor[0], "ccc", "cgc", 'c', c, 'g',
                new ItemStack(Block.thinGlass));
        GameRegistry.addRecipe(armor[1], "i i", "cic", "ccc", 'c', c, 'i', i);
        GameRegistry.addRecipe(armor[2], "ici", "c c", "c c", 'c', c, 'i', i);
        GameRegistry.addRecipe(armor[3], "c c", "c c", 'c', c);
    }

    public void setUpAdvnArmor() {
        ItemStack[] armor = { Ref.ADV_HELM.stack(), Ref.ADV_PLATE.stack(),
                Ref.ADV_PANTS.stack(), Ref.ADV_BOOTS.stack() };

        for (int i = 0; i < armor.length; i++)
            LanguageRegistry.addName(armor[i], "Carbon Alloy "
                    + this.armornames[i]);

        ItemStack c = new ItemStack(carbon, 1, 6), // woven carbon
        d = new ItemStack(itemStuff, 1, ItemStuff.ALLOY_PLATE);
        GameRegistry.addRecipe(armor[0], "dod", "cgc", 'd', d, 'o',
                Ref.WOVEN_HELM.stack(), 'c', c, 'g', new ItemStack(comGlass));
        GameRegistry.addRecipe(armor[1], "dod", "cic", "ccc", 'd', d, 'o',
                Ref.WOVEN_PLATE.stack(), 'c', c, 'i',
                ItemStuff.stack(ItemStuff.GOLD_ALLOYED));
        GameRegistry.addRecipe(armor[2], "dod", "c c", "c c", 'd', d, 'c', c,
                'o', Ref.WOVEN_PANTS.stack());
        GameRegistry.addRecipe(armor[3], "c c", "dod", 'd', d, 'c', c, 'o',
                Ref.WOVEN_BOOTS.stack());
    }

    @ForgeSubscribe
    public void onEntityLivingFallEvent(LivingFallEvent evt) {
        ItemCompactArmor.onEntityLivingFallEvent(evt);
    }

    @ForgeSubscribe
    public boolean onEntityLivingAttackEvent(LivingAttackEvent evt) {
        if (!(evt.entity instanceof EntityPlayer)) return false;
        EntityPlayer player = (EntityPlayer) evt.entity;
        ItemStack boots = player.inventory.armorItemInSlot(0);
        if (boots == null) return false;

        if (Ref.matches(boots, Ref.WOVEN_BOOTS, Ref.ADV_BOOTS)) {
            boolean b = evt.source.equals(DamageSource.fall);
            evt.setCanceled(b);
            boots.damageItem(1, player);
            return b;
        }
        return false;
    }
}
