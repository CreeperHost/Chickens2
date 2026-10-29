package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import java.util.function.Supplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.creeperhost.chickens.api.ChickensRegistryItem;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.entity.EntityRooster;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModEntities {
    public static final PolyRegistry<EntityType<?>> ENTITIES = PolyRegistry.create(Registries.ENTITY_TYPE, Chickens.MOD_ID);

//    public static final Supplier<EntityType<EntityRooster>> ROOSTER = ENTITIES.register("rooster", () -> EntityType.Builder.of(EntityRooster::new, MobCategory.CREATURE)
//            .sized(0.6F, 1.7F)
//            .clientTrackingRange(8)
//            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "rooster"))));

    public static final Supplier<EntityType<ChickensChicken>> CHICKEN = ENTITIES.register("chicken", () -> EntityType.Builder.of(ChickensChicken::new, MobCategory.CREATURE)
            .sized(0.4F, 0.7F)
            .eyeHeight(0.644F)
            .passengerAttachments(new Vec3(0.0, 0.7, -0.1))
            .clientTrackingRange(10)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken"))));

//    public static final Map<ChickensRegistryItem, Supplier<EntityType<EntityChickensChicken>>> CHICKENS = Util.make(new LinkedHashMap<>(), map ->
//    {
//        for (ChickensRegistryItem item : ChickensRegistry.getItems())
//        {
//            if(!item.equals(ModChickens.ROOSTER))
//            {
//                map.put(item, ENTITIES.register(item.getEntityName(), () -> EntityType.Builder.of(EntityChickensChicken::new, MobCategory.CREATURE)
//                        .sized(0.6F, 1.7F)
//                        .clientTrackingRange(8)
//                        .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Chickens.MOD_ID, item.getEntityName())))
//                ));
//            }
//        }
//    });
}
