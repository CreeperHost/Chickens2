package net.creeperhost.chickens.item;

import net.creeperhost.chickens.api.ChickenStats;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.entity.EntityRooster;
import net.creeperhost.chickens.init.ModComponentTypes;
import net.creeperhost.chickens.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemChickenCatcher extends Item
{
    public ItemChickenCatcher(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemStack, @NotNull Player player, @NotNull LivingEntity livingEntity, @NotNull InteractionHand hand) {
        Level level = livingEntity.level();
        if (livingEntity instanceof ChickensChicken chicken) {
            ChickenData data = ChickenData.fromEntity(chicken);
            ItemStack chickenStack = data.toChickenItem();
            ItemEntity itemEntity = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), chickenStack);
            level.addFreshEntity(itemEntity);
            livingEntity.remove(Entity.RemovalReason.DISCARDED);
            return InteractionResult.PASS;
        }
        //TODO, how do we want to handle vanilla chickens?
//        else if (livingEntity instanceof Chicken chicken) {
//            ItemStack stack = new ItemStack(ModItems.CHICKEN_ITEM.get());
//            ItemChicken.applyEntityIdToItemStack(stack, ChickensRegistry.VANILLA_CHICKEN);
//
//            //Vanilla chickens don't have any stats so lets create set them to default
//            ChickenStats chickenStats = new ChickenStats(1,1, 1, 100);
//            chickenStats.write(stack);
//            stack.set(ModComponentTypes.LOVE.get(), chicken.getInLoveTime());
//            stack.set(ModComponentTypes.IS_BABY.get(), chicken.isBaby());
//
//            ItemEntity itemEntity = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), stack);
//            level.addFreshEntity(itemEntity);
//            livingEntity.remove(Entity.RemovalReason.DISCARDED);
//            return InteractionResult.PASS;
//        }
        return InteractionResult.PASS;
    }
}
