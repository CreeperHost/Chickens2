package net.creeperhost.chickens.fabric;

import net.creeperhost.chickens.ChickensClient;
import net.creeperhost.chickens.client.ChickensModel;
import net.creeperhost.chickens.client.RenderChickens;
import net.creeperhost.chickens.client.RenderIncubator;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.init.ModEntities;
import net.creeperhost.polylib.registry.PolyScreens;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.animal.chicken.Chicken;

public class FabricClient {
    public static void init() {
        ChickensClient.clientSetup();
        PolyScreens.flush();
        ModelLayerRegistry.registerModelLayer(ChickensModel.LAYER_LOCATION, () -> ChickensModel.createBodyLayer(false));
        ModelLayerRegistry.registerModelLayer(ChickensModel.BABY_LAYER_LOCATION, () -> ChickensModel.createBodyLayer(true));
        EntityRendererRegistry.register(ModEntities.CHICKEN.get(), RenderChickens::new);
        BlockEntityRendererRegistry.register(ModBlocks.INCUBATOR_TILE.get(), RenderIncubator::new);
    }
}
