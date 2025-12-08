package net.creeperhost.chickens.init;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Chickens.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    //Chickens
    @Deprecated
    public static final RegistrySupplier<DataComponentType<String>> CHICKEN_TYPE = COMPONENTS.register("chicken_type", () -> DataComponentType.<String>builder().
            persistent(Codec.STRING.orElse("")).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Integer>> CHICKENS_GAIN = COMPONENTS.register("gain", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Integer>> CHICKENS_GROWTH = COMPONENTS.register("growth", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Integer>> CHICKENS_STRENGTH = COMPONENTS.register("strength", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Integer>> CHICKENS_LIFESPAN = COMPONENTS.register("lifespan", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Integer>> LOVE = COMPONENTS.register("love", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<Boolean>> IS_BABY = COMPONENTS.register("is_baby", () -> DataComponentType.<Boolean>builder().
            persistent(Codec.BOOL.orElse(false)).networkSynchronized(ByteBufCodecs.BOOL).build());


    //Eggs
    public static final RegistrySupplier<DataComponentType<Boolean>> EGG_FERTILIZED = COMPONENTS.register("fertilized", () -> DataComponentType.<Boolean>builder().
            persistent(Codec.BOOL.orElse(false)).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final RegistrySupplier<DataComponentType<Integer>> EGG_MISSED = COMPONENTS.register("missed", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    public static final RegistrySupplier<DataComponentType<Integer>> EGG_PROGRESS = COMPONENTS.register("progress", () -> DataComponentType.<Integer>builder().
            persistent(Codec.INT.orElse(0)).networkSynchronized(ByteBufCodecs.INT).build());

    @Deprecated
    public static final RegistrySupplier<DataComponentType<String>> EGG_CHICKEN_TYPE = COMPONENTS.register("egg_chicken_type", () -> DataComponentType.<String>builder().
            persistent(Codec.STRING.orElse("")).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

//    public static final RegistrySupplier<DataComponentType<ChickenVariant>> EGG_VARIANT = COMPONENTS.register("chicken_variant", () -> DataComponentType.<ChickenVariant>builder().persistent(ChickenVariant.CODEC).networkSynchronized(ChickenVariant.STREAM_CODEC).build());
//    public static final RegistrySupplier<DataComponentType<List<Trait.State>>> EGG_TRAITS = COMPONENTS.register("chicken_traits", () -> DataComponentType.<List<Trait.State>>builder().persistent(Trait.State.CODEC.listOf()).networkSynchronized(Trait.State.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    public static final RegistrySupplier<DataComponentType<ChickenData>> EGG_DATA = COMPONENTS.register("chicken_data", () -> DataComponentType.<ChickenData>builder().persistent(ChickenData.CODEC).networkSynchronized(ChickenData.STREAM_CODEC).build());

}
