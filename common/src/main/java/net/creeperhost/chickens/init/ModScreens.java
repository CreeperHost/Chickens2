package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyScreens;
import net.creeperhost.chickens.client.gui.IncubatorGui;
import net.creeperhost.chickens.client.gui.BreederGui;
import net.creeperhost.chickens.client.gui.EggCrackerGui;
import net.creeperhost.chickens.client.gui.SorterGui;

public class ModScreens
{
    public static void init()
    {
        PolyScreens.register(ModContainers.BREEDER_CONTAINER, BreederGui.Screen::new);
        PolyScreens.register(ModContainers.INCUBATOR, IncubatorGui.Screen::new);
        PolyScreens.register(ModContainers.EGG_CRACKER, EggCrackerGui.Screen::new);
        PolyScreens.register(ModContainers.SORTER, SorterGui.Screen::new);
    }
}
