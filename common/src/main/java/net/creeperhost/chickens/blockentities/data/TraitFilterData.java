package net.creeperhost.chickens.blockentities.data;

import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 06/03/2026
 */
public class TraitFilterData extends AbstractDataStore<List<TraitFilter>> {

    public TraitFilterData() {
        super(new ArrayList<>());
    }

    @Override
    public List<TraitFilter> set(List<TraitFilter> value) {
        return super.set(value);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        TraitFilter.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, value);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        value = validValue(TraitFilter.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf), value);
    }

    @Override
    public void toTag(ValueOutput output) {
        output.store("value", TraitFilter.CODEC.listOf(), value);
    }

    @Override
    public void fromTag(ValueInput input) {
        value = validValue(input.read("value", TraitFilter.CODEC.listOf()).orElse(value), value);
    }
}
