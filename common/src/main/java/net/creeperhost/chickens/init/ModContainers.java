package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import java.util.function.Supplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.containers.BreederMenu;
import net.creeperhost.chickens.containers.EggCrackerMenu;
import net.creeperhost.chickens.containers.IncubatorMenu;
import net.creeperhost.chickens.containers.SorterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;


public class ModContainers {
    public static final PolyRegistry<MenuType<?>> CONTAINERS = PolyRegistry.create(Registries.MENU, Chickens.MOD_ID);
    public static final Supplier<MenuType<BreederMenu>> BREEDER_CONTAINER = CONTAINERS.registerMenu("container_breeder", BreederMenu::new);
    public static final Supplier<MenuType<IncubatorMenu>> INCUBATOR = CONTAINERS.registerMenu("incubator", IncubatorMenu::new);
    public static final Supplier<MenuType<EggCrackerMenu>> EGG_CRACKER = CONTAINERS.registerMenu("egg_cracker", EggCrackerMenu::new);
    public static final Supplier<MenuType<SorterMenu>> SORTER = CONTAINERS.registerMenu("sorter", SorterMenu::new);
}
