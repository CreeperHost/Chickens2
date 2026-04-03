package net.creeperhost.chickens.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Created by brandon3055 on 10/11/2025
 *
 * @param id           Internal chicken id
 * @param name         User readable variant name, e.g. Diamond or Coal
 * @param texture      Texture resource location
 * @param product      Product this chicken creates (Will need a product class that can handle items and fluids and stuff)
 * @param eggColour    Egg colour rgb
 * @param traitConfigs List of trait configs
 * @param parent1      First parent variant required to breed this chicken.
 * @param parent2      Second parent variant required to breed this chicken.
 */
public record ChickenVariant(ResourceLocation id, String name, ResourceLocation texture, ChickenProduct product, int eggColour, List<TraitConfig> traitConfigs, Optional<ResourceLocation> parent1, Optional<ResourceLocation> parent2, Optional<ChickenSpawn> spawn) {
    /**Used as a fallback ic a chicken variant is no longer available*/
    public static final ChickenVariant MISSING = new ChickenVariant(ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "_invalid_id_"), "[Invalid or unknown chicken ID]", ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "textures/entity/invalid_chicken.png"), new ChickenProduct(ResourceLocation.parse("minecraft:air"), ChickenProduct.Type.ITEM, 1, 1, 0, 0), 0xf800f8, Collections.emptyList(), Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<ChickenVariant> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ChickenVariant::id),
            Codec.STRING.fieldOf("name").forGetter(ChickenVariant::name),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ChickenVariant::texture),
            ChickenProduct.CODEC.fieldOf("product").forGetter(ChickenVariant::product),
            Codec.INT.fieldOf("eggColour").forGetter(ChickenVariant::eggColour),
            TraitConfig.CODEC.listOf().fieldOf("traitConfigs").forGetter(ChickenVariant::traitConfigs),
            ResourceLocation.CODEC.optionalFieldOf("parent1").forGetter(ChickenVariant::parent1),
            ResourceLocation.CODEC.optionalFieldOf("parent2").forGetter(ChickenVariant::parent2),
            ChickenSpawn.CODEC.optionalFieldOf("spawn").forGetter(ChickenVariant::spawn)
    ).apply(builder, ChickenVariant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChickenVariant> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ChickenVariant::id,
            ByteBufCodecs.STRING_UTF8, ChickenVariant::name,
            ResourceLocation.STREAM_CODEC, ChickenVariant::texture,
            ChickenProduct.STREAM_CODEC, ChickenVariant::product,
            ByteBufCodecs.INT, ChickenVariant::eggColour,
            TraitConfig.STREAM_CODEC.apply(ByteBufCodecs.list()), ChickenVariant::traitConfigs,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), ChickenVariant::parent1,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), ChickenVariant::parent2,
            ByteBufCodecs.optional(ChickenSpawn.STREAM_CODEC), ChickenVariant::spawn,
            ChickenVariant::new
    );

    public List<ResourceLocation> parents() {
        if (parent1().isPresent() && parent2().isPresent()) {
            return Lists.newArrayList(parent1().get(), parent2().get());
        }
        return Collections.emptyList();
    }

    public Map<Trait, TraitConfig> traitMap() {
        Map<Trait, TraitConfig> map = new HashMap<>();
        traitConfigs().forEach(e -> map.put(e.trait(), e));
        return map;
    }

    public record GUI(ResourceLocation id, String name) {
        public static final Codec<GUI> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(GUI::id),
                Codec.STRING.fieldOf("name").forGetter(GUI::name)
        ).apply(builder, GUI::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GUI> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, GUI::id,
                ByteBufCodecs.STRING_UTF8, GUI::name,
                GUI::new
        );
    }
}
