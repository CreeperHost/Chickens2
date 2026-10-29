package net.creeperhost.chickens.neoforge.mixin;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeAttributesMixin {
    @Shadow public abstract ModifiableBiomeInfo modifiableBiomeInfo();

    // NeoForge 26.3 applies biome modifiers but its attribute getter still returns the original map.
    @Inject(method = "getAttributes", at = @At("RETURN"), cancellable = true)
    private void chickens$useModifiedAttributes(CallbackInfoReturnable<EnvironmentAttributeMap> cir) {
        ModifiableBiomeInfo info = modifiableBiomeInfo();
        if (info != null && info.getModifiedBiomeInfo() != null) {
            cir.setReturnValue(info.getModifiedBiomeInfo().attributes());
        }
    }
}
