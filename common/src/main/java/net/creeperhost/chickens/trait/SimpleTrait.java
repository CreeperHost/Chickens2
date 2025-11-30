package net.creeperhost.chickens.trait;

import net.minecraft.resources.ResourceLocation;

/**
 * Created by brandon3055 on 28/11/2025
 */
public class SimpleTrait implements Trait {

    private final ResourceLocation traitId;

    public SimpleTrait(ResourceLocation traitId) {
        this.traitId = traitId;
    }

    @Override
    public ResourceLocation getId() {
        return traitId;
    }
}
