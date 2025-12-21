package net.creeperhost.chickens.init;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.api.ChickenAPI;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModRecipes
{
    public static void init()
    {
        registerTransformationRecipe(new ItemStack(Items.BOOK), ChickensRegistry.SMART_CHICKEN_ID);

        registerTransformationRecipe(new ItemStack(Items.WHITE_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "white"));
        registerTransformationRecipe(new ItemStack(Items.ORANGE_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "orange"));
        registerTransformationRecipe(new ItemStack(Items.MAGENTA_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "magenta"));
        registerTransformationRecipe(new ItemStack(Items.LIGHT_BLUE_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "light_blue"));
        registerTransformationRecipe(new ItemStack(Items.YELLOW_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "yellow"));
        registerTransformationRecipe(new ItemStack(Items.LIME_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "lime"));
        registerTransformationRecipe(new ItemStack(Items.PINK_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "pink"));
        registerTransformationRecipe(new ItemStack(Items.GRAY_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "gray"));
        registerTransformationRecipe(new ItemStack(Items.LIGHT_GRAY_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "light_gray"));
        registerTransformationRecipe(new ItemStack(Items.CYAN_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "cyan"));
        registerTransformationRecipe(new ItemStack(Items.PURPLE_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "purple"));
        registerTransformationRecipe(new ItemStack(Items.BLUE_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "blue"));
        registerTransformationRecipe(new ItemStack(Items.BROWN_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "brown"));
        registerTransformationRecipe(new ItemStack(Items.GREEN_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "green"));
        registerTransformationRecipe(new ItemStack(Items.RED_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "red"));
        registerTransformationRecipe(new ItemStack(Items.BLACK_DYE), ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "black"));
    }

    public static void registerTransformationRecipe(ItemStack stack, ResourceLocation variant)
    {
        ChickenAPI.registerVariantRecipe(EntityType.CHICKEN, stack, variant);
    }
}
