package net.creeperhost.chickens.containers;

import net.creeperhost.chickens.blockentities.SorterBlockEntity;
import net.creeperhost.chickens.blockentities.data.ListData;
import net.creeperhost.chickens.blockentities.data.TraitFilter;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.data.TryStateData;
import net.creeperhost.chickens.init.ModContainers;
import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.client.modulargui.lib.container.SlotGroup;
import net.creeperhost.polylib.containers.PolyBlockContainerMenu;
import net.creeperhost.polylib.containers.slots.PolySlot;
import net.creeperhost.polylib.data.serializable.BooleanData;
import net.creeperhost.polylib.data.serializable.ByteData;
import net.creeperhost.polylib.data.serializable.IntData;
import net.creeperhost.polylib.data.serializable.LongData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class SorterMenu extends PolyBlockContainerMenu<SorterBlockEntity> {
    public final SlotGroup main = Config.INSTANCE.enableEnergy ? createSlotGroup(0, 1, 4) : createSlotGroup(0, 1);
    public final SlotGroup hotBar = Config.INSTANCE.enableEnergy ? createSlotGroup(0, 1, 4) : createSlotGroup(0, 1);

    public final SlotGroup input = createSlotGroup(1, 0);
    public final SlotGroup viable = createSlotGroup(2, 0);
    public final SlotGroup nonViable = createSlotGroup(3, 0);
    public final SlotGroup energySlot = createSlotGroup(4, 0);

    public final DataSync<Boolean> scanning;
    public final DataSync<Integer> progress;
    public final DataSync<Long> energy;
    public final DataSync<Byte> scanCount;
    public final DataSync<List<TraitFilter>> traitFilter;
    public final DataSync<List<ResourceLocation>> variantFilter;
    public final DataSync<TriState> fertilizedFilter;
    public final DataSync<TriState> viableFilter;
    public final DataSync<TriState> eggFilter;

    public SorterMenu(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(windowId, playerInv, getClientTile(playerInv, extraData));
    }

    public SorterMenu(int windowId, Inventory playerInv, SorterBlockEntity tile) {
        super(ModContainers.SORTER.get(), windowId, playerInv, tile);
        main.addPlayerMain(playerInv);
        hotBar.addPlayerBar(playerInv);

        input.addSlot(new PolySlot(tile.inventory, 0));
        viable.addSlot(new PolySlot(tile.inventory, 1).output());
        nonViable.addSlot(new PolySlot(tile.inventory, 2).output());

        scanning = new DataSync<>(this, new BooleanData(), () -> tile.scanning);
        progress = new DataSync<>(this, new IntData(), tile.progress::get);
        energy = new DataSync<>(this, new LongData(), tile.energy::getEnergyStored);
        scanCount = new DataSync<>(this, new ByteData(), () -> tile.scanCount);
        traitFilter = new DataSync<>(this, new ListData<>(TraitFilter.CODEC, TraitFilter.STREAM_CODEC), tile.traitFilter::get);
        variantFilter = new DataSync<>(this, new ListData<>(ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC.cast()), tile.variantFilter::get);
        fertilizedFilter = new DataSync<>(this, new TryStateData(), tile.fertilizedFilter::get);
        viableFilter = new DataSync<>(this, new TryStateData(), tile.viableFilter::get);
        eggFilter = new DataSync<>(this, new TryStateData(), tile.eggFilter::get);

        if (Config.INSTANCE.enableEnergy) {
            energySlot.addSlot(new PolySlot(tile.inventory, 3).setStackLimit(e -> 1));
        }
    }
}
