package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import java.util.List;

public final class ChickenVariantNetworking {
    public record VariantsPayload(List<ChickenVariant> variants) implements CustomPacketPayload {
        public static final Type<VariantsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "variants"));
        public static final StreamCodec<RegistryFriendlyByteBuf, VariantsPayload> CODEC = StreamCodec.composite(
                ChickenVariant.STREAM_CODEC.apply(ByteBufCodecs.list()), VariantsPayload::variants, VariantsPayload::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(VariantsPayload.TYPE, VariantsPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> ChickenVariantClient.receive(payload.variants())));
    }
}
