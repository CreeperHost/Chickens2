package net.creeperhost.chickens.blockentities.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 06/03/2026
 */
public class ListData<T> extends AbstractDataStore<List<T>> {

    private final Codec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public ListData(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(new ArrayList<>());
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    @Override
    public List<T> set(List<T> value) {
        return super.set(value);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        streamCodec.apply(ByteBufCodecs.list()).encode(buf, value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(streamCodec.apply(ByteBufCodecs.list()).decode(buf), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.store("value", codec.listOf(), value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = validValue(input.read("value", codec.listOf()).orElse(value), value);
    }
}

//public class VariantFilterData extends AbstractDataStore<List<ChickenVariant.GUI>> {
//
//    public VariantFilterData() {
//        super(new ArrayList<>());
//    }
//
//    @Override
//    public List<ChickenVariant.GUI> set(List<ChickenVariant.GUI> value) {
//        return super.set(value);
//    }
//
//    @Override
//    public void toBytes(RegistryFriendlyByteBuf buf) {
//        ChickenVariant.GUI.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, value);
//    }
//
//    @Override
//    public void fromBytes(RegistryFriendlyByteBuf buf) {
//        value = validValue(ChickenVariant.GUI.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf), value);
//    }
//
//    @Override
//    public void toTag(ValueOutput output) {
//        output.store("value", ChickenVariant.GUI.CODEC.listOf(), value);
//    }
//
//    @Override
//    public void fromTag(ValueInput input) {
//        value = validValue(input.read("value", ChickenVariant.GUI.CODEC.listOf()).orElse(value), value);
//    }
//}
