package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import java.util.function.Supplier;
import net.creeperhost.chickens.Chickens;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

import java.util.List;

public class ModSounds {
    public static final PolyRegistry<SoundEvent> SOUNDS = PolyRegistry.create(Registries.SOUND_EVENT, Chickens.MOD_ID);
    public static final Supplier<SoundEvent> CHICKEN_IDLE_1 = SOUNDS.register("chicken_idle_1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_idle_1")));
    public static final Supplier<SoundEvent> CHICKEN_IDLE_2 = SOUNDS.register("chicken_idle_2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_idle_2")));
    public static final Supplier<SoundEvent> CHICKEN_IDLE_3 = SOUNDS.register("chicken_idle_3", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_idle_3")));
    public static final Supplier<SoundEvent> CHICKEN_IDLE_4 = SOUNDS.register("chicken_idle_4", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_idle_4")));
    private static final List<Supplier<SoundEvent>> CHICKEN_SOUNDS = List.of(CHICKEN_IDLE_1, CHICKEN_IDLE_2, CHICKEN_IDLE_3, CHICKEN_IDLE_4);

    public static SoundEvent getRandomIdleSound(Level level) {
        return CHICKEN_SOUNDS.get(level.getRandom().nextInt(CHICKEN_SOUNDS.size())).get();
    }
}
