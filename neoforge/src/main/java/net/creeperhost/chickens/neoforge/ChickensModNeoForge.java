package net.creeperhost.chickens.neoforge;

import dev.architectury.platform.Platform;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;

import java.util.HashMap;
import java.util.Map;

@Mod (Chickens.MOD_ID)
public class ChickensModNeoForge {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Chickens.MOD_ID);
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Map<Trait, Double>>> TRAIT_SERIALIZER = ENTITY_DATA_SERIALIZER.register("trait_serializer", () -> EntityDataSerializer.forValueType(ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(Chickens.TRAIT_KEY), ByteBufCodecs.DOUBLE)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ResourceLocation>> RESOURCE_SERIALIZER = ENTITY_DATA_SERIALIZER.register("resource_serializer", () -> EntityDataSerializer.forValueType(ResourceLocation.STREAM_CODEC));

    public ChickensModNeoForge(IEventBus iEventBus) {
        iEventBus.addListener(ChickensModNeoForge::createRegistries);
        iEventBus.addListener(ChickensModNeoForge::modifyRegistries);
        ENTITY_DATA_SERIALIZER.register(iEventBus);

        Chickens.init();

        if(Platform.getEnv().isClient()) {
            ClientInit.init(iEventBus);
        }
    }

    public static void createRegistries(NewRegistryEvent event) {
        Chickens.TRAIT_REGISTRY = event.create(new RegistryBuilder<>(Chickens.TRAIT_KEY)
                .sync(true)
        );
    }

    public static void modifyRegistries(ModifyRegistriesEvent event) {
        Chickens.register();
    }
}
