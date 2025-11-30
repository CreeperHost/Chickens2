package net.creeperhost.chickens.init;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.SimpleTrait;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by brandon3055 on 24/11/2025
 */
public class ChickenTraits {

    //TODO, would like to switch this over to a custom registry if I can figure out how to do that with architectury.
    private static final Map<ResourceLocation, Trait> TRAITS = new HashMap<>();


    public static final Trait SPEED         = register("speed", SimpleTrait::new);
    public static final Trait PRODUCTION    = register("production", SimpleTrait::new);
    public static final Trait GROWTH        = register("growth", SimpleTrait::new);
    public static final Trait LIFESPAN      = register("lifespan", SimpleTrait::new);


    public static Trait register(ResourceLocation name, Function<ResourceLocation, Trait> traitFunc) {
        if (TRAITS.containsKey(name)) throw new RuntimeException("Duplicate trait id: " + name);
        Trait trait = traitFunc.apply(name);
        TRAITS.put(name, trait);
        return trait;
    }

    private static Trait register(String name, Function<ResourceLocation, Trait> traitFunc) {
        return register(ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, name), traitFunc);
    }

    @Nullable
    public static Trait getTrait(ResourceLocation name) {
        return TRAITS.getOrDefault(name, null);
    }
}
