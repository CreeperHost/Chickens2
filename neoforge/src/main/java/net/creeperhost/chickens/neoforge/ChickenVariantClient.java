package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.mixin.CreativeModeTabsAccessor;
import java.util.List;

public final class ChickenVariantClient {
    public static void receive(List<ChickenVariant> variants) {
        ChickenDataManager.setClientVariants(variants);
        CreativeModeTabsAccessor.chickens$setParameters(null);
        if (net.neoforged.fml.ModList.get().isLoaded("jei")) {
            net.creeperhost.chickens.neoforge.jei.ChickensJeiPlugin.refresh();
        }
    }
}
