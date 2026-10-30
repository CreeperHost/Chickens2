package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorage;
import net.creeperhost.polylib.neoforge.inventory.fluid.PolyNeoFluidWrapper;
import net.creeperhost.polylib.neoforge.inventory.power.PolyNeoEnergyWrapper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = Chickens.MOD_ID)
public final class ResourceCapabilities {
    private static final WeakHashMap<IPolyEnergyStorage, WeakReference<PolyNeoEnergyWrapper>> ENERGY = new WeakHashMap<>();
    private static final WeakHashMap<IPolyFluidStorage, WeakReference<PolyNeoFluidWrapper>> FLUID = new WeakHashMap<>();

    private ResourceCapabilities() {}

    // PolyLib's wrappers journal transactions per wrapper, so all faces of one storage must share a wrapper.
    private static synchronized PolyNeoEnergyWrapper energy(IPolyEnergyStorage storage) {
        if (storage == null) return null;
        WeakReference<PolyNeoEnergyWrapper> reference = ENERGY.get(storage);
        PolyNeoEnergyWrapper wrapper = reference == null ? null : reference.get();
        if (wrapper == null) {
            wrapper = new PolyNeoEnergyWrapper(storage);
            ENERGY.put(storage, new WeakReference<>(wrapper));
        }
        return wrapper;
    }

    private static synchronized PolyNeoFluidWrapper fluid(IPolyFluidStorage storage) {
        if (storage == null) return null;
        WeakReference<PolyNeoFluidWrapper> reference = FLUID.get(storage);
        PolyNeoFluidWrapper wrapper = reference == null ? null : reference.get();
        if (wrapper == null) {
            wrapper = new PolyNeoFluidWrapper(storage);
            FLUID.put(storage, new WeakReference<>(wrapper));
        }
        return wrapper;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlocks.INCUBATOR_TILE.get(),
                (block, side) -> Config.INSTANCE.enableEnergy ? energy(block.getEnergyStorage(side)) : null);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlocks.EGG_CRACKER_TILE.get(),
                (block, side) -> Config.INSTANCE.enableEnergy ? energy(block.getEnergyStorage(side)) : null);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, ModBlocks.SORTER_TILE.get(),
                (block, side) -> Config.INSTANCE.enableEnergy ? energy(block.getEnergyStorage(side)) : null);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlocks.INCUBATOR_TILE.get(),
                (block, side) -> fluid(block.getFluidStorage(side)));
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlocks.EGG_CRACKER_TILE.get(),
                (block, side) -> fluid(block.getFluidStorage(side)));
    }
}
