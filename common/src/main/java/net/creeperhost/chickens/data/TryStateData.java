package net.creeperhost.chickens.data;

import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.TriState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static net.minecraft.util.TriState.*;

public class TryStateData extends AbstractDataStore<TriState> {
    public TryStateData() {
        super(DEFAULT);
    }

    public TryStateData(TriState defaultValue) {
        super(defaultValue);
    }

    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeEnum(this.value);
    }

    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.value = this.validValue(buf.readEnum(TriState.class), this.value);
    }

    public void toTag(ValueOutput output) {
        output.putString("value", this.value.name());
    }

    public void fromTag(ValueInput input) {
        try {
            this.value = TriState.valueOf(input.getStringOr("value", DEFAULT.name()));
        } catch (Throwable ignored) {
        }
    }
}