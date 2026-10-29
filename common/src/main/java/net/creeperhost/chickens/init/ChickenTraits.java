package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import java.util.function.Supplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.SpeedTrait;
import net.creeperhost.chickens.trait.Trait;

/**
 * Created by brandon3055 on 24/11/2025
 */
public class ChickenTraits {

    //TODO, would like to switch this over to a custom registry if I can figure out how to do that with architectury.
//    private static final Map<Identifier, Trait> TRAITS = new HashMap<>();

    public static final PolyRegistry<Trait> TRAITS = PolyRegistry.create(Chickens.TRAIT_KEY, Chickens.MOD_ID);

    public static final Supplier<Trait> SPEED       = TRAITS.register("speed", SpeedTrait::new);
//    public static final Supplier<Trait> PRODUCTION  = TRAITS.register("production", Trait::new);
    public static final Supplier<Trait> MATURATION = TRAITS.register("maturation", Trait::new);
    public static final Supplier<Trait> LIFESPAN    = TRAITS.register("lifespan", Trait::new);


//    public static final Trait SPEED         = register("speed", SpeedTrait::new);
//    public static final Trait PRODUCTION    = register("production", SimpleTrait::new);
//    public static final Trait GROWTH        = register("growth", SimpleTrait::new);
//    public static final Trait LIFESPAN      = register("lifespan", SimpleTrait::new);


//    public static Trait register(Identifier name, Function<Identifier, Trait> traitFunc) {
//        if (TRAITS.containsKey(name)) throw new RuntimeException("Duplicate holder id: " + name);
//        Trait holder = traitFunc.apply(name);
//        TRAITS.put(name, holder);
//        return holder;
//    }
//
//    private static Trait register(String name, Function<Identifier, Trait> traitFunc) {
//        return register(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, name), traitFunc);
//    }
//
//    @Nullable
//    public static Trait getTrait(Identifier name) {
//        return TRAITS.getOrDefault(name, null);
//    }
}
