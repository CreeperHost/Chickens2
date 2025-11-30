package net.creeperhost.chickens.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

/**
 * Created by brandon3055 on 16/11/2025
 */
public record ChickenSpawn(List<TagKey<Biome>> biomes, int weight) {

    public static final Codec<ChickenSpawn> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            TagKey.codec(Registries.BIOME).listOf().fieldOf("biomes").forGetter(ChickenSpawn::biomes),
            Codec.INT.fieldOf("weight").forGetter(ChickenSpawn::weight)
    ).apply(builder, ChickenSpawn::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChickenSpawn> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(Registries.BIOME).apply(ByteBufCodecs.list()), ChickenSpawn::biomes,
            ByteBufCodecs.INT, ChickenSpawn::weight,
            ChickenSpawn::new
    );
}
