package net.creeperhost.chickens.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.init.ModComponentTypes;
import net.creeperhost.chickens.init.ModItems;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Replacement for ChickensRegistryItem, contains all the relevant data for an individual chicken.
 * <p>
 * Created by brandon3055 on 01/12/2025
 */
public record ChickenData(ChickenVariant variant, boolean isRooster, List<Trait.StateValue> traits, EntityData entityData) {

    public static final Codec<ChickenData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ChickenVariant.CODEC.fieldOf("variant").forGetter(ChickenData::variant),
            Codec.BOOL.fieldOf("isRooster").forGetter(ChickenData::isRooster),
            Trait.StateValue.CODEC.listOf().fieldOf("traits").forGetter(ChickenData::traits),
            EntityData.CODEC.fieldOf("entityData").forGetter(ChickenData::entityData)
    ).apply(builder, ChickenData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChickenData> STREAM_CODEC = StreamCodec.composite(
            ChickenVariant.STREAM_CODEC, ChickenData::variant,
            ByteBufCodecs.BOOL, ChickenData::isRooster,
            Trait.StateValue.STREAM_CODEC.apply(ByteBufCodecs.list()), ChickenData::traits,
            EntityData.STREAM_CODEC, ChickenData::entityData,
            ChickenData::new
    );

    public static ChickenData fromEntity(ChickensChicken chicken) {
        List<Trait.StateValue> traits = new ArrayList<>();
        chicken.getTraits().forEach((trait, value) -> traits.add(new Trait.StateValue(trait, value)));
        return new ChickenData(chicken.getChickenVariant(), chicken.isRooster(), traits, EntityData.fromChicken(chicken));
    }

    public void apply(ChickensChicken chicken) {
        chicken.setChickenVariant(variant);
        chicken.setRooster(isRooster);
        Map<Trait, Double> traitMap = new HashMap<>();
        traits.forEach(state -> traitMap.put(state.trait(), state.value()));
        chicken.setTraits(traitMap);
        entityData.apply(chicken);
    }

    @Nullable
    public static ChickenData fromItem(ItemStack stack) {
        return stack.get(ModComponentTypes.EGG_DATA.get());
    }

    public ItemStack toChickenEgg(boolean fertilized) {
        ItemStack stack = new ItemStack(ModItems.CHICKEN_EGG);
        stack.set(ModComponentTypes.EGG_DATA.get(), this);
        stack.set(ModComponentTypes.EGG_FERTILIZED.get(), fertilized);
        return stack;
    }

    public ItemStack toChickenItem() {
        ItemStack stack = new ItemStack(ModItems.CHICKEN_ITEM);
        stack.set(ModComponentTypes.EGG_DATA.get(), this);
        return stack;
    }

    public double getTraitValue(Trait trait, double fallback) {
        for (Trait.StateValue state : traits()) {
            if (state.trait() == trait) {
                return state.value();
            }
        }
        return fallback;
    }

    public double tamingModifier() {
        return entityData().tamingModifier();
    }

    /**
     * This is the main function that handles all breeding calculations.
     * Currently, this does not actually care about parent gender, that should be handled before we get to calling this method.
     */
    public static ChickenData fromParents(ChickenData chicken, ChickenData rooster, RandomSource random) {
        ChickenVariant chickenVariant = chicken.variant();
        ChickenVariant roosterVariant = rooster.variant();
        ChickenVariant childVariant = chicken.variant();

        //The logic for choosing the child variant is simple, both parent variants, and any potential combination variants are added to a list,
        //Then we just choose one at random, meaning each has an equal chance.
        //TODO, we may want to add some additional logic to this later, but not sure if we will use the old logic or something new.
        if (chickenVariant != roosterVariant) {
            List<ChickenVariant> potentials = new ArrayList<>();
            potentials.add(chickenVariant);
            potentials.add(roosterVariant);

            for (ChickenVariant potential : ChickenDataManager.getVariants()) {
                List<String> parents = potential.parents();
                if (parents.contains(chickenVariant.id()) && parents.contains(roosterVariant.id())) {
                    potentials.add(potential);
                }
            }
            childVariant = potentials.get(random.nextInt(potentials.size()));
        }

        Map<Trait, TraitConfig> childConfigs = chickenVariant.traitMap();

        Set<Trait> chickenTraits = new HashSet<>();
        chicken.traits().forEach(e -> chickenTraits.add(e.trait()));

        Set<Trait> roosterTraits = new HashSet<>();
        rooster.traits().forEach(e -> roosterTraits.add(e.trait()));

        Set<Trait> allTraits = new HashSet<>();
        allTraits.addAll(chickenTraits);
        allTraits.addAll(roosterTraits);

        //Taming modifier will be the mid-point between its parents. A modifier of zero is 'neutral' and will result in no trait evolution.
        double tamingModifier = chicken.tamingModifier() + ((rooster.tamingModifier() - chicken.tamingModifier()) * 0.5);

        List<Trait.StateValue> childTraits = new ArrayList<>();
        for (Trait trait : allTraits) {
            TraitConfig config = childConfigs.get(trait);
            if (config == null) {
                continue;
            }

            double chance = chickenTraits.contains(trait) && roosterTraits.contains(trait) ? config.inheritChance() : config.singleInheritChance();
            //TODO apply taming modifier offset.
            if (random.nextDouble() > chance) {
                continue;
            }
            double value1 = chicken.getTraitValue(trait, 0);
            double value2 = rooster.getTraitValue(trait, 0);
            //Random value somewhere between both the parent values.
            double result = value1 + ((value2 - value1) * random.nextDouble());

            //If taming modifier is neutral, there will be no evolution, if its negative, evolution will also be negative.
            double evoValue = config.evoRate() * random.nextDouble() * tamingModifier;

            if (evoValue > 0) {
                double eveDifficulty = Math.pow((result / config.evoLimit()), config.evoExpo());
                //Note, if trait value somehow ends up greater than evoLimit, this will be negative, but I don't really see a problem with that.
                double eveModifier = 1D - eveDifficulty;
                evoValue *= eveModifier;
            }

            result += evoValue;
            if (result <= 0) {
                continue;
            }
            childTraits.add(new Trait.StateValue(trait, result));
        }

        double growthA = chicken.getTraitValue(ChickenTraits.MATURATION.get(), 1); //All chickens should have the growth trait, but if somehow it does not, we will fall back to 1 because 1 results in the vanilla maturation rate.
        double growthB = rooster.getTraitValue(ChickenTraits.MATURATION.get(), 1);
        double growth = Math.max(0.1, growthA + ((growthB - growthA) * 0.5));

        //Taming modifier starts at zero, TODO, if baby chick spends time around adults, its taming modifier should slowly raise to the average of the adults around it.
        EntityData childEntity = new EntityData(0, (int) (-24000D / growth), 0);
        return new ChickenData(childVariant, random.nextBoolean(), childTraits, childEntity);
    }

    /**
     * Contains secondary entity data that needs to ke kept when converting a chicken to item form and back.
     */
    public record EntityData(int inLoveTime, int age, double tamingModifier) {
        public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.INT.fieldOf("inLoveTime").forGetter(EntityData::inLoveTime),
                Codec.INT.fieldOf("age").forGetter(EntityData::age),
                Codec.DOUBLE.fieldOf("tamingModifier").forGetter(EntityData::tamingModifier)
        ).apply(builder, EntityData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, EntityData::inLoveTime,
                ByteBufCodecs.INT, EntityData::age,
                ByteBufCodecs.DOUBLE, EntityData::tamingModifier,
                EntityData::new
        );

        public static EntityData fromChicken(ChickensChicken chicken) {
            return new EntityData(chicken.getInLoveTime(), chicken.getAge(), chicken.getTamingModifier());
        }

        public void apply(ChickensChicken chicken) {
            chicken.setInLoveTime(inLoveTime());
            chicken.setAge(age());
            chicken.setTamingModifier(tamingModifier());
        }
    }
}
