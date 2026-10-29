package net.creeperhost.chickens.fabric;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModEntities;
import net.creeperhost.chickens.trait.Trait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
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
    public static final EntityDataSerializer<Identifier> RESOURCE_SERIALIZER = EntityDataSerializer.forValueType(Identifier.STREAM_CODEC);

    @Override
    public void onInitialize() {
        Chickens.TRAIT_REGISTRY = FabricRegistryBuilder.create(Chickens.TRAIT_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();

        Chickens.init();
        Chickens.register();
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(ModEntities.CHICKEN.get(), net.minecraft.world.entity.animal.chicken.Chicken.createAttributes());
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            FabricClient.init();
        }

        FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "trait_serializer"), TRAIT_SERIALIZER);
        FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "resource_serializer"), RESOURCE_SERIALIZER);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_variants");
            }

            @Override
            public CompletableFuture<Void> reload(SharedState state, Executor executor, PreparationBarrier preparationBarrier, Executor executor2) {
                return ChickenDataManager.INSTANCE.reload(state, executor, preparationBarrier, executor2);
            }
        });

//        for (Config.FabricSpawn spawn : Config.INSTANCE.fabricSpawns) {
//            List<TagKey<Biome>> tags = spawn.biomeTags().stream().map(e -> TagKey.create(Registries.BIOME, Identifier.parse(e))).toList();
//            BiomeModifications.addSpawn(e -> tags.stream().anyMatch(e::hasTag), MobCategory.CREATURE, BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(spawn.type())), spawn.weight(), spawn.minCluster(), spawn.maxCluster());
//        }

        BiomeModifications.addSpawn(e -> e.hasTag(BiomeTags.IS_OVERWORLD), MobCategory.CREATURE, ModEntities.CHICKEN.get(), 10, 6, 6);
        BiomeModifications.addSpawn(e -> e.hasTag(BiomeTags.IS_NETHER), MobCategory.CREATURE, ModEntities.CHICKEN.get(), 60, 10, 10);

        SpawnPlacements.register(ModEntities.CHICKEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ChickensChicken::checkSpawnRules);

//            ModEntities.CHICKENS.forEach((chickensRegistryItem, entityTypeSupplier) -> ModEntities.registerSpawnFabric(entityTypeSupplier.get(), chickensRegistryItem));
//            SpawnPlacements.register(ModEntities.ROOSTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::checkChickenSpawnRules);
    }
}

