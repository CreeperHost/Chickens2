package net.creeperhost.chickens.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class ChickenTransformationRecipe
{
    private final EntityType<?> entityTypeIn;
    private final ItemStack stack;
    private final Identifier variant;

    public ChickenTransformationRecipe(EntityType<?> entityTypeIn, ItemStack stack, Identifier variant)
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

    public Identifier getVariant() {
        return variant;
    }
}
