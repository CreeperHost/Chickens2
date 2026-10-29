package net.creeperhost.chickens.polylib;

import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Set;

public class CommonTags {
    public static TagKey<Item> DYE = registerTag("dyes");
    private static TagKey<Item> SEEDS = registerTag("seeds");

    private static final Set<Item> SEED_ITEMS = Set.of(Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);

    public static TagKey<Item> registerTag(String string) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", string));
    }

    public static boolean isSeeds(ItemStack stack) {
        if (!Services.PLATFORM.getPlatformName().equalsIgnoreCase("fabric")) {
            return stack.is(SEEDS);
        }
        //On fabric there is not default seeds tag
        return stack.is(SEEDS) || SEED_ITEMS.contains(stack.getItem());
    }
}
