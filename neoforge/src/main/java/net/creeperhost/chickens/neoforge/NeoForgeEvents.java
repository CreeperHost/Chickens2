package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModEntities;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.minecraft.world.entity.animal.chicken.Chicken;

@EventBusSubscriber(modid = Chickens.MOD_ID)
public class NeoForgeEvents
{
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CHICKEN.get(), Chicken.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
//        ModEntities.CHICKENS.forEach((item, typeSupplier) -> {
//            if (item.isEnabled()) {
//                event.register(typeSupplier.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::checkChickenSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
//            }
//        });
        event.register(ModEntities.CHICKEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ChickensChicken::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    @SubscribeEvent
    public static void registerServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_variants"), ChickenDataManager.INSTANCE);
    }
}
