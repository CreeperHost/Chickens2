package net.creeperhost.chickens;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.event.events.server.PolyServerCommandEvents;

import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
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
import net.creeperhost.polylib.init.DataComps;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.chicken.Chicken;
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

    public static final ResourceKey<Registry<Trait>> TRAIT_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "trait"));
    public static Registry<Trait> TRAIT_REGISTRY;

    public static void init() {
        DataComps.registerData();
        Config.init();

        ModBlocks.BLOCKS.init();
        ModEntities.ENTITIES.init();
        ModBlocks.TILES_ENTITIES.init();
        ModItems.ITEMS.init();
        ModItems.TABS.init();
        ModContainers.CONTAINERS.init();
        ModSounds.SOUNDS.init();
        ModComponentTypes.COMPONENTS.init();
        ChickenTraits.TRAITS.init();


        PolyPlayerEvents.ENTITY_INTERACT.register(Chickens::onEntityInteract);
        PacketHandler.init();
        EggTimer.init();

        //We need to do this late or the entities are not registered yet
        PolyServerLifecycleEvents.SERVER_STARTED.register(server -> ModRecipes.init());

        PolyServerCommandEvents.REGISTER_COMMANDS.register(Commands::registerCommands);
    }

    public static void register() {
    }

    private static void onEntityInteract(Player player, Entity entity, InteractionHand interactionHand) {
        Level level = player.level();
//        if (!level.isClientSide() && interactionHand == InteractionHand.MAIN_HAND) {
//            for (ChickenVariant variant : ChickenDataManager.getVariants()) {
//                for (int i = 0; i < 2; i++) {
//                    ChickensChicken chicken = new ChickensChicken(ModEntities.CHICKEN.get(), player.level());
//                    chicken.setChickenVariant(variant);
//                    chicken.setRooster(i == 1);
//                    chicken.setPos(player.getX() - 3 + level.getRandom().nextInt(6), player.getY(), player.getZ() - 3 + level.getRandom().nextInt(6));
//                    player.level().addFreshEntity(chicken);
//                }
//            }
//        }

        ItemStack stack = player.getItemInHand(interactionHand);
        if (stack.isEmpty() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        for (ChickenTransformationRecipe recipe : ChickenAPI.TRANSFORMATION_RECIPES) {
            if (recipe.getEntityTypeIn() == entity.getType() && ItemStack.isSameItem(stack, recipe.getStack())) {
                ChickensChicken chicken = ModEntities.CHICKEN.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
                ChickenVariant variant = ChickenDataManager.getVariant(recipe.getVariant());
                if (chicken == null || variant == null) {
                    return;
                }
                chicken.setPos(entity.position());
                chicken.setChickenVariant(variant);
                chicken.initRandomTraits(serverLevel, variant, 0);
                chicken.setRooster(serverLevel.getRandom().nextBoolean());
                level.addFreshEntity(chicken);
                entity.remove(Entity.RemovalReason.DISCARDED);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return;
            }
        }

    }
}
