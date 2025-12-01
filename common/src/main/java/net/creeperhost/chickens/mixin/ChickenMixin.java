package net.creeperhost.chickens.mixin;

import net.creeperhost.chickens.entity.ChickensChicken;
import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by brandon3055 on 01/12/2025
 */
@Mixin (Chicken.class)
public class ChickenMixin {

    @Unique
    private Chicken chickens$getThis() {
        return (Chicken) (Object) this;
    }

    @Inject (
            method = "Lnet/minecraft/world/entity/animal/Chicken;aiStep()V",
            at = @At (
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/animal/Chicken;isAlive()Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    public void onAiStep(CallbackInfo ci) {
        //Disable vanilla egg logic
        if (chickens$getThis() instanceof ChickensChicken){
            ci.cancel();
        }
    }

}
