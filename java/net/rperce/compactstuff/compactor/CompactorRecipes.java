package net.rperce.compactstuff.compactor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.rperce.compactstuff.ItemStackSet;
import net.rperce.compactstuff.blockcompact.BlockCompact;
import net.rperce.compactstuff.blockcompact.BlockCompactSquishy;

import java.util.*;
import java.util.stream.Stream;

import static net.rperce.compactstuff.blockcompact.BlockCompact.Meta.*;
import static net.rperce.compactstuff.blockcompact.BlockCompactSquishy.Meta.*;

class CompactorRecipes {
    private static final Set<IRecipe> recipes = new HashSet<>();
    private static final ItemStackSet defaultEnabled = new ItemStackSet();

    public static ItemStackSet getDefaultEnabled() { return defaultEnabled; }

    public static void setup() {
        // Enabled by default, 3x3
        RecipePair[] pairs = new RecipePair[] {
                new RecipePair(BlockCompact.stack(COMCOBBLE), Blocks.cobblestone),
                new RecipePair(BlockCompact.stack(COMNETHER), Blocks.netherrack),
                new RecipePair(BlockCompactSquishy.stack(COMDIRT), Blocks.dirt),
                new RecipePair(BlockCompactSquishy.stack(COMSAND), Blocks.sand),
                new RecipePair(BlockCompactSquishy.stack(COMGRAVEL), Blocks.gravel),
                new RecipePair(Blocks.diamond_block, Items.diamond),
                new RecipePair(Blocks.emerald_block, Items.emerald),
                new RecipePair(Blocks.iron_block,    Items.iron_ingot),
                new RecipePair(Blocks.gold_block,    Items.gold_ingot),
                new RecipePair(Items.gold_ingot,     Items.gold_nugget),
                new RecipePair(Blocks.lapis_block,   new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage())),
                new RecipePair(Blocks.redstone_block, Items.redstone),
                new RecipePair(Blocks.slime_block,   Items.slime_ball),
        };
        for (RecipePair pair : pairs) {
            addHomogeneousRecipe(pair, 9);
        }
        addHomogeneousRecipe(new RecipePair(Blocks.nether_brick, Items.netherbrick), 4);
        addHomogeneousRecipe(new RecipePair(Blocks.clay, Items.clay_ball), 4);
        addHomogeneousRecipe(new RecipePair(Blocks.snow, Items.snowball), 4);

        // Not enabled by default
        pairs = new RecipePair[] {
                new RecipePair(BlockCompact.stack(COMDIAMOND), Blocks.diamond_block),
                new RecipePair(BlockCompact.stack(COMIRON), Blocks.iron_block),
                new RecipePair(BlockCompact.stack(COMGOLD), Blocks.gold_block),
                new RecipePair(BlockCompact.stack(COMSTEEL), BlockCompact.stack(STEELBLOCK)),
        };
        for (RecipePair pair : pairs) {
            addHomogeneousRecipe(pair, 9, false);
        }
    }

    private static void addHomogeneousRecipe(RecipePair io, int amt) {
        addHomogeneousRecipe(io, amt, true);
    }

    private static void addHomogeneousRecipe(RecipePair io, int amt, boolean enable) {
        Object[] o = new Object[amt];
        Arrays.fill(o, io.input);
        addShapelessRecipe(io.output, o);
        if (enable) {
            enableRecipe(defaultEnabled, io.output);
        }
    }

    private static void addShapelessRecipe(ItemStack output, Object... inputs) {
        ArrayList<ItemStack> list = new ArrayList<>();
        for(Object o : inputs) {
            if (o instanceof ItemStack) {
                list.add(((ItemStack)o).copy());
            } else if (o instanceof Item) {
                list.add(new ItemStack((Item)o));
            } else if (o instanceof Block) {
                list.add(new ItemStack((Block)o));
            } else {
                throw new RuntimeException("Invalid input object " + o.toString() + "for a shapeless recipe. Must be ItemStack, Item, or Block.");
            }
        }
        addRecipe(new ShapelessRecipes(output, list));
    }

    private static void enableRecipe(Set<ItemStack> enabled, ItemStack output) {
        for (ItemStack stack : enabled) {
            if (stack.isItemEqual(output)) return;
        }
        ItemStack add = output.copy();
        add.stackSize = 1;
        enabled.add(add);
    }

    public static boolean isEnabledIngredient(Set<ItemStack> enabled, ItemStack input) {
        return getEnabledRecipes(enabled)
                .flatMap(CompactorRecipes::getRequirements)
                .anyMatch(stack -> stack.isItemEqual(input));
    }

    public static boolean isEnabled(Set<ItemStack> enabled, ItemStack output) {
        return getEnabledRecipes(enabled)
                .map(IRecipe::getRecipeOutput)
                .anyMatch(stack -> stack.isItemEqual(output));
    }

    public static Stream<ItemStack> getRequirements(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes) {
            return Arrays.stream(((ShapedRecipes) recipe).recipeItems);
        } else if (recipe instanceof ShapelessRecipes) {
            return ((ShapelessRecipes) recipe).recipeItems.stream();
        }
        return Stream.empty();
    }

    public static Stream<IRecipe> getEnabledRecipes(Set<ItemStack> enabled) {
        return enabled.stream()
                .filter(CompactorRecipes::containsRecipe)
                .flatMap(CompactorRecipes::getRecipesFor);
    }

    private static void addRecipe(IRecipe recipe) {
        ItemStack out = recipe.getRecipeOutput();
        out.stackSize = 1;
        recipes.add(recipe);
    }

    public static boolean containsRecipe(ItemStack stack) {
        return recipes.stream().anyMatch(s -> s.getRecipeOutput().isItemEqual(stack));
    }
    public static Stream<IRecipe> getRecipesFor(ItemStack stack) {
        return recipes.stream().filter(s -> s.getRecipeOutput().isItemEqual(stack));
    }

    public static Optional<ItemStack> findMatchingRecipe(InventoryCrafting inv, World world) {
        return recipes.stream()
                .filter(recipe -> recipe.matches(inv, world))
                .map(recipe -> recipe.getCraftingResult(inv))
                .findFirst();
    }
}
