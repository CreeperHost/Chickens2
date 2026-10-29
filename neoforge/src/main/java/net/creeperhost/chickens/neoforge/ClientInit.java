package net.creeperhost.chickens.neoforge;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.client.ChickenEggTint;
import net.creeperhost.chickens.client.ChickenGuiTextures;
import net.creeperhost.chickens.client.ChickenItemRender;
import net.creeperhost.chickens.init.ModBlocks;
import net.creeperhost.chickens.init.ModItems;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

/**
 * Created by brandon3055 on 23/02/2024
 */
public class ClientInit {

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientInit::registerRenderers);
        modBus.addListener(ClientInit::registerLayers);
        modBus.addListener(ClientInit::registerItemExtensions);
        modBus.addListener(ClientInit::registerItemTintSources);
    }


    private static void registerRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(net.creeperhost.chickens.init.ModEntities.CHICKEN.get(), net.creeperhost.chickens.client.RenderChickens::new);
        event.registerBlockEntityRenderer(ModBlocks.INCUBATOR_TILE.get(), net.creeperhost.chickens.client.RenderIncubator::new);
    }
    private static void registerLayers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(net.creeperhost.chickens.client.ChickensModel.LAYER_LOCATION, () -> net.creeperhost.chickens.client.ChickensModel.createBodyLayer(false));
        event.registerLayerDefinition(net.creeperhost.chickens.client.ChickensModel.BABY_LAYER_LOCATION, () -> net.creeperhost.chickens.client.ChickensModel.createBodyLayer(true));
    }
    public static void registerItemExtensions(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "chicken_item"), ChickenItemRender.Unbaked.MAP_CODEC);
    }

    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "egg_tint"), ChickenEggTint.MAP_CODEC);
    }
}
