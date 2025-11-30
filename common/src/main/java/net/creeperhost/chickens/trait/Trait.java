package net.creeperhost.chickens.trait;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 28/11/2025
 */
public interface Trait {

    ResourceLocation getId();

}
