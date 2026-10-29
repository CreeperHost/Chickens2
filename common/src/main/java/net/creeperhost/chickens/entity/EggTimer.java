package net.creeperhost.chickens.entity;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyServerTickEvents;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.item.ItemChickenEgg;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.WeakHashMap;

/**
 * Created by brandon3055 on 06/06/2024
 */
public class EggTimer {

    private static final WeakHashMap<ItemEntity, Long> EGGS = new WeakHashMap<>();

    public static void init() {
        PolyEntityEvents.JOIN_LEVEL.register(EggTimer::entitySpawn);
        PolyServerTickEvents.TICK_START.register(EggTimer::serverTick);
    }

    private static void entitySpawn(Entity entity, ServerLevel level, CancelContext context) {
        if (entity instanceof ItemEntity item) {
            ItemStack stack = item.getItem();
            if (stack.getItem() instanceof ItemChickenEgg egg && egg.isFertilized(stack)) {
                EGGS.put(item, System.currentTimeMillis());
            }
        }

    }

    private static void serverTick(MinecraftServer server) {
        EGGS.entrySet().removeIf(e -> {
            if (System.currentTimeMillis() > e.getValue() + (Config.INSTANCE.eggItemMaxTimeOnGround * 1000L)) {
                ItemStack stack = e.getKey().getItem();
                if (stack.getItem() instanceof ItemChickenEgg egg) {
                    egg.setNotViable(stack);
                }
                return true;
            }
            return false;
        });
    }

}
