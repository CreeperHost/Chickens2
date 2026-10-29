package net.creeperhost.chickens;

import net.creeperhost.chickens.trait.Trait;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

public final class ChickensPlatform {
    public interface Provider {
        Path getConfigDirectory();
        int getBiomeTemperature(Holder<Biome> biome);
        int getBiomeHumidity(Holder<Biome> biome);
        EntityDataSerializer<Map<Trait, Double>> getTraitSerializer();
        EntityDataSerializer<Identifier> getResourceSerializer();
    }
    private static final Provider PROVIDER = ServiceLoader.load(Provider.class).findFirst().orElseThrow();
    public static Path getConfigDirectory() { return PROVIDER.getConfigDirectory(); }
    public static int getBiomeTemperature(Holder<Biome> biome) { return PROVIDER.getBiomeTemperature(biome); }
    public static int getBiomeHumidity(Holder<Biome> biome) { return PROVIDER.getBiomeHumidity(biome); }
    public static EntityDataSerializer<Map<Trait, Double>> getTraitSerializer() { return PROVIDER.getTraitSerializer(); }
    public static EntityDataSerializer<Identifier> getResourceSerializer() { return PROVIDER.getResourceSerializer(); }
}
