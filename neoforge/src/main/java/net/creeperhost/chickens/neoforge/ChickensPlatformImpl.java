package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.ChickensPlatform;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.Tags;

import java.nio.file.Path;
import java.util.Map;

public class ChickensPlatformImpl implements ChickensPlatform.Provider
{
    /**
     * This is our actual method to {@link ChickensPlatform#getConfigDirectory()}.
     */
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

//    public static Item createNewChickenItem(Item.Properties properties)
//    {
//        return new ItemChickenNeoForge(properties);
//    }

    public int getBiomeTemperature(Holder<Biome> biome) {
        if (biome.is(Tags.Biomes.IS_SNOWY)) {
            return  -10;
        } else if (biome.is(Tags.Biomes.IS_COLD)) {
            return  10;
        }else if (biome.is(Tags.Biomes.IS_HOT)) {
            return  35;
        }
        return 20;
    }

    public int getBiomeHumidity(Holder<Biome> biome) {
        if (biome.is(Tags.Biomes.IS_WET)) {
            return 50;
        }else if (biome.is(Tags.Biomes.IS_DRY)) {
            return  10;
        }
        return 30;
    }


    public EntityDataSerializer<Map<Trait, Double>> getTraitSerializer() {
        return ChickensModNeoForge.TRAIT_SERIALIZER.get();
    }

    public EntityDataSerializer<Identifier> getResourceSerializer() {
        return ChickensModNeoForge.RESOURCE_SERIALIZER.get();
    }
}
