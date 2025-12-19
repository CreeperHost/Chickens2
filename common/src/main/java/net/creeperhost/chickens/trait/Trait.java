package net.creeperhost.chickens.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.Chickens;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created by brandon3055 on 28/11/2025
 */
public class Trait {

    @Nullable
    private String descriptionId;

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("trait", Chickens.TRAIT_REGISTRY.getKey(this));
        }
        return this.descriptionId;
    }

    public double getLaySpeedModifier(double traitValue) {
        return 1;
    }

    public void appendHoverText(Consumer<Component> consumer, double traitValue) {
        consumer.accept(Component.translatable(getDescriptionId()).append(": " + String.format("%.3f", traitValue)));
    }

    public record StateValue(Trait trait, double value) {
        public static final Codec<StateValue> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Chickens.TRAIT_REGISTRY.byNameCodec().fieldOf("trait").forGetter(StateValue::trait),
                Codec.DOUBLE.fieldOf("value").forGetter(StateValue::value)
        ).apply(builder, StateValue::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StateValue> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Chickens.TRAIT_KEY), StateValue::trait,
                ByteBufCodecs.DOUBLE, StateValue::value,
                StateValue::new
        );
    }
}
