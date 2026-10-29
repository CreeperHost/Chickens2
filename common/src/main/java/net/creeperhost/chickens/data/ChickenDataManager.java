package net.creeperhost.chickens.data;

import net.creeperhost.chickens.Chickens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by brandon3055 on 10/11/2025
 */
public class ChickenDataManager extends SimpleJsonResourceReloadListener<ChickenVariant> {

    public static final ChickenDataManager INSTANCE = new ChickenDataManager();
    private volatile Map<Identifier, ChickenVariant> variants = Map.of();
    private final List<SpawnData> spawns = new ArrayList<>();
    private final Set<TagKey<Biome>> biomes = new HashSet<>();

    public ChickenDataManager() {
        super(ChickenVariant.CODEC, FileToIdConverter.json("variants"));
    }

    public static Set<Identifier> getVariantIds() {
        return INSTANCE.variants.keySet();
    }

    public static Collection<ChickenVariant> getVariants() {
        return INSTANCE.variants.values();
    }

    /** Replace the client-side view with the server's current datapack contents. */
    public static void setClientVariants(Collection<ChickenVariant> variants) {
        Map<Identifier, ChickenVariant> updated = new HashMap<>();
        for (ChickenVariant variant : variants) {
            updated.put(variant.id(), variant);
        }
        INSTANCE.variants = Map.copyOf(updated);
    }

    @Nullable
    public static ChickenVariant getVariant(Identifier variantId) {
        return INSTANCE.variants.get(variantId);
    }

    public static ChickenVariant getVariantOrMissing(Identifier variantId) {
        return INSTANCE.variants.getOrDefault(variantId, ChickenVariant.MISSING);
    }

    @Nullable
    public static ChickenVariant getVariantForSpawn(ServerLevelAccessor level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        List<SpawnData> options = INSTANCE.spawns.stream().filter(spawnData -> spawnData.isMatch(biome)).toList();
        Optional<SpawnData> result = WeightedRandom.getRandomItem(level.getRandom(), options, value -> value.spawn.weight());
        return result.map(SpawnData::variant).orElse(null);
    }

    @Override
    protected void apply(Map<Identifier, ChickenVariant> variantMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, ChickenVariant> updated = new HashMap<>();
        spawns.clear();
        biomes.clear();
        variantMap.forEach((location, variant) -> {
            updated.put(variant.id(), variant);
            variant.spawn().ifPresent(e -> spawns.add(new SpawnData(variant, e)));
        });
        for (SpawnData data : spawns) {
            biomes.addAll(data.spawn().biomes());
        }
        variants = Map.copyOf(updated);

        Chickens.LOGGER.info("Loaded {} entity chicken variants", variants.size());
    }

    public static boolean isValidSpawnBiome(Holder<Biome> biome) {
        for (TagKey<Biome> biomeTag : INSTANCE.biomes) {
            if (biome.is(biomeTag)) {
                return true;
            }
        }
        return false;
    }

    private record SpawnData(ChickenVariant variant, ChickenSpawn spawn) {

        public boolean isMatch(Holder<Biome> biome) {
            for (TagKey<Biome> biomeTagKey : spawn.biomes()) {
                if (biome.is(biomeTagKey)) {
                    return true;
                }
            }
            return false;
        }

    }
}
