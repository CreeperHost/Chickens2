package net.creeperhost.chickens.data;

import net.creeperhost.chickens.Chickens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
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
    private final Map<String, ChickenVariant> variants = new HashMap<>();
    private final List<SpawnData> spawns = new ArrayList<>();

    public ChickenDataManager() {
        super(ChickenVariant.CODEC, FileToIdConverter.json("variants"));
    }

    public static Set<String> getVariantIds() {
        return INSTANCE.variants.keySet();
    }

    public static Collection<ChickenVariant> getVariants() {
        return INSTANCE.variants.values();
    }

    @Nullable
    public static ChickenVariant getVariant(String variantId) {
        return INSTANCE.variants.get(variantId);
    }

    public static ChickenVariant getVariantOrMissing(String variantId) {
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
    protected void apply(Map<ResourceLocation, ChickenVariant> variantMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        variants.clear();
        spawns.clear();
        variantMap.forEach((location, variant) -> {
            variants.put(variant.id(), variant);
            variant.spawn().ifPresent(e -> spawns.add(new SpawnData(variant, e)));
        });
        Chickens.LOGGER.info("Loaded {} entity chicken variants", variants.size());
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
