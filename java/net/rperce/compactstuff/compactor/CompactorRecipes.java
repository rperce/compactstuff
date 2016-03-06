package net.rperce.compactstuff.compactor;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.rperce.compactstuff.blockcompact.BlockCompact;
import net.rperce.compactstuff.blockcompact.BlockCompactSquishy;
import static net.rperce.compactstuff.blockcompact.BlockCompact.Meta.*;
import static net.rperce.compactstuff.blockcompact.BlockCompactSquishy.Meta.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by robert on 3/5/16.
 */
public class CompactorRecipes {
    private static Map<ItemStack, Set<IRecipe>> recipes    = new HashMap<>();
    private static Set<ItemStack> defaultEnabled = new HashSet<>();

    public static Set<ItemStack> getDefaultEnabled() { return defaultEnabled; }

    public static void setup() {
        // Enabled by default, 3x3
        RecipePair[] pairs = new RecipePair[] {
                new RecipePair(Blocks.diamond_block, Items.diamond),
                new RecipePair(BlockCompact.stack(COMCOBBLE), Blocks.cobblestone),
                new RecipePair(BlockCompact.stack(COMNETHER), Blocks.netherrack),
                new RecipePair(BlockCompactSquishy.stack(COMDIRT), Blocks.dirt),
                new RecipePair(BlockCompactSquishy.stack(COMGRAVEL), Blocks.gravel),
                new RecipePair(BlockCompactSquishy.stack(COMSAND), Blocks.sand),
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

    public static void addHomogeneousRecipe(RecipePair io, int amt) {
        addHomogeneousRecipe(io, amt, true);
    }

    public static void addHomogeneousRecipe(RecipePair io, int amt, boolean enable) {
        System.err.println("Adding recipe " + io.toString());
        Object[] o = new Object[amt];
        Arrays.fill(o, io.input);
        addShapelessRecipe(io.output, o);
        if (enable) {
            enableRecipe(defaultEnabled, io.output);
        }
    }

    public static void addShapelessRecipe(ItemStack output, Object... inputs) {
        System.err.printf("Adding shapeless recipe %s <- %s\n", output, Arrays.toString(inputs));
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

    public static void enableRecipe(Set<ItemStack> enabled, ItemStack output) {
        System.err.printf("Enabling %s in %s\n", output, enabled);
        for (ItemStack stack : enabled) {
            if (stack.isItemEqual(output)) return;
        }
        ItemStack add = output.copy();
        add.stackSize = 1;
        enabled.add(add);
    }

    public static boolean isEnabledIngredient(Set<ItemStack> enabled, ItemStack input) {
        return getEnabledRecipes(enabled)
                .flatMap(recipe -> getRequirements(recipe))
                .anyMatch(stack -> stack.isItemEqual(input));
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
                .filter(stack -> recipes.containsKey(stack))
                .flatMap(stack -> recipes.get(stack).stream());
    }

    public static void addRecipe(IRecipe recipe) {
        ItemStack out = recipe.getRecipeOutput();
        out.stackSize = 1;
        if (!recipes.containsKey(out))
            recipes.put(out, new HashSet<>());
        recipes.get(out).add(recipe);
    }
}
