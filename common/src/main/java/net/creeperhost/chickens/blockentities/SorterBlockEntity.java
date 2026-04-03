package net.creeperhost.chickens.blockentities;

import net.creeperhost.chickens.block.SorterBlock;
import net.creeperhost.chickens.blockentities.data.ListData;
import net.creeperhost.chickens.blockentities.data.TraitFilter;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.containers.SorterMenu;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.TryStateData;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.item.ItemChickenEgg;
import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.blocks.RedstoneActivatedBlock;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.helpers.ContainerUtil;
import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.items.ContainerAccessControl;
import net.creeperhost.polylib.inventory.items.PolyInventoryBlock;
import net.creeperhost.polylib.inventory.power.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
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

import java.util.List;

public class SorterBlockEntity extends PolyBlockEntity implements PolyInventoryBlock, MenuProvider, PolyEnergyBlock, RedstoneActivatedBlock {
    public final PolyEnergyStorage energy = new PolyBlockEnergyStorage(this, 128000);
    public final BlockInventory inventory = new BlockInventory(this, 4)
            .setMaxStackSize(1)
            .setSlotValidator(0, stack -> stack.getItem() instanceof ItemChickenEgg || stack.getItem() instanceof ItemChicken)
            .setSlotValidator(3, stack -> EnergyManager.isEnergyItem(stack) && EnergyManager.getHandler(stack).canExtract());

    public boolean scanning = false;
    public byte scanCount = 0;
    public final IntData progress = register("progress", new IntData(10), SAVE_BOTH);
    public final ListData<TraitFilter> traitFilter = register("traitFilter", new ListData<>(TraitFilter.CODEC, TraitFilter.STREAM_CODEC), SAVE_BOTH, CLIENT_CONTROL);
    public final ListData<ResourceLocation> variantFilter = register("variantFilter", new ListData<>(ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC.cast()), SAVE_BOTH, CLIENT_CONTROL);
    public final TryStateData fertilizedFilter = register("fertilizedFilter", new TryStateData(), SAVE_BOTH, CLIENT_CONTROL);
    public final TryStateData viableFilter = register("viableFilter", new TryStateData(), SAVE_BOTH, CLIENT_CONTROL);
    public final TryStateData eggFilter = register("eggFilter", new TryStateData(), SAVE_BOTH, CLIENT_CONTROL);


    public SorterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SORTER_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide()) return;

        //Update Energy
        if (Config.INSTANCE.enableEnergy) {
            IPolyEnergyStorageItem storage = EnergyManager.getHandler(inventory.getItem(3));
            if (storage != null && EnergyManager.transferEnergy(storage, energy) > 0) {
                inventory.setItem(3, storage.getContainer());
            }
        }

        //Handle Outputs
        ItemStack matchStack = inventory.getItem(1);
        if (!matchStack.isEmpty() && level.getBlockEntity(getBlockPos().relative(viableFace())) instanceof Container target) {
            matchStack.setCount(ContainerUtil.insertStack(matchStack, target));
        }
        ItemStack failStack = inventory.getItem(2);
        if (!failStack.isEmpty() && level.getBlockEntity(getBlockPos().relative(nonViableFace())) instanceof Container target) {
            failStack.setCount(ContainerUtil.insertStack(failStack, target));
        }

        boolean outputObstructed = !inventory.getItem(1).isEmpty() || !inventory.getItem(2).isEmpty();
        ItemStack input = inventory.getItem(0);
        if (input.isEmpty() || (!(input.getItem() instanceof ItemChickenEgg) && !(input.getItem() instanceof ItemChicken)) || outputObstructed) {
            progress.set(0);
            scanning = false;
            return;
        }

        if (progress.get() < Config.INSTANCE.sorterProcessTime) {
            if (isTileEnabled() && consumeEnergy()) {
                progress.inc();
                scanning = true;
            } else {
                scanning = false;
            }
            return;
        }

        if (isMatch(input)) {
            inventory.setItem(1, input);
        } else {
            inventory.setItem(2, input);
        }

        inventory.setItem(0, ItemStack.EMPTY);
        scanning = false;
        progress.set(0);
        scanCount++;
    }

    private boolean isMatch(ItemStack stack) {
        boolean isEgg = stack.getItem() instanceof ItemChickenEgg;
        if (fertilizedFilter.get() != TriState.DEFAULT) {
            if (!isEgg) {
                return false;
            }
            ItemChickenEgg egg = (ItemChickenEgg) stack.getItem();
            if (fertilizedFilter.get().toBoolean(false) != egg.isFertilized(stack)) {
                return false;
            }
        }
        if (viableFilter.get() != TriState.DEFAULT) {
            if (!isEgg) {
                return false;
            }
            ItemChickenEgg egg = (ItemChickenEgg) stack.getItem();
            if (viableFilter.get().toBoolean(false) != egg.isViable(stack)) {
                return false;
            }
        }
        if (eggFilter.get() != TriState.DEFAULT) {
            if (isEgg != eggFilter.get().toBoolean(false)) {
                return false;
            }
        }

        ChickenData data = ChickenData.fromItem(stack);
        if (data == null) {
            return false;
        }

        for (TraitFilter filter : traitFilter.get()) {
            double val = data.getTraitValue(filter.trait(), 0);
            if (filter.greater() && val < filter.value()) {
                return false;
            }
            if (!filter.greater() && val > filter.value()) {
                return false;
            }
        }

        List<ResourceLocation> variants = variantFilter.get();
        if (!variants.isEmpty()) {
            return variants.contains(data.variantId());
        }

        return true;
    }

    private Direction viableFace() {
        Direction facing = getBlockState().getValue(SorterBlock.FACING);
        return facing.getCounterClockWise(Direction.Axis.Y);
    }

    private Direction nonViableFace() {
        Direction facing = getBlockState().getValue(SorterBlock.FACING);
        return facing.getClockWise(Direction.Axis.Y);
    }

    private boolean consumeEnergy() {
        return !Config.INSTANCE.enableEnergy || energy.extractEnergy(Config.INSTANCE.sorterEnergyRate, false) == Config.INSTANCE.sorterEnergyRate;
    }

    @Override
    public Container getContainer(@Nullable Direction side) {
        ContainerAccessControl ac = new ContainerAccessControl(inventory, 0, Config.INSTANCE.enableEnergy ? 4 : 3)
                .slotRemoveCheck(0, stack -> false)
                .slotInsertCheck(1, stack -> false)
                .slotInsertCheck(2, stack -> false)
                .slotRemoveCheck(1, stack -> side == viableFace()) //via
                .slotRemoveCheck(2, stack -> side == nonViableFace()); //nonVia

        if (Config.INSTANCE.enableEnergy) {
            ac.slotRemoveCheck(3, stack -> {
                IPolyEnergyStorage energy = EnergyManager.getHandler(stack);
                return energy == null || !energy.canExtract() || energy.getEnergyStored() == 0;
            });
        }
        return ac;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SorterMenu(i, inventory, this);
    }

    @Override
    public IPolyEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return Config.INSTANCE.enableEnergy ? energy : null;
    }

    @Override
    public void writeExtraData(ValueOutput output) {
        inventory.serialize(output);
        energy.serialize(output);
    }

    @Override
    public void readExtraData(ValueInput input) {
        inventory.deserialize(input);
        energy.deserialize(input);
    }
}
