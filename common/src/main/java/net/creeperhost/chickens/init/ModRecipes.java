package net.creeperhost.chickens.init;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.api.ChickenAPI;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModRecipes
{
    private static boolean initialized;

    public static void init()
    {
        if (initialized) return;
        initialized = true;
        registerTransformationRecipe(new ItemStack(Items.BOOK), ChickensRegistry.SMART_CHICKEN_ID);

        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.WHITE)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "white"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.ORANGE)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "orange"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.MAGENTA)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "magenta"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.LIGHT_BLUE)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "light_blue"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.YELLOW)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "yellow"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.LIME)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "lime"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.PINK)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "pink"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.GRAY)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "gray"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.LIGHT_GRAY)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "light_gray"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.CYAN)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "cyan"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.PURPLE)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "purple"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.BLUE)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "blue"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.BROWN)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "brown"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.GREEN)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "green"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.RED)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "red"));
        registerTransformationRecipe(new ItemStack(Items.DYE.pick(net.minecraft.world.item.DyeColor.BLACK)), Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "black"));
    }

    public static void registerTransformationRecipe(ItemStack stack, Identifier variant)
    {
        ChickenAPI.registerVariantRecipe(net.minecraft.world.entity.EntityTypes.CHICKEN, stack, variant);
    }
}
