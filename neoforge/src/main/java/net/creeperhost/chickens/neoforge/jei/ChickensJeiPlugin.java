package net.creeperhost.chickens.neoforge.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import java.util.*;

@JeiPlugin
public final class ChickensJeiPlugin implements IModPlugin {
    private static final IRecipeType<BreedingRecipe> BREEDING = IRecipeType.create(Chickens.MOD_ID, "breeding", BreedingRecipe.class);
    private static IJeiRuntime runtime;
    private static List<ItemStack> displayedItems = List.of();
    private static List<BreedingRecipe> displayedRecipes = List.of();

    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "jei"); }
    @Override public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.CHICKEN_ITEM.get(), (stack, context) -> {
            ChickenData data = ChickenData.fromItem(stack);
            return data == null ? null : data.variantId() + ":" + data.isRooster();
        });
        registration.registerSubtypeInterpreter(ModItems.CHICKEN_EGG.get(), (stack, context) -> {
            ChickenData data = ChickenData.fromItem(stack);
            return data == null ? null : data.variantId().toString();
        });
    }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new BreedingCategory(registration.getJeiHelpers().getGuiHelper().createDrawableItemStack(new ItemStack(ModItems.BREEDER.get()))));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(BREEDING, ModItems.BREEDER.get());
    }
    @Override public void onRuntimeAvailable(IJeiRuntime value) { runtime = value; refresh(); }
    @Override public void onRuntimeUnavailable() { runtime = null; displayedItems = List.of(); displayedRecipes = List.of(); }

    public static void refresh() {
        if (runtime == null) return;
        if (!displayedItems.isEmpty()) runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, displayedItems);
        if (!displayedRecipes.isEmpty()) runtime.getRecipeManager().hideRecipes(BREEDING, displayedRecipes);
        List<ChickenVariant> variants = ChickenDataManager.getVariants().stream().sorted(Comparator.comparing(v -> v.id().toString())).toList();
        List<ItemStack> items = new ArrayList<>();
        Set<BreedingRecipe> recipeSet = new LinkedHashSet<>();
        for (ChickenVariant variant : variants) {
            items.add(chicken(variant.id(), false));
            items.add(chicken(variant.id(), true));
            items.add(egg(variant.id()));
            recipeSet.add(new BreedingRecipe(variant.id(), variant.id(), variant.id()));
            if (variant.parent1().isPresent() && variant.parent2().isPresent()) {
                Identifier first = variant.parent1().get(), second = variant.parent2().get();
                for (Identifier child : List.of(variant.id(), first, second)) {
                    recipeSet.add(new BreedingRecipe(first, second, child));
                    recipeSet.add(new BreedingRecipe(second, first, child));
                }
            }
        }
        List<BreedingRecipe> recipes = List.copyOf(recipeSet);
        displayedItems = List.copyOf(items);
        displayedRecipes = List.copyOf(recipes);
        if (!items.isEmpty()) runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, items);
        if (!recipes.isEmpty()) {
            runtime.getRecipeManager().addRecipes(BREEDING, recipes);
            runtime.getRecipeManager().unhideRecipes(BREEDING, recipes);
        }
        Chickens.LOGGER.info("Updated JEI with {} chicken ingredients and {} breeding recipes", items.size(), recipes.size());
    }
    private static ItemStack chicken(Identifier id, boolean rooster) {
        return new ChickenData(id, rooster, List.of(), ChickenData.EntityData.create()).toChickenItem();
    }
    private static ItemStack egg(Identifier id) {
        return new ChickenData(id, false, List.of(), ChickenData.EntityData.create()).toChickenEgg(true);
    }
    public record BreedingRecipe(Identifier first, Identifier second, Identifier child) { }
    private record BreedingCategory(IDrawable icon) implements IRecipeCategory<BreedingRecipe> {
        @Override public IRecipeType<BreedingRecipe> getRecipeType() { return BREEDING; }
        @Override public Component getTitle() { return Component.translatable("gui.breeding"); }
        @Override public int getWidth() { return 90; }
        @Override public int getHeight() { return 50; }
        @Override public IDrawable getIcon() { return icon; }
        @Override public void setRecipe(IRecipeLayoutBuilder builder, BreedingRecipe recipe, IFocusGroup focuses) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 16).add(chicken(recipe.first(), false));
            builder.addSlot(RecipeIngredientRole.INPUT, 35, 16).add(chicken(recipe.second(), true));
            builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 16).add(egg(recipe.child()));
        }
    }
}
