package net.creeperhost.chickens.fabric.datagen;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenProduct;
import net.creeperhost.chickens.data.ChickenSpawn;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.data.TraitConfig;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.trait.Trait;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This is based on Fabric's FabricCodecDataProvider
 * <p>
 * Created by brandon3055 on 10/11/2025
 */
public class ChickenVariantProvider extends FabricCodecDataProvider<ChickenVariant> {
    public static final Identifier flint = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "flint");
    public static final Identifier log = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "log");
    public static final Identifier sand = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "sand");
    public static final Identifier quartz = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "quartz");
    public static final Identifier soulsand = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "soulsand");

    public static final Identifier black = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "black");
    public static final Identifier blue = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "blue");
    public static final Identifier brown = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "brown");
    public static final Identifier cyan = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "cyan");
    public static final Identifier gray = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "gray");
    public static final Identifier green = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "green");
    public static final Identifier light_blue = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "light_blue");
    public static final Identifier light_gray = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "light_gray");
    public static final Identifier lime = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "lime");
    public static final Identifier magenta = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "magenta");
    public static final Identifier orange = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "orange");
    public static final Identifier pink = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "pink");
    public static final Identifier purple = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "purple");
    public static final Identifier red = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "red");
    public static final Identifier white = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "white");
    public static final Identifier yellow = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "yellow");

    public static final Identifier blaze = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "blaze");
    public static final Identifier clay = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "clay");
    public static final Identifier coal = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "coal");
    public static final Identifier copper = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "copper");
    public static final Identifier diamond = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "diamond");
    public static final Identifier emerald = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "emerald");
    public static final Identifier ender = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "ender");
    public static final Identifier ghast = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "ghast");
    public static final Identifier glass = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "glass");
    public static final Identifier glowstone = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "glowstone");
    public static final Identifier gold = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "gold");
    public static final Identifier gunpowder = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "gunpowder");
    public static final Identifier iron = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "iron");
    public static final Identifier lapis = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "lapis");
    public static final Identifier lava = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "lava");
    public static final Identifier leather = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "leather");
    public static final Identifier magma = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "magma");
    public static final Identifier netherite = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "netherite");
    public static final Identifier netherwart = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "netherwart");
    public static final Identifier obsidian = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "obsidian");
    public static final Identifier pcrystal = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "pcrystal");
    public static final Identifier pshard = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "pshard");
    public static final Identifier redstone = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "redstone");
    public static final Identifier slime = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "slime");
    public static final Identifier smart = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "smart");
    public static final Identifier snowball = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "snowball");
    public static final Identifier string = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "string");
    public static final Identifier water = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "water");


    protected ChickenVariantProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.DATA_PACK, "variants", ChickenVariant.CODEC);
    }


    private void addChickens(BiConsumer<Identifier, ChickenVariant> consumer, HolderLookup.Provider provider) {

        // === Natural Spawn Chickens === //
        simple(provider, flint, "Flint", Items.FLINT)
                .spawn(List.of(BiomeTags.IS_MOUNTAIN, ConventionalBiomeTags.IS_STONY_SHORES, ConventionalBiomeTags.IS_MOUNTAIN), 10)
                .eggColour(0x6b6b47)
                .build(consumer);
        simple(provider, log, "Log", Items.OAK_LOG)
                .spawn(List.of(BiomeTags.IS_FOREST, ConventionalBiomeTags.IS_FOREST), 10)
                .eggColour(0x98846d)
                .build(consumer);
        simple(provider, sand, "Sand", Items.SAND)
                .spawn(List.of(BiomeTags.HAS_VILLAGE_DESERT, BiomeTags.HAS_DESERT_PYRAMID, BiomeTags.IS_BEACH, ConventionalBiomeTags.IS_DESERT), 10)
                .eggColour(0xece5b1)
                .build(consumer);
        simple(provider, quartz, "Quartz", Items.QUARTZ)
                .spawn(List.of(BiomeTags.IS_NETHER, ConventionalBiomeTags.IS_NETHER), 10)
                .eggColour(0x4d0000)
                .build(consumer);
        simple(provider, soulsand, "Soulsand", Items.SOUL_SAND)
                .spawn(List.of(BiomeTags.IS_NETHER, ConventionalBiomeTags.IS_NETHER), 10)
                .eggColour(0x453125)
                .build(consumer);

        // === Dyes === //
        simple(provider, black, "Black", Items.BLACK_DYE)
                .eggColour(DyeColor.BLACK.getFireworkColor())
                .build(consumer);
        simple(provider, blue, "Blue", Items.BLUE_DYE)
                .eggColour(DyeColor.BLUE.getFireworkColor())
                .build(consumer);
        simple(provider, brown, "Brown", Items.BROWN_DYE)
                .eggColour(DyeColor.BROWN.getFireworkColor())
                .build(consumer);
        simple(provider, cyan, "Cyan", Items.CYAN_DYE)
                .eggColour(DyeColor.CYAN.getFireworkColor())
                .parents(blue, green)
                .build(consumer);
        simple(provider, gray, "Gray", Items.GRAY_DYE)
                .eggColour(DyeColor.GRAY.getFireworkColor())
                .parents(black, white)
                .build(consumer);
        simple(provider, green, "Green", Items.GREEN_DYE)
                .eggColour(DyeColor.GREEN.getFireworkColor())
                .build(consumer);
        simple(provider, light_blue, "Light Blue", Items.LIGHT_BLUE_DYE)
                .eggColour(DyeColor.LIGHT_BLUE.getFireworkColor())
                .parents(white, blue)
                .build(consumer);
        simple(provider, light_gray, "Light Gray", Items.LIGHT_GRAY_DYE)
                .eggColour(DyeColor.LIGHT_GRAY.getFireworkColor())
                .parents(green, white)
                .build(consumer);
        simple(provider, lime, "Lime", Items.LIME_DYE)
                .eggColour(DyeColor.LIME.getFireworkColor())
                .parents(green, white)
                .build(consumer);
        simple(provider, magenta, "Magenta", Items.MAGENTA_DYE)
                .eggColour(DyeColor.MAGENTA.getFireworkColor())
                .parents(purple, pink)
                .build(consumer);
        simple(provider, orange, "Orange", Items.ORANGE_DYE)
                .eggColour(DyeColor.ORANGE.getFireworkColor())
                .parents(red, yellow)
                .build(consumer);
        simple(provider, pink, "Pink", Items.PINK_DYE)
                .eggColour(DyeColor.PINK.getFireworkColor())
                .parents(red, white)
                .build(consumer);
        simple(provider, purple, "Purple", Items.PURPLE_DYE)
                .eggColour(DyeColor.PURPLE.getFireworkColor())
                .parents(blue, red)
                .build(consumer);
        simple(provider, red, "Red", Items.RED_DYE)
                .eggColour(DyeColor.RED.getFireworkColor())
                .build(consumer);
        simple(provider, white, "White", Items.WHITE_DYE)
                .eggColour(DyeColor.WHITE.getFireworkColor())
                .build(consumer);
        simple(provider, yellow, "Yellow", Items.YELLOW_DYE)
                .eggColour(DyeColor.YELLOW.getFireworkColor())
                .build(consumer);

        // === Tier 2 === //
        simple(provider, string, "String", Items.STRING)
                .eggColour(0xffffff)
                .parents(black, log)
                .build(consumer);
        simple(provider, glowstone, "Glowstone", Items.GLOWSTONE)
                .eggColour(0xffff66)
                .parents(quartz, yellow)
                .build(consumer);
        simple(provider, gunpowder, "Gunpowder", Items.GUNPOWDER)
                .eggColour(0x999999)
                .parents(sand, flint)
                .build(consumer);
        simple(provider, redstone, "Redstone", Items.REDSTONE)
                .eggColour(0xe60000)
                .parents(red, sand)
                .build(consumer);
        simple(provider, lapis, "Lapis", Items.LAPIS_LAZULI)
                .eggColour(0x0000e6)
                .parents(blue, sand)
                .build(consumer);
        simple(provider, glass, "Glass", Items.GLASS)
                .eggColour(0xffffff)
                .parents(quartz, redstone)
                .build(consumer);
        simple(provider, iron, "Iron", Items.IRON_INGOT)
                .eggColour(0xffffcc)
                .parents(flint, white)
                .build(consumer);
        simple(provider, copper, "Copper", Items.COPPER_INGOT)
                .eggColour(0xB87333)
                .parents(flint, orange)
                .build(consumer);
        simple(provider, coal, "Coal", Items.COAL)
                .eggColour(0x262626)
                .parents(flint, log)
                .build(consumer);

        // === Tier 3 === //
        simple(provider, gold, "Gold", Items.GOLD_INGOT)
                .eggColour(0xcccc00)
                .parents(iron, yellow)
                .build(consumer);
        simple(provider, snowball, "Snowball", Items.SNOWBALL)
                .eggColour(0x33bbff)
                .parents(blue, log)
                .build(consumer);
        simple(provider, water, "Water", Fluids.WATER)
                .eggColour(0x000099)
                .parents(gunpowder, snowball)
                .build(consumer);
        simple(provider, lava, "Lava", Fluids.LAVA)
                .eggColour(0xcc3300)
                .parents(coal, quartz)
                .build(consumer);
        simple(provider, clay, "Clay", Items.CLAY)
                .eggColour(0xcccccc)
                .parents(snowball, sand)
                .build(consumer);
        simple(provider, leather, "Leather", Items.LEATHER)
                .eggColour(0xA7A06C)
                .parents(string, brown)
                .build(consumer);
        simple(provider, netherwart, "Netherwart", Items.NETHER_WART)
                .eggColour(0x800000)
                .parents(brown, glowstone)
                .build(consumer);

        // === Tier 4 === //
        simple(provider, diamond, "Diamond", Items.DIAMOND)
                .eggColour(0x99ccff)
                .parents(glass, gold)
                .build(consumer);
        simple(provider, blaze, "Blaze", Items.BLAZE_ROD)
                .eggColour(0xffff66)
                .parents(gold, lava)
                .build(consumer);
        simple(provider, slime, "Slime", Items.SLIME_BALL)
                .eggColour(0x009933)
                .parents(clay, green)
                .build(consumer);

        // === Tier 5 === //
        simple(provider, ender, "Ender", Items.ENDER_PEARL)
                .eggColour(0x001a00)
                .parents(diamond, netherwart)
                .build(consumer);
        simple(provider, ghast, "Ghast", Items.GHAST_TEAR)
                .eggColour(0xffffcc)
                .parents(white, blaze)
                .build(consumer);
        simple(provider, emerald, "Emerald", Items.EMERALD)
                .eggColour(0x00cc00)
                .parents(diamond, green)
                .build(consumer);
        simple(provider, magma, "Magma", Items.MAGMA_CREAM)
                .eggColour(0x1a0500)
                .parents(slime, blaze)
                .build(consumer);
        simple(provider, pshard, "Prismarine Shard", Items.PRISMARINE_SHARD)
                .eggColour(0x43806e)
                .parents(water, blue)
                .build(consumer);
        simple(provider, pcrystal, "Prismarine Crystal", Items.PRISMARINE_CRYSTALS)
                .eggColour(0x4e6961)
                .parents(water, emerald)
                .build(consumer);
        simple(provider, obsidian, "Obsidian", Items.OBSIDIAN)
                .eggColour(0x08080e)
                .parents(water, lava)
                .build(consumer);

        // === Tier 5 === //
        simple(provider, netherite, "Netherite", Items.NETHERITE_SCRAP)
                .eggColour(0x700000)
                .parents(obsidian, magenta)
                .build(consumer);

        //        simple(provider, smart, "Smart", Items.EGG)
//                .build(consumer);
    }


    @Override
    protected void configure(BiConsumer<Identifier, ChickenVariant> consumer, HolderLookup.Provider provider) {
        Map<Identifier, ChickenVariant> variantMap = new HashMap<>();
        addChickens((location, chickenVariant) -> {
            if (variantMap.containsKey(chickenVariant.id())) {
                throw new IllegalArgumentException("Duplicate chicken id " + chickenVariant.id());
            }
            variantMap.put(chickenVariant.id(), chickenVariant);
            consumer.accept(location, chickenVariant);
        }, provider);

        for (ChickenVariant value : variantMap.values()) {
            value.parent1().ifPresent(s -> {
                if (!variantMap.containsKey(s)) {
                    throw new IllegalArgumentException("Could not find parent id " + s + ", for chicken " + value.id());
                }
            });
            value.parent2().ifPresent(s -> {
                if (!variantMap.containsKey(s)) {
                    throw new IllegalArgumentException("Could not find parent id " + s + ", for chicken " + value.id());
                }
            });
        }
    }


    //TODO this is just a quick helper for the initial data gen. Once we start balancing things, this method will likely go away.
    private Builder simple(HolderLookup.Provider provider, Identifier id, String name, Item item) {
        return builder(provider, id, name)
                .itemProduct(item, 1, 1, 6000, 12000)
                .trait(ChickenTraits.SPEED, 0.25, 1, 0.1, 10, 1)
//                .trait(ChickenTraits.PRODUCTION, 0.25, 1, 0.1, 10, 1)
                .trait(ChickenTraits.MATURATION, 0.25, 1, 0.1, 10, 1)
                .trait(ChickenTraits.LIFESPAN, 0.25, 1, 0.1, 10, 1);
    }

    private Builder simple(HolderLookup.Provider provider, Identifier id, String name, Fluid fluid) {
        return builder(provider, id, name)
                .fluidProduct(fluid, 1000, 1000, 6000, 12000)//TODO, this is not going to work with fabric... Maybe I should use mb for this even on fabric?
                .trait(ChickenTraits.SPEED, 0.25, 1, 0.1, 10, 1)
//                .trait(ChickenTraits.PRODUCTION, 0.25, 1, 0.1, 10, 1)
                .trait(ChickenTraits.MATURATION, 0.25, 1, 0.1, 10, 1)
                .trait(ChickenTraits.LIFESPAN, 0.25, 1, 0.1, 10, 1);
    }

    @Override
    public String getName() {
        return "chickens:chicken-variants";
    }

    private static Builder builder(HolderLookup.Provider provider, Identifier id, String name) {
        return new Builder(provider, id, name);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        return super.run(writer);
    }

    private static class Builder {
        private final HolderLookup.Provider provider;
        private final Identifier id;
        private final String name;
        private Identifier texture;
        private ChickenProduct product = ChickenProduct.EMPTY;
        private int colour = 0xFFFFFFFF;
        private final List<TraitConfig> traits = new ArrayList<>();
        private Identifier parent1 = null;
        private Identifier parent2 = null;
        private ChickenSpawn spawn = null;
        private double inheritChance = 1;
        private double singleInheritChance = 0.25;

        public Builder(HolderLookup.Provider provider, Identifier id, String name) {
            this.provider = provider;
            this.id = id;
            this.name = name;
            this.texture = Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + "_chicken.png");
        }

        public Builder parents(Identifier parent1, Identifier parent2) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            return this;
        }

        public Builder texture(Identifier texture) {
            this.texture = texture;
            return this;
        }

        public Builder setInheritChance(double inheritChance, double singleInheritChance) {
            this.inheritChance = inheritChance;
            this.singleInheritChance = singleInheritChance;
            return this;
        }

        public Builder spawn(ChickenSpawn spawn) {
            this.spawn = spawn;
            return this;
        }

        public Builder spawn(List<TagKey<Biome>> biomes, int weight) {
            this.spawn = new ChickenSpawn(biomes, weight);
            return this;
        }

        public Builder itemProduct(Item item, int min, int max, int minLayTime, int maxLayTime) {
            this.product = new ChickenProduct(item.builtInRegistryHolder().key().location(), ChickenProduct.Type.ITEM, min, max, minLayTime, maxLayTime);
            return this;
        }

        public Builder fluidProduct(Fluid fluid, int min, int max, int minLayTime, int maxLayTime) {
            this.product = new ChickenProduct(fluid.builtInRegistryHolder().key().location(), ChickenProduct.Type.FLUID, min, max, minLayTime, maxLayTime);
            return this;
        }

        public Builder eggColour(int colour) {
            this.colour = colour;
            return this;
        }

        public Builder trait(Supplier<Trait> trait, double spawnMin, double spawnMax, double evoRate, double evoLimit, double evoExpo) {
            return trait(trait.get(), spawnMin, spawnMax, evoRate, evoLimit, evoExpo);
        }

        public Builder trait(Trait trait, double spawnMin, double spawnMax, double evoRate, double evoLimit, double evoExpo) {
            traits.forEach(e -> {
                if (e.trait() == trait) throw new IllegalArgumentException("Duplicate holder for chicken " + Builder.this.id + ", holder: " + id);
            });
            traits.add(new TraitConfig(trait, spawnMin, spawnMax, evoRate, evoLimit, evoExpo, inheritChance, singleInheritChance));
            return this;
        }

        public void build(BiConsumer<Identifier, ChickenVariant> consumer) {
            consumer.accept(id, new ChickenVariant(id, name, texture, product, colour, traits, Optional.ofNullable(parent1), Optional.ofNullable(parent2), Optional.ofNullable(spawn)));
        }
    }
}
