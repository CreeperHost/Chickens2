package net.creeperhost.chickens.blockentities;

import net.creeperhost.chickens.block.BreederBlock;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.containers.BreederMenu;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.ChickenProduct;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.polylib.CommonTags;
import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.helpers.ContainerUtil;
import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.items.ContainerAccessControl;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class BreederBlockEntity extends PolyBlockEntity implements PolyInventoryBlock, MenuProvider {

    public final BlockInventory inventory = new BlockInventory(this, 6)
            .setSlotValidator(0, CommonTags::isSeeds)
            .setSlotValidator(1, e -> {
                ChickenData data = ChickenData.fromItem(e);
                return data != null && data.isRooster();
            })
            .setSlotValidator(2, e -> {
                ChickenData data = ChickenData.fromItem(e);
                return data != null && !data.isRooster();
            });

    public final IntData progress = register("progress", new IntData(0), SAVE_BOTH);
    public final IntData targetProgress = register("cycle_target", new IntData(0), SAVE_BOTH);

    public BreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BREEDER_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level instanceof ServerLevel serverLevel)) return;

        ItemStack seeds = inventory.getItem(0);
        ChickenData rooster = ChickenData.fromItem(inventory.getItem(1));
        ChickenData chicken = ChickenData.fromItem(inventory.getItem(2));

        boolean canWork = chicken != null && CommonTags.isSeeds(seeds);
        setState(canWork);
        if (!canWork) {
            progress.set(0);
            targetProgress.set(-1);
            return;
        }

        ChickenProduct product = chicken.variant().product();
        if (targetProgress.get() < 0) {
            targetProgress.set(product.minLayTime() + level.random.nextInt(Math.max(product.maxLayTime() - product.minLayTime(), 1)));
            chicken.traits().forEach(state -> targetProgress.set((int) (targetProgress.get() * state.trait().getLaySpeedModifier(state.value()))));
        }

        if (progress.get() < targetProgress.get()) {
            progress.inc();
            return;
        }


        ItemStack resultEgg;
        if (rooster != null) {
            ChickenData child = ChickenData.fromParents(chicken, rooster, level.random);
            resultEgg = child.toChickenEgg(true);
        } else {
            resultEgg = chicken.toChickenEgg(false);
        }

//        ChickenStats chickenStats = new ChickenStats(rooster);
//        int count = Math.max(1, ((1 + chickenStats.getGain()) / 3));
//
//        chickenStack.setCount(count);

        if (ContainerUtil.insertStack(resultEgg, inventory, true) == 0) {
            ContainerUtil.insertStack(resultEgg, inventory);
            damageChicken(1);
            damageChicken(2);
            level.playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 0.5F, 0.8F);
            serverLevel.sendParticles(ParticleTypes.HEART, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1, getBlockPos().getZ() + 0.5, 8, 0.45, 0.45, 0.45, 0.0125);
            if (level.random.nextDouble() < Config.INSTANCE.breederFoodConsumptionChance) {
                seeds.shrink(1);
            }
            progress.set(0);
            targetProgress.set(-1);
        }
    }

    @Override
    public Container getContainer(@Nullable Direction side) {
        if (side != Direction.DOWN) {
            //Allows extraction from any slot from sides or top
            return new ContainerAccessControl(inventory, 0, 6)
                    .slotInsertCheck(1, stack -> stack.getCount() == 1 && inventory.getItem(1).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
                    .slotInsertCheck(2, stack -> stack.getCount() == 1 && inventory.getItem(2).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
//                    .slotInsertCheck(3, stack -> stack.getCount() == 1 && inventory.getItem(3).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
                    .containerInsertCheck((slot, stack) -> slot <= 2);
        }
        //Only allow extraction of outputs from bottom (basic hopper compatibility)
        return new ContainerAccessControl(inventory, 0, 6)
                .slotInsertCheck(1, stack -> stack.getCount() == 1 && inventory.getItem(1).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
                .slotInsertCheck(2, stack -> stack.getCount() == 1 && inventory.getItem(2).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
//                .slotInsertCheck(3, stack -> stack.getCount() == 1 && inventory.getItem(3).isEmpty()) //TODO This limiting slot to 1 item can be done better with a custom SidedInvWrapper, though not sure about fabric...
                .containerInsertCheck((slot, stack) -> slot <= 2)
                .containerRemoveCheck((slot, stack) -> slot > 2);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BreederMenu(i, inventory, this);
    }

    @Override
    public void writeExtraData(ValueOutput output) {
        super.writeExtraData(output);
        inventory.serialize(output);
    }

    @Override
    public void readExtraData(ValueInput input) {
        super.readExtraData(input);
        inventory.deserialize(input);
    }

    public void setState(boolean canWork) {
        boolean hasSeeds = !inventory.getItem(0).isEmpty();
        level.setBlock(getBlockPos(), getBlockState().setValue(BreederBlock.HAS_SEEDS, hasSeeds).setValue(BreederBlock.IS_BREEDING, canWork), 3);
    }

    public void damageChicken(int slot) {
        ChickenData data = ChickenData.fromItem(inventory.getItem(slot));
        if (data == null) {
            return;
        }

        double mod = data.getTraitValue(ChickenTraits.LIFESPAN.get(), 1);
        data = data.modifyLifespan((float) -(Config.INSTANCE.lifespanReductionOnLay / mod));
        inventory.setItem(slot, data.toChickenItem());
    }
}
