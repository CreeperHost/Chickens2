package net.creeperhost.chickens;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModEntities;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;

/**
 * Created by brandon3055 on 21/12/2025
 */
public class Commands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, net.minecraft.commands.Commands.CommandSelection selection) {
        dispatcher.register(net.minecraft.commands.Commands.literal("summon_chicken")
                .requires(stack -> stack.hasPermission(2))
                .then(net.minecraft.commands.Commands.argument("variant", ResourceLocationArgument.id())
                        .suggests((c, builder) -> {
                            ChickenDataManager.getVariantIds().forEach(id -> builder.suggest(id.toString()));
                            return builder.buildFuture();
                        })
                        .executes(c -> spawnChicken(c, ResourceLocationArgument.getId(c, "variant"), false, -1, 0))
                        .then(net.minecraft.commands.Commands.argument("rooster", BoolArgumentType.bool())
                                .executes(c -> spawnChicken(c, ResourceLocationArgument.getId(c, "variant"), BoolArgumentType.getBool(c, "rooster"), -1, 0))
                                .then(net.minecraft.commands.Commands.argument("traits", DoubleArgumentType.doubleArg(0, 100))
                                        .executes(c -> spawnChicken(c, ResourceLocationArgument.getId(c, "variant"), BoolArgumentType.getBool(c, "rooster"), DoubleArgumentType.getDouble(c, "traits"), 0))
                                        .then(net.minecraft.commands.Commands.argument("taming", DoubleArgumentType.doubleArg(-Config.INSTANCE.tamingModifierLimit, Config.INSTANCE.tamingModifierLimit))
                                                .executes(c -> spawnChicken(c, ResourceLocationArgument.getId(c, "variant"), BoolArgumentType.getBool(c, "rooster"), DoubleArgumentType.getDouble(c, "traits"), DoubleArgumentType.getDouble(c, "taming")))
                                        )
                                )
                        )
                ));
    }

    private static int spawnChicken(CommandContext<CommandSourceStack> stack, ResourceLocation variantId, boolean rooster, double traits, double tamingMod) throws CommandSyntaxException {
        ChickenVariant variant = ChickenDataManager.getVariant(variantId);
        if (variant == null) {
            stack.getSource().sendFailure(Component.literal("Could not find variant: " + variantId));
            return 1;
        }

        ServerLevel serverLevel = stack.getSource().getLevel();
        Player player = stack.getSource().getPlayerOrException();
        ChickensChicken chicken = ModEntities.CHICKEN.get().create(serverLevel, EntitySpawnReason.COMMAND);
        if (chicken == null) {
            stack.getSource().sendFailure(Component.literal("Failed to create chicken entity"));
            return 1;
        }

        chicken.snapTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        chicken.setChickenVariant(variant);
        chicken.initRandomTraits(serverLevel, variant, traits);
        chicken.setRooster(rooster);
        chicken.setTamingModifier(tamingMod);
        serverLevel.addFreshEntity(chicken);
        return 0;
    }
}
