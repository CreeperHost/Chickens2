package net.creeperhost.chickens.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.init.ChickenTraits;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Created by brandon3055 on 28/11/2025
 */
public interface Trait {

    ResourceLocation getId();

    default double getLaySpeedModifier(double traitValue) {
        return 1;
    }

    default void appendHoverText(Consumer<Component> consumer, double traitValue) {
        consumer.accept(Component.translatable("trait." + getId().getNamespace() + "." + getId().getPath() + ".value", Math.round(traitValue * 100) / 100D));
    }

    record State(ResourceLocation id, double value) {
        public static final Codec<State> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(State::id),
                Codec.DOUBLE.fieldOf("value").forGetter(State::value)
        ).apply(builder, State::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, State> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, State::id,
                ByteBufCodecs.DOUBLE, State::value,
                State::new
        );
    }

    static Trait fromId(ResourceLocation traitId) {
        return ChickenTraits.getTrait(traitId);
    }
}
