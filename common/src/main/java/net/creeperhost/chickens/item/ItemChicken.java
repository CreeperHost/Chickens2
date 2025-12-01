package net.creeperhost.chickens.item;

import net.creeperhost.chickens.api.ChickensRegistryItem;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModComponentTypes;
import net.creeperhost.chickens.init.ModEntities;
import net.creeperhost.chickens.init.ModItems;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemChicken extends Item {
    public ItemChicken(Properties properties) {
        super(properties);
    }

    @Deprecated
    public static ItemStack of(ChickensRegistryItem chickensRegistryItem) {
        ItemStack stack = new ItemStack(ModItems.CHICKEN_ITEM.get());
        applyEntityIdToItemStack(stack, chickensRegistryItem.getRegistryName());
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if (!level.isClientSide) {
            InteractionHand hand = useOnContext.getHand();
            ItemStack stack = useOnContext.getPlayer().getItemInHand(hand);
            BlockPos blockPos = correctPosition(useOnContext.getClickedPos(), useOnContext.getClickedFace());
            spawn(stack, level, blockPos);
            if (!useOnContext.getPlayer().isCreative()) {
                stack.shrink(1);
            }
        }
        return InteractionResult.PASS;
    }

    public static BlockPos correctPosition(BlockPos pos, Direction side) {
        final int[] offsetsXForSide = new int[]{0, 0, 0, 0, -1, 1};
        final int[] offsetsYForSide = new int[]{-1, 1, 0, 0, 0, 0};
        final int[] offsetsZForSide = new int[]{0, 0, -1, 1, 0, 0};

        int posX = pos.getX() + offsetsXForSide[side.ordinal()];
        int posY = pos.getY() + offsetsYForSide[side.ordinal()];
        int posZ = pos.getZ() + offsetsZForSide[side.ordinal()];

        return new BlockPos(posX, posY, posZ);
    }

    public static void spawn(ItemStack stack, Level level, BlockPos pos) {
        ChickenData data = ChickenData.fromItem(stack);
        ChickensChicken chicken = ModEntities.CHICKEN.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (chicken == null || data == null) {
            return;
        }
        data.apply(chicken);
        chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(chicken);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        ChickenData data = ChickenData.fromItem(itemStack);
        if (Screen.hasShiftDown()) {
            for (Trait.State state : data.traits()) {
                Trait trait = Trait.fromId(state.id());
                if (trait == null) continue;
                trait.appendHoverText(consumer, state.value());
            }
        } else {
            consumer.accept(Component.translatable("screen.shift.tooltip"));
        }
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        ChickenData data = ChickenData.fromItem(stack);
        if (data == null) {
            return super.getName(stack);
        }
        return data.isRooster() ? Component.translatable("item.chickens.rooster.name", data.variant().name()) : Component.translatable("item.chickens.chicken.name", data.variant().name());
    }

    @Deprecated
    public static void applyEntityIdToItemStack(ItemStack stack, ResourceLocation entityId) {
        stack.set(ModComponentTypes.CHICKEN_TYPE.get(), entityId.toString());
    }

    @Deprecated
    @Nullable
    public static String getTypeFromStack(ItemStack stack) {
        return stack.get(ModComponentTypes.CHICKEN_TYPE.get());
    }
}
