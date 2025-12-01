package net.creeperhost.chickens.trait;

import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 01/12/2025
 */
public class SpeedTrait extends SimpleTrait {
    public SpeedTrait(ResourceLocation traitId) {
        super(traitId);
    }

    @Override
    public double getLaySpeedModifier(double traitValue) {
        //Super simple math here, a trait value of 1 means "no change", 2 would double the rate, 10 would 10x the rate, 0.5 would halve the rate.
        return 1D / traitValue;
    }
}
