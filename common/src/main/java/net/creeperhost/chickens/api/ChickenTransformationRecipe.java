package net.creeperhost.chickens.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class ChickenTransformationRecipe
{
    private final EntityType<?> entityTypeIn;
    private final ItemStack stack;
    private final ResourceLocation variant;

    public ChickenTransformationRecipe(EntityType<?> entityTypeIn, ItemStack stack, ResourceLocation variant)
    {
        this.entityTypeIn = entityTypeIn;
        this.stack = stack;
        this.variant = variant;
    }

    public EntityType<?> getEntityTypeIn()
    {
        return entityTypeIn;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public ResourceLocation getVariant() {
        return variant;
    }
}
