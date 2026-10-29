package net.creeperhost.chickens.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import java.util.function.Supplier;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.data.TraitConfig;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.item.ItemChickenCatcher;
import net.creeperhost.chickens.item.ItemChickenEgg;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ModItems {
    public static final PolyRegistry<Item> ITEMS = PolyRegistry.create(Registries.ITEM, Chickens.MOD_ID);
    public static final PolyRegistry<CreativeModeTab> TABS = PolyRegistry.create(Registries.CREATIVE_MODE_TAB, Chickens.MOD_ID);

    public static final Supplier<Item> CHICKEN_ITEM = ITEMS.register("chicken_item", item("chicken_item", ItemChicken::new, new Item.Properties().stacksTo(16)));
    public static final Supplier<Item> CATCHER_ITEM = ITEMS.register("catcher", item("catcher", ItemChickenCatcher::new));

    public static final Supplier<Item> CHICKEN_EGG = ITEMS.register("chicken_egg", item("chicken_egg", ItemChickenEgg::new));

    //ItemBlocks
    public static final Supplier<Item> BREEDER = ITEMS.register("breeder", item("breeder", props -> new BlockItem(ModBlocks.BREEDER.get(), props)));
    public static final Supplier<Item> INCUBATOR = ITEMS.register("incubator", item("incubator", props -> new BlockItem(ModBlocks.INCUBATOR.get(), props)));
    public static final Supplier<Item> EGG_CRACKER = ITEMS.register("egg_cracker", item("egg_cracker", props -> new BlockItem(ModBlocks.EGG_CRACKER.get(), props)));
    public static final Supplier<Item> SORTER = ITEMS.register("sorter", item("sorter", props -> new BlockItem(ModBlocks.SORTER.get(), props)));

    public static final Supplier<CreativeModeTab> CREATIVE_MODE_TAB = TABS.registerCreativeTab("creative_tab", "Chickens",
                    () -> new ItemStack(ModBlocks.BREEDER.get()),
                    (itemDisplayParameters, output) -> {
                        output.accept(BREEDER.get());
                        output.accept(INCUBATOR.get());
                        output.accept(EGG_CRACKER.get());
                        output.accept(SORTER.get());
                        output.accept(CATCHER_ITEM.get());

                        for (ChickenVariant variant : ChickenDataManager.getVariants()) {
                            List<Trait.StateValue> traits = new ArrayList<>();
                            for (TraitConfig trait : variant.traitConfigs()) {
                                traits.add(new Trait.StateValue(trait.trait(), trait.evoLimit()));
                            }

                            ChickenData hen = new ChickenData(variant.id(), false, traits, ChickenData.EntityData.create());
                            ChickenData rooster = new ChickenData(variant.id(), true, traits, ChickenData.EntityData.create());
                            output.accept(hen.toChickenItem());
                            output.accept(rooster.toChickenItem());
                        }
                    }
    );
    public static final Supplier<CreativeModeTab> CREATIVE_MODE_TAB_EGGS = TABS.registerCreativeTab("creative_tab_eggs", "Chicken Eggs",
                    () -> new ItemStack(Items.EGG),
                    (itemDisplayParameters, output) -> {
                        for (ChickenVariant variant : ChickenDataManager.getVariants()) {
                            ChickenData data = new ChickenData(variant.id(), false, Collections.emptyList(), ChickenData.EntityData.create());
                            output.accept(data.toChickenEgg(true));
                        }
                    }
    );

    private static Supplier<Item> item(String name, Function<Item.Properties, Item> item, Item.Properties properties) {
        return () -> item.apply(properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Chickens.MOD_ID, name))));
    }

    private static Supplier<Item> item(String name, Function<Item.Properties, Item> item) {
        return () -> item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Chickens.MOD_ID, name))));
    }
}
