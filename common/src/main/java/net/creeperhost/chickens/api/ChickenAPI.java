package net.creeperhost.chickens.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChickenAPI
{
    public static final List<ChickenTransformationRecipe> TRANSFORMATION_RECIPES = new ArrayList<>();

    public static void registerVariantRecipe(EntityType<?> entityTypeIn, ItemStack itemStack, ResourceLocation variant)
    {
        ChickenTransformationRecipe recipe = new ChickenTransformationRecipe(entityTypeIn, itemStack, variant);
        if(!TRANSFORMATION_RECIPES.contains(recipe)) {
            TRANSFORMATION_RECIPES.add(recipe);
        }
    }
}
