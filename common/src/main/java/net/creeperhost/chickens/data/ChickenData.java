package net.creeperhost.chickens.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModComponentTypes;
import net.creeperhost.chickens.init.ModItems;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replacement for ChickensRegistryItem, contains all the relevant data for an individual chicken.
 * <p>
 * Created by brandon3055 on 01/12/2025
 */
public record ChickenData(ChickenVariant variant, boolean isRooster, List<Trait.State> traits, EntityData entityData) {

    public static final Codec<ChickenData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ChickenVariant.CODEC.fieldOf("variant").forGetter(ChickenData::variant),
            Codec.BOOL.fieldOf("isRooster").forGetter(ChickenData::isRooster),
            Trait.State.CODEC.listOf().fieldOf("traits").forGetter(ChickenData::traits),
            EntityData.CODEC.fieldOf("entityData").forGetter(ChickenData::entityData)
    ).apply(builder, ChickenData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChickenData> STREAM_CODEC = StreamCodec.composite(
            ChickenVariant.STREAM_CODEC, ChickenData::variant,
            ByteBufCodecs.BOOL, ChickenData::isRooster,
            Trait.State.STREAM_CODEC.apply(ByteBufCodecs.list()), ChickenData::traits,
            EntityData.STREAM_CODEC, ChickenData::entityData,
            ChickenData::new
    );

    public static ChickenData fromEntity(ChickensChicken chicken) {
        List<Trait.State> traits = new ArrayList<>();
        chicken.getTraits().forEach((location, value) -> traits.add(new Trait.State(location, value)));
        return new ChickenData(chicken.getChickenVariant(), chicken.isRooster(), traits, EntityData.fromChicken(chicken));
    }

    public void apply(ChickensChicken chicken) {
        chicken.setChickenVariant(variant);
        chicken.setRooster(isRooster);
        Map<ResourceLocation, Double> traitMap = new HashMap<>();
        traits.forEach(state -> traitMap.put(state.id(), state.value()));
        chicken.setTraits(traitMap);
        entityData.apply(chicken);
    }

    @Nullable
    public static ChickenData fromItem(ItemStack stack) {
        return stack.get(ModComponentTypes.EGG_DATA.get());
    }

    public ItemStack toChickenEgg() {
        ItemStack stack = new ItemStack(ModItems.CHICKEN_EGG);
        stack.set(ModComponentTypes.EGG_DATA.get(), this);
        return stack;
    }

    public ItemStack toChickenItem() {
        ItemStack stack = new ItemStack(ModItems.CHICKEN_ITEM);
        stack.set(ModComponentTypes.EGG_DATA.get(), this);
        return stack;
    }

    /**
     * Contains secondary entity data that needs to ke kept when converting a chicken to item form and back.
     */
    public record EntityData(int inLoveTime, int age) {
        public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.INT.fieldOf("inLoveTime").forGetter(EntityData::inLoveTime),
                Codec.INT.fieldOf("age").forGetter(EntityData::age)
        ).apply(builder, EntityData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, EntityData::inLoveTime,
                ByteBufCodecs.INT, EntityData::age,
                EntityData::new
        );

        public static EntityData fromChicken(ChickensChicken chicken) {
            return new EntityData(chicken.getInLoveTime(), chicken.getAge());
        }

        public void apply(ChickensChicken chicken) {
            chicken.setInLoveTime(inLoveTime);
            chicken.setAge(age);
        }
    }
}
