package net.creeperhost.chickens.entity;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.ChickensPlatform;
import net.creeperhost.chickens.data.*;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 17/11/2025
 */
public class ChickensChicken extends Chicken {

    private static final EntityDataAccessor<String> CHICKEN_VARIANT = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_ROOSTER = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Map<Trait, Double>> TRAITS = SynchedEntityData.defineId(ChickensChicken.class, ChickensPlatform.getTraitSerializer());
    private static final EntityDataAccessor<Float> TAMING_MODIFIER = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.FLOAT);

    public ChickensChicken(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHICKEN_VARIANT, ChickenVariant.MISSING.id());//TODO, would like to pick a random naturally spawned chicken, but need to figure out spawning first.
        builder.define(IS_ROOSTER, false);
        builder.define(TAMING_MODIFIER, 0F);
        builder.define(TRAITS, new HashMap<>());
    }

    @Override
    protected Component getTypeName() {
        ChickenVariant variant = getChickenVariant();
        if (isRooster()) {
            return Component.translatable("entity.chickens.rooster.name", variant.name());
        } else {
            return Component.translatable("entity.chickens.chicken.name", variant.name());
        }
    }

    public void setRooster(boolean isRooster) {
        this.entityData.set(IS_ROOSTER, isRooster);
    }

    public boolean isRooster() {
        return this.entityData.get(IS_ROOSTER);
    }

    public void setChickenVariant(ChickenVariant variant) {
        this.entityData.set(CHICKEN_VARIANT, variant.id());
    }

    public ChickenVariant getChickenVariant() {
        return ChickenDataManager.getVariantOrMissing(this.entityData.get(CHICKEN_VARIANT));
    }

    public void setVariantString(String variantId) {
        this.entityData.set(CHICKEN_VARIANT, variantId);
    }

    public String getVariantString() {
        return this.entityData.get(CHICKEN_VARIANT);
    }

    public Map<Trait, Double> getTraits() {
        return this.entityData.get(TRAITS);
    }

    public void setTraits(Map<Trait, Double> traits) {
        this.entityData.set(TRAITS, traits);
    }

    public boolean hasTrait(Trait trait) {
        return getTraits().containsKey(trait);
    }

    public double getTraitValue(Trait trait) {
        return getTraits().getOrDefault(trait, -1D);
    }

    public void setTrait(Trait trait, double value) {
        Map<Trait, Double> traits = getTraits();
        traits.put(trait, value);
        setTraits(traits);
    }

    public void setTamingModifier(double tamingModifier) {
        this.entityData.set(TAMING_MODIFIER, (float) tamingModifier);
    }

    public double getTamingModifier() {
        return this.entityData.get(TAMING_MODIFIER);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        ChickenVariant variant = ChickenDataManager.getVariantForSpawn(level, blockPosition());
        if (variant == null) {
            remove(RemovalReason.DISCARDED);
            Chickens.LOGGER.warn("No valid chicken variant found for spawn biome {}, chicken will be discarded.", level.getBiome(blockPosition()));
        } else {
            setChickenVariant(variant);
            for (TraitConfig config : variant.traitConfigs()) {
                double value = config.spawnMin() + ((config.spawnMax() - config.spawnMin()) * level.getRandom().nextDouble());
                if (value <= 0) continue;
                setTrait(config.trait(), value);
            }
        }
        setRooster(level.getRandom().nextBoolean());
        return super.finalizeSpawn(level, difficultyInstance, entitySpawnReason, spawnGroupData);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason spawnReason) {
        if (!levelAccessor.getBiome(getOnPos()).is(BiomeTags.IS_OVERWORLD)) {
            return true; //Allows spawning in the nether
        }
        return super.checkSpawnRules(levelAccessor, spawnReason);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("chicken_variant", getVariantString());
        output.putBoolean("is_rooster", isRooster());

        ValueOutput.ValueOutputList traits = output.childrenList("traits");
        getTraits().forEach((trait, value) -> {
            ValueOutput entry = traits.addChild();
            entry.store("trait", Chickens.TRAIT_REGISTRY.byNameCodec(), trait);
            entry.putDouble("value", value);
        });
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setVariantString(input.getStringOr("chicken_variant", ChickenVariant.MISSING.id()));
        setRooster(input.getBooleanOr("is_rooster", false));

        Map<Trait, Double> traitMap = new HashMap<>();
        ValueInput.ValueInputList traits = input.childrenListOrEmpty("traits");
        traits.forEach(entry -> {
            entry.read("trait", Chickens.TRAIT_REGISTRY.byNameCodec())
                    .ifPresent(trait -> {
                        double value = entry.getDoubleOr("value", 0);
                        traitMap.put(trait, value);
                    });
        });
        setTraits(traitMap);
    }

    //=== Custom Chicken Logic ===//

    @Override
    public void aiStep() {
        super.aiStep(); //Default egg logic is disabled via mixin

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ChickenVariant variant = getChickenVariant();
        ChickenProduct product = variant.product();
        if (isRooster() || variant == ChickenVariant.MISSING || product == ChickenProduct.EMPTY) {
            eggTime = 0;
        } else if (isAlive() && !isBaby() && !isChickenJockey() && eggTime-- <= 0) {
            //All we do here is drop an egg with all of this chicken's data. The rest can be figured out when the egg is used in one way or another.
            ChickenData data = ChickenData.fromEntity(this);
            ItemStack stack = data.toChickenEgg(false);
            spawnAtLocation(serverLevel, stack);
            playSound(SoundEvents.CHICKEN_EGG, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            gameEvent(GameEvent.ENTITY_PLACE);

            eggTime = product.minLayTime() + random.nextInt(Math.max(product.maxLayTime() - product.minLayTime(), 1));
            getTraits().forEach((trait, value) -> {
                eggTime = (int) (eggTime * trait.getLaySpeedModifier(value));
            });
        }
    }


}
