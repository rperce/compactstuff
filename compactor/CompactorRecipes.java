package mods.CompactStuff.compactor;

import static mods.CompactStuff.CompactStuff.carbon;
import static mods.CompactStuff.CompactStuff.comBlock;
import static mods.CompactStuff.CompactStuff.comGlass;
import static mods.CompactStuff.CompactStuff.transmog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import mods.CompactStuff.BlockCompressed;
import mods.CompactStuff.CompactStuff;
import mods.CompactStuff.ItemCarbon;
import mods.CompactStuff.ItemStuff;
import mods.CompactStuff.Metas;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

public class CompactorRecipes {
	/** A list of all the recipes added */
    private static List<IRecipe> recipes = new ArrayList<IRecipe>();
    public static HashSet<ItemStack> defaultEnabled = new HashSet<ItemStack>();
    
    static {
    	System.out.println(BlockCompressed.stack(Metas.COMCOBBLE));
    	addHomogeneousRecipe(new ItemStack(CompactStuff.plantBall, 1, 0), 	new ItemStack(Block.sapling,1,0), 8);
    	addHomogeneousRecipe(new ItemStack(CompactStuff.plantBall, 1, 1), 	new ItemStack(Block.sapling,1,1), 8);
    	addHomogeneousRecipe(new ItemStack(CompactStuff.plantBall, 1, 2), 	new ItemStack(Block.sapling,1,2), 8);
    	addHomogeneousRecipe(new ItemStack(CompactStuff.plantBall, 1, 3), 	new ItemStack(Block.sapling,1,3), 8);
    	addHomogeneousRecipe(new ItemStack(CompactStuff.plantBall, 1, 4), 	new ItemStack(Item.seeds), 8);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMCOAL), 		 	new ItemStack(Item.coal), 8);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMCOBBLE), 		new ItemStack(Block.cobblestone), 9);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMDIRT),			new ItemStack(Block.dirt), 9);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMGRAVEL),		new ItemStack(Block.gravel), 9);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMRACK),			new ItemStack(Block.netherrack), 9);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMSAND),			new ItemStack(Block.sand), 9);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.STEELBLOCK),		ItemStuff.stack(ItemStuff.STEEL_INGOT), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockDiamond),				new ItemStack(Item.diamond), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockEmerald),				new ItemStack(Item.emerald), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockGold),				new ItemStack(Item.ingotGold), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockLapis),				new ItemStack(Item.dyePowder, 1, Metas.DYE_BLUE), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockNetherQuartz),		new ItemStack(Item.netherQuartz), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockRedstone),			new ItemStack(Item.redstone), 9);
    	addHomogeneousRecipe(new ItemStack(Block.blockIron),				new ItemStack(Item.ingotIron), 9);
    	addHomogeneousRecipe(new ItemStack(Block.netherBrick),				new ItemStack(Item.netherrackBrick), 4);
    	
    	/* The following recipes are not enabled by default
    	 * because they are not 1-to-1 undoable.  They must be
    	 * enabled in the Compactor's GUI.
    	 * 
    	 * Only addHomogeneousRecipe enables by default.
    	 */
    	addHomogeneousRecipe(new ItemStack(Block.blockClay),				new ItemStack(Item.clay), 4, false);
    	addHomogeneousRecipe(new ItemStack(Block.blockSnow),				new ItemStack(Item.snowball), 4, false);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMIRON),			new ItemStack(Block.blockIron), 9, false);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMDIAMOND),		new ItemStack(Block.blockDiamond), 9, false);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMSTEEL),			BlockCompressed.stack(Metas.STEELBLOCK), 9, false);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.COMGOLD),			new ItemStack(Block.blockGold), 9, false);
    	addHomogeneousRecipe(BlockCompressed.stack(Metas.DIORITE),			BlockCompressed.stack(Metas.COMCOBBLE), 9, false);
    	
    	addHomogeneousRecipe(ItemCarbon.stack(Metas.CB_DENSE), 				ItemCarbon.stack(Metas.CB_PURE), 8, false);
    	addRecipe(ItemCarbon.stack(Metas.CB_COMPRESS)," x ","xyx"," x ",'x',new ItemStack(comBlock,1,Metas.COMCOBBLE),'y',ItemCarbon.stack(Metas.CB_DENSE));
		addRecipe(ItemCarbon.stack(Metas.CB_META)," x ","xyx"," x ",'x',	new ItemStack(comBlock,1,Metas.COMRACK),'y',ItemCarbon.stack(Metas.CB_COMPRESS));
		
		addBothAlternatingRecipes(ItemCarbon.stack(Metas.CB_FIBER,2), ItemStuff.stack(ItemStuff.GLASS_FIBER), 
				ItemCarbon.stack(Metas.CB_PURE), new ItemStack(Item.slimeBall));
		addRecipe(ItemStuff.stack(ItemStuff.TMOG_CRYSTAL),"mhm","gzg","mnm",
				'm',new ItemStack(carbon,1,4),
				'g',new ItemStack(Item.netherQuartz),
				'z',ItemStuff.stack(ItemStuff.BLAZE_EMERALD),
				'n',new ItemStack(Item.enderPearl),
				'h',new ItemStack(Item.ghastTear));
		addBothAlternatingRecipes(ItemStuff.stack(ItemStuff.ALLOY_PLATE),
				ItemStuff.stack(ItemStuff.STEEL_PLATE), ItemStuff.stack(ItemStuff.DIAMOND_PLATE),
				new ItemStack(carbon,1,4));
		addBothAlternatingRecipes(ItemStuff.stack(ItemStuff.GOLD_ALLOYED),
				ItemStuff.stack(ItemStuff.GOLD_PLATE), new ItemStack(Item.lightStoneDust),
				ItemStuff.stack(ItemStuff.ALLOY_PLATE)); 
		addRecipe(new ItemStack(transmog,1,1), "ggg", "oao", "iri",
				'g', new ItemStack(comGlass),
				'o', new ItemStack(Block.obsidian),
				'a', ItemStuff.stack(ItemStuff.GOLD_ALLOYED),
				'i', new ItemStack(comBlock, 1, Metas.DIORITE),
				'r', new ItemStack(Block.blockRedstone));
		addRecipe(ItemCarbon.stack(Metas.CB_META), "xxx","xax","xxx",
				'x',new ItemStack(Block.netherBrick),
				'a',ItemCarbon.stack(Metas.CB_COMPRESS));
    }
    public static void addBothAlternatingRecipes(ItemStack a, ItemStack b, ItemStack c, ItemStack d) {
		addRecipe(a,"bcb","cdc","bcb",'b',b,'c',c,'d',d);
		addRecipe(a,"cbc","bdb","cbc",'b',b,'c',c,'d',d);
    }
    public static void addHomogeneousRecipe(ItemStack output, ItemStack in, int amt) {
    	addHomogeneousRecipe(output, in, amt, true);
    }
    
    public static void addHomogeneousRecipe(ItemStack output, ItemStack in, int amt, boolean enab) {
    	Object[] o = new Object[amt];
    	Arrays.fill(o, in);
    	addShapelessRecipe(output, o);
    	if(enab) enableRecipe(defaultEnabled, output);
    }

    public static ShapedRecipes addRecipe(ItemStack output, Object ... inputs) {
        String s = "";
        int i = 0, width = 0, height = 0;

        if (inputs[i] instanceof String[]) {
            String[] astring = (String[])((String[])inputs[i++]);

            for (int l = 0; l < astring.length; l++) {
                String s1 = astring[l];
                height++;
                width = s1.length();
                s += s1;
            }
        } else {
            while (inputs[i] instanceof String) {
                String s2 = (String)inputs[i++];
                height++;
                width = s2.length();
                s += s2;
            }
        }

        HashMap<Character, ItemStack> hashmap = new HashMap<Character, ItemStack>();
        for (; i < inputs.length; i += 2) {
            Character character = (Character)inputs[i];
            ItemStack item = null;

            if (inputs[i + 1] instanceof Item) 			item = new ItemStack((Item)inputs[i + 1]);
            else if (inputs[i + 1] instanceof Block) 	item = new ItemStack((Block)inputs[i + 1], 1, 32767);
            else if (inputs[i + 1] instanceof ItemStack)item = (ItemStack)inputs[i + 1];
            
            hashmap.put(character, item);
        }

        ItemStack[] recipe = new ItemStack[width * height];
        for (int l = 0; l < width * height; ++l) {
            char c = s.charAt(l);

            if (hashmap.containsKey(Character.valueOf(c)))
                recipe[l] = ((ItemStack)hashmap.get(Character.valueOf(c))).copy();
            else recipe[l] = null;
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(width, height, recipe, output);
        recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    public static void addShapelessRecipe(ItemStack output, Object ... inputs) {
        ArrayList list = new ArrayList();
        Object[] aobject = inputs;
        int len = inputs.length;

        for (int i = 0; i < len; i++) {
            Object o = aobject[i];
            if (o instanceof ItemStack) list.add(((ItemStack)o).copy());
            else if (o instanceof Item) list.add(new ItemStack((Item)o));
            else if(o instanceof Block) list.add(new ItemStack((Block)o));
            else throw new RuntimeException("Invalid shapeless recipie!");
        }

        recipes.add(new ShapelessRecipes(output, list));
    }

    public static ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack2 = inv.getStackInSlot(j);

            if (itemstack2 != null) {
                if (i == 0) itemstack = itemstack2;
                if (i == 1) itemstack1 = itemstack2;

                ++i;
            }
        }

        if (i == 2 && itemstack.itemID == itemstack1.itemID && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && Item.itemsList[itemstack.itemID].isRepairable()) {
            Item item = Item.itemsList[itemstack.itemID];
            int k = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int l = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int i1 = k + l + item.getMaxDamage() * 5 / 100;
            int j1 = item.getMaxDamage() - i1;

            if (j1 < 0) j1 = 0;

            return new ItemStack(itemstack.itemID, 1, j1);
        } else {
            for (j = 0; j < recipes.size(); ++j) {
                IRecipe recipe = (IRecipe)recipes.get(j);

                if (recipe.matches(inv, world)) return recipe.getCraftingResult(inv);
            }

            return null;
        }
    }
    public static List getRecipes() {
    	return recipes;
    }
    public synchronized static void enableRecipe(HashSet<ItemStack> enabled, ItemStack output) {
    	for(ItemStack stack : enabled)
    		if(areShallowEqual(stack,output)) return;
    	ItemStack add = output.copy(); add.stackSize = 1;
    	enabled.add(add);
    }
    
    public synchronized static void disableRecipe(HashSet<ItemStack> enabled, ItemStack output) {
    	for(Iterator<ItemStack> i=enabled.iterator(); i.hasNext();) {
    		ItemStack stack = i.next();
    		if(areShallowEqual(stack,output)) {
    			enabled.remove(stack);
    			return;
    		}
    	}
    }
    
    public static boolean isEnabled(HashSet<ItemStack> enabled, ItemStack output) {
    	for(ItemStack stack : enabled)
    		if(areShallowEqual(stack,output)) return true;
		return false;
    }
    
    public static boolean isEnabledIngredient(HashSet<ItemStack> enabled, ItemStack input) {
    	for(IRecipe recipe : recipes) {
    		if(enabled.contains(recipe.getRecipeOutput())) {
    			if(recipe instanceof ShapedRecipes) {
    				ShapedRecipes sr = (ShapedRecipes)recipe;
    				for(ItemStack stack: sr.recipeItems) if(areShallowEqual(stack,input)) return true;
    			} else {
    				ShapelessRecipes sr = (ShapelessRecipes)recipe;
    				for(ItemStack stack : (List<ItemStack>)sr.recipeItems) if(areShallowEqual(stack,input)) return true;
    			}
    		}
    	} return false;
    }
    public static HashSet<IRecipe> getEnabledRecipes(HashSet<ItemStack> hashSet) {
    	HashSet<IRecipe> out = new HashSet<IRecipe>();
    	for(IRecipe recipe : recipes) {
    		for(ItemStack result : hashSet) {
    			if(areShallowEqual(result,recipe.getRecipeOutput())) out.add(recipe);
    		}
    	}
    	return out;
    }
    public static List<IRecipe> getMatchingRecipes(HashSet<ItemStack> enabled, ItemStack input) {
    	List<IRecipe> goodOnes = new ArrayList<IRecipe>();
    	for(IRecipe recipe : getEnabledRecipes(enabled)) {
			if(recipe instanceof ShapedRecipes) {
				ShapedRecipes sr = (ShapedRecipes)recipe;
				for(ItemStack stack: sr.recipeItems) if(areShallowEqual(stack,input)) {
					goodOnes.add(recipe);
					break;
				}
			} else {
				ShapelessRecipes sr = (ShapelessRecipes)recipe;
				for(ItemStack stack : (List<ItemStack>)sr.recipeItems) if(areShallowEqual(stack,input)) {
					goodOnes.add(recipe);
					break;
				}
			}
    	} return goodOnes;
    }
    
    public static boolean areShallowEqual(ItemStack a, ItemStack b) {
    	if(a==null) return b==null;
    	if(b==null) return a==null;
    	return a.itemID==b.itemID && a.getItemDamage()==b.getItemDamage();
    }
    public static IRecipe getRecipeWithOutput(ItemStack out) {
    	for(IRecipe r : recipes) if(areShallowEqual(r.getRecipeOutput(),out)) return r;
    	return null;
    }
    public static List<ItemStack> getRequirements(IRecipe r) {
    	ArrayList<ItemStack> out = new ArrayList<ItemStack>();
    	ArrayList<ItemStack> flat = new ArrayList<ItemStack>();
    	if(r instanceof ShapedRecipes) flat.addAll(Arrays.asList(((ShapedRecipes)r).recipeItems));
    	else flat.addAll(((ShapelessRecipes)r).recipeItems);
    	for(ItemStack a : flat) {
    		if(a==null) continue;
    		boolean dupe = false;
    		for(ItemStack b : out) {
    			if(areShallowEqual(a,b)) {
    				dupe=true;
    				b.stackSize+=a.stackSize;
    			}
    		}
    		if(!dupe) out.add(a.copy());
    	}   		
    	return out;
    }
}
