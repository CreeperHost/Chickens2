package net.creeperhost.chickens;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.creeperhost.chickens.api.ChickenAPI;
import net.creeperhost.chickens.api.ChickenTransformationRecipe;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.entity.EggTimer;
import net.creeperhost.chickens.init.*;
import net.creeperhost.chickens.network.PacketHandler;
import net.creeperhost.chickens.trait.Trait;
import net.creeperhost.polylib.PolyLib;
import net.fabricmc.api.EnvType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Chickens {
    public static final String MOD_ID = "chickens";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final File CONFIG_DIR = new File("config/chickens");
    public static final File CONFIG_FILE = new File(CONFIG_DIR, "chickens.json");

    public static final ResourceKey<Registry<Trait>> TRAIT_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "trait"));
    public static Registry<Trait> TRAIT_REGISTRY;

    public static void init() {
        PolyLib.initPolyItemData();
        Config.init();

        ModBlocks.BLOCKS.register();
        ModEntities.ENTITIES.register();
        ModBlocks.TILES_ENTITIES.register();
        ModItems.ITEMS.register();
        ModItems.TABS.register();
        ModContainers.CONTAINERS.register();
        ModSounds.SOUNDS.register();
        ModComponentTypes.COMPONENTS.register();

        if (Platform.getEnv() == EnvType.CLIENT) {
            ClientLifecycleEvent.CLIENT_SETUP.register(ChickensClient::clientSetup);
        }

        EntityAttributeRegistry.register(ModEntities.CHICKEN, Chicken::createAttributes);

        InteractionEvent.INTERACT_ENTITY.register(Chickens::onEntityInteract);
        PacketHandler.init();
        EggTimer.init();

        //We need to do this late or the entities are not registered yet
        LifecycleEvent.SETUP.register(ModRecipes::init);

        CommandRegistrationEvent.EVENT.register(Commands::registerCommands);
    }

    public static void register() {
        ChickenTraits.TRAITS.register();
    }

    private static EventResult onEntityInteract(Player player, Entity entity, InteractionHand interactionHand) {
        Level level = player.level();
//        if (!level.isClientSide() && interactionHand == InteractionHand.MAIN_HAND) {
//            for (ChickenVariant variant : ChickenDataManager.getVariants()) {
//                for (int i = 0; i < 2; i++) {
//                    ChickensChicken chicken = new ChickensChicken(ModEntities.CHICKEN.get(), player.level());
//                    chicken.setChickenVariant(variant);
//                    chicken.setRooster(i == 1);
//                    chicken.setPos(player.getX() - 3 + level.random.nextInt(6), player.getY(), player.getZ() - 3 + level.random.nextInt(6));
//                    player.level().addFreshEntity(chicken);
//                }
//            }
//        }
        if (!player.getItemInHand(interactionHand).isEmpty()) {
            for (ChickenTransformationRecipe transformationRecipe : ChickenAPI.TRANSFORMATION_RECIPES) {
                if (transformationRecipe.getEntityTypeIn() == entity.getType() && ItemStack.isSameItem(player.getItemInHand(interactionHand), transformationRecipe.getStack())) {
                    Entity newEntity = transformationRecipe.getEntityTypeOut().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
                    if (newEntity != null) {
                        newEntity.setPos(entity.position());
                        level.addFreshEntity(newEntity);
                        entity.remove(Entity.RemovalReason.DISCARDED);
                        if (!player.isCreative()) {
                            player.getItemInHand(interactionHand).shrink(1);
                        }
                    }
                }
            }
        }
        return EventResult.pass();
    }
}
