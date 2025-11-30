package net.creeperhost.chickens.entity;

import net.creeperhost.chickens.ChickensPlatform;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 17/11/2025
 */
public class ChickensChicken extends Chicken {

    private static final EntityDataAccessor<String> CHICKEN_VARIANT = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> IS_ROOSTER = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Map<ResourceLocation, Double>> TRAITS = SynchedEntityData.defineId(ChickensChicken.class, ChickensPlatform.getTraitSerializer());

    public ChickensChicken(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHICKEN_VARIANT, ChickenVariant.MISSING.id());//TODO, would like to pick a random naturally spawned chicken, but need to figure out spawning first.
        builder.define(IS_ROOSTER, false);
        builder.define(TRAITS, new HashMap<>());
    }

    @Override
    public void aiStep() {
        if (isRooster()) eggTime = 2;
        super.aiStep();
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

    public Map<ResourceLocation, Double> getTraits() {
        return this.entityData.get(TRAITS);
    }

    public void setTraits(Map<ResourceLocation, Double> traits) {
        this.entityData.set(TRAITS, traits);
    }

    public boolean hasTrait(Trait trait) {
        return getTraits().containsKey(trait.getId());
    }

    public double getTraitValue(Trait trait) {
        return getTraits().getOrDefault(trait.getId(), -1D);
    }

    public void setTrait(Trait trait, double value) {
        Map<ResourceLocation, Double> traits = getTraits();
        traits.put(trait.getId(), value);
        setTraits(traits);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, @Nullable SpawnGroupData spawnGroupData) {
        //TODO Set spawn variant based on biome/dimension etc.
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, entitySpawnReason, spawnGroupData);
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
        getTraits().forEach((name, value) -> {
            ValueOutput entry = traits.addChild();
            entry.putString("name", name.toString());
            entry.putDouble("value", value);
        });
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setVariantString(input.getStringOr("chicken_variant", ChickenVariant.MISSING.id()));
        setRooster(input.getBooleanOr("is_rooster", false));

        Map<ResourceLocation, Double> traitMap = new HashMap<>();
        ValueInput.ValueInputList traits = input.childrenListOrEmpty("traits");
        traits.forEach(entry -> {
            entry.getString("name").ifPresent(s -> {
                ResourceLocation name = ResourceLocation.parse(s);
                double value = entry.getDoubleOr("value", 0);
                traitMap.put(name, value);
            });
        });
        setTraits(traitMap);
    }
}
