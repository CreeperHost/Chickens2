package net.creeperhost.chickens.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.SpeedTrait;
import net.creeperhost.chickens.trait.Trait;

/**
 * Created by brandon3055 on 24/11/2025
 */
public class ChickenTraits {

    //TODO, would like to switch this over to a custom registry if I can figure out how to do that with architectury.
//    private static final Map<ResourceLocation, Trait> TRAITS = new HashMap<>();

    public static final DeferredRegister<Trait> TRAITS = DeferredRegister.create(Chickens.MOD_ID, Chickens.TRAIT_KEY);

    public static final RegistrySupplier<Trait> SPEED       = TRAITS.register("speed", SpeedTrait::new);
    public static final RegistrySupplier<Trait> PRODUCTION  = TRAITS.register("production", Trait::new);
    public static final RegistrySupplier<Trait> MATURATION = TRAITS.register("maturation", Trait::new);
    public static final RegistrySupplier<Trait> LIFESPAN    = TRAITS.register("lifespan", Trait::new);


//    public static final Trait SPEED         = register("speed", SpeedTrait::new);
//    public static final Trait PRODUCTION    = register("production", SimpleTrait::new);
//    public static final Trait GROWTH        = register("growth", SimpleTrait::new);
//    public static final Trait LIFESPAN      = register("lifespan", SimpleTrait::new);


//    public static Trait register(ResourceLocation name, Function<ResourceLocation, Trait> traitFunc) {
//        if (TRAITS.containsKey(name)) throw new RuntimeException("Duplicate holder id: " + name);
//        Trait holder = traitFunc.apply(name);
//        TRAITS.put(name, holder);
//        return holder;
//    }
//
//    private static Trait register(String name, Function<ResourceLocation, Trait> traitFunc) {
//        return register(ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, name), traitFunc);
//    }
//
//    @Nullable
//    public static Trait getTrait(ResourceLocation name) {
//        return TRAITS.getOrDefault(name, null);
//    }
}
