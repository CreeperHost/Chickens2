package net.creeperhost.chickens.blockentities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Created by brandon3055 on 06/03/2026
 */
public record TraitFilter(Trait trait, boolean greater, double value) {

    public static final Codec<TraitFilter> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Chickens.TRAIT_REGISTRY.byNameCodec().fieldOf("trait").forGetter(TraitFilter::trait),
            Codec.BOOL.fieldOf("greater").forGetter(TraitFilter::greater),
            Codec.DOUBLE.fieldOf("value").forGetter(TraitFilter::value)
    ).apply(builder, TraitFilter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TraitFilter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Chickens.TRAIT_KEY), TraitFilter::trait,
            ByteBufCodecs.BOOL, TraitFilter::greater,
            ByteBufCodecs.DOUBLE, TraitFilter::value,
            TraitFilter::new
    );
}
