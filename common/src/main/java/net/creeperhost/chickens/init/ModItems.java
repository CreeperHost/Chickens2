package net.creeperhost.chickens.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Chickens.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Chickens.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<Item> CHICKEN_ITEM = ITEMS.register("chicken_item", item("chicken_item", ItemChicken::new, new Item.Properties().stacksTo(16)));
    public static final RegistrySupplier<Item> CATCHER_ITEM = ITEMS.register("catcher", item("catcher", ItemChickenCatcher::new));

    public static final RegistrySupplier<Item> CHICKEN_EGG = ITEMS.register("chicken_egg", item("chicken_egg", ItemChickenEgg::new));

    //ItemBlocks
    public static final RegistrySupplier<Item> BREEDER = ITEMS.register("breeder", item("breeder", props -> new BlockItem(ModBlocks.BREEDER.get(), props)));
    public static final RegistrySupplier<Item> INCUBATOR = ITEMS.register("incubator", item("incubator", props -> new BlockItem(ModBlocks.INCUBATOR.get(), props)));
    public static final RegistrySupplier<Item> EGG_CRACKER = ITEMS.register("egg_cracker", item("egg_cracker", props -> new BlockItem(ModBlocks.EGG_CRACKER.get(), props)));
    public static final RegistrySupplier<Item> OVOSCOPE = ITEMS.register("ovoscope", item("ovoscope", props -> new BlockItem(ModBlocks.OVOSCOPE.get(), props)));

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_MODE_TAB = TABS.register("creative_tab", () -> CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.chickens.creative_tab"))
                    .icon(() -> new ItemStack(ModBlocks.BREEDER.get()))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(BREEDER.get());
                        output.accept(INCUBATOR.get());
                        output.accept(EGG_CRACKER.get());
                        output.accept(OVOSCOPE.get());
                        output.accept(CATCHER_ITEM.get());

                        for (ChickenVariant variant : ChickenDataManager.getVariants()) {
                            List<Trait.StateValue> traits = new ArrayList<>();
                            for (TraitConfig trait : variant.traitConfigs()) {
                                traits.add(new Trait.StateValue(trait.trait(), trait.evoLimit()));
                            }

                            ChickenData hen = new ChickenData(variant, false, traits, ChickenData.EntityData.create());
                            ChickenData rooster = new ChickenData(variant, true, traits, ChickenData.EntityData.create());
                            output.accept(hen.toChickenItem());
                            output.accept(rooster.toChickenItem());
                        }
                    }))
    );
    public static final RegistrySupplier<CreativeModeTab> CREATIVE_MODE_TAB_EGGS = TABS.register("creative_tab_eggs", () -> CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.chickens.creative_tab_eggs"))
                    .icon(() -> new ItemStack(Items.EGG))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (ChickenVariant variant : ChickenDataManager.getVariants()) {
                            ChickenData data = new ChickenData(variant, false, Collections.emptyList(), ChickenData.EntityData.create());
                            output.accept(data.toChickenEgg(true));
                        }
                    })
            )
    );

    private static Supplier<Item> item(String name, Function<Item.Properties, Item> item, Item.Properties properties) {
        return () -> item.apply(properties.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, name))));
    }

    private static Supplier<Item> item(String name, Function<Item.Properties, Item> item) {
        return () -> item.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, name))));
    }
}
