package net.creeperhost.chickens.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.init.ModEntities;
import net.creeperhost.chickens.trait.Trait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChickensModFabric implements ModInitializer {
    public static final EntityDataSerializer<Map<Trait, Double>> TRAIT_SERIALIZER = EntityDataSerializer.forValueType(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(Chickens.TRAIT_KEY), ByteBufCodecs.DOUBLE));

    @Override
    public void onInitialize() {
        Chickens.TRAIT_REGISTRY = FabricRegistryBuilder.createSimple(Chickens.TRAIT_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();

        Chickens.init();
        Chickens.register();
        if(Platform.getEnv() == EnvType.CLIENT) {
            FabricClient.init();
        }

        FabricTrackedDataRegistry.register(ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "trait_serializer"), TRAIT_SERIALIZER);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_variants");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
                return ChickenDataManager.INSTANCE.reload(preparationBarrier, resourceManager, executor, executor2);
            }
        });

//        for (Config.FabricSpawn spawn : Config.INSTANCE.fabricSpawns) {
//            List<TagKey<Biome>> tags = spawn.biomeTags().stream().map(e -> TagKey.create(Registries.BIOME, ResourceLocation.parse(e))).toList();
//            BiomeModifications.addSpawn(e -> tags.stream().anyMatch(e::hasTag), MobCategory.CREATURE, BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.parse(spawn.type())), spawn.weight(), spawn.minCluster(), spawn.maxCluster());
//        }

        BiomeModifications.addSpawn(e -> e.hasTag(BiomeTags.IS_OVERWORLD), MobCategory.CREATURE, ModEntities.CHICKEN.get(), 10, 6, 6);
        BiomeModifications.addSpawn(e -> e.hasTag(BiomeTags.IS_NETHER), MobCategory.CREATURE, ModEntities.CHICKEN.get(), 60, 10, 10);

        SpawnPlacements.register(ModEntities.CHICKEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::checkChickenSpawnRules);

//            ModEntities.CHICKENS.forEach((chickensRegistryItem, entityTypeSupplier) -> ModEntities.registerSpawnFabric(entityTypeSupplier.get(), chickensRegistryItem));
//            SpawnPlacements.register(ModEntities.ROOSTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::checkChickenSpawnRules);
    }
}

