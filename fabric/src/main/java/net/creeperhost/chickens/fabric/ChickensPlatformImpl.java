package net.creeperhost.chickens.fabric;

import net.creeperhost.chickens.ChickensPlatform;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.trait.Trait;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Map;

public class ChickensPlatformImpl implements ChickensPlatform.Provider
{
    /**
     * This is our actual method to {@link ChickensPlatform#getConfigDirectory()}.
     */
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

//    public static Item createNewChickenItem(Item.Properties properties)
//    {
//        return new ItemChicken(properties);
//    }

    public int getBiomeTemperature(Holder<Biome> biome) {
        if (biome.is(ConventionalBiomeTags.IS_SNOWY) || biome.is(ConventionalBiomeTags.IS_ICY)) {
            return  -10;
        } else if (biome.is(ConventionalBiomeTags.IS_COLD)) {
            return  10;
        }else if (biome.is(ConventionalBiomeTags.IS_HOT)) {
            return 35;
        }
        return 20;
    }

    public int getBiomeHumidity(Holder<Biome> biome) {
        if (biome.is(ConventionalBiomeTags.IS_WET)) {
            return 50;
        }else if (biome.is(ConventionalBiomeTags.IS_DRY)) {
            return  10;
        }
        return 30;
    }


    public EntityDataSerializer<Map<Trait, Double>> getTraitSerializer() {
        return ChickensModFabric.TRAIT_SERIALIZER;
    }

    public EntityDataSerializer<Identifier> getResourceSerializer() {
        return ChickensModFabric.RESOURCE_SERIALIZER;
    }
}
