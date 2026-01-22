package net.creeperhost.chickens.entity;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.ChickensPlatform;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.data.*;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.init.ModEntities;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by brandon3055 on 17/11/2025
 */
public class ChickensChicken extends Chicken {

    private static final EntityDataAccessor<ResourceLocation> CHICKEN_VARIANT = SynchedEntityData.defineId(ChickensChicken.class, ChickensPlatform.getResourceSerializer());
    private static final EntityDataAccessor<Boolean> IS_ROOSTER = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Map<Trait, Double>> TRAITS = SynchedEntityData.defineId(ChickensChicken.class, ChickensPlatform.getTraitSerializer());

    private static final EntityDataAccessor<Float> TAMING_MODIFIER = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LIFESPAN = SynchedEntityData.defineId(ChickensChicken.class, EntityDataSerializers.FLOAT);

    private UUID hostilePlayer = null;
    private int playerTime = 0;
    private int tick = 0;

    public ChickensChicken(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new ChickenAvoidEntityGoal<>(this, Player.class, 16.0F, 0.8, 1.33));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHICKEN_VARIANT, ChickenVariant.MISSING.id());//TODO, would like to pick a random naturally spawned chicken, but need to figure out spawning first.
        builder.define(IS_ROOSTER, false);
        builder.define(TAMING_MODIFIER, 0F);
        builder.define(LIFESPAN, (float) Config.INSTANCE.chickenLifeSpan);
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

    public static boolean checkSpawnRules(EntityType<? extends Animal> entityType, LevelAccessor levelAccessor, EntitySpawnReason spawnReason, BlockPos blockPos, RandomSource randomSource) {
        if (!ChickenDataManager.isValidSpawnBiome(levelAccessor.getBiome(blockPos))) {
            return false;
        }
        BlockPos spawnOn = blockPos.below();
        return levelAccessor.getBlockState(spawnOn).isFaceSturdy(levelAccessor, spawnOn, Direction.UP);
    }

    public void setRooster(boolean isRooster) {
        this.entityData.set(IS_ROOSTER, isRooster);
    }

    public boolean isRooster() {
        return this.entityData.get(IS_ROOSTER);
    }

    public float getLifeSpan()
    {
        return entityData.get(LIFESPAN);
    }

    public void setLifeSpan(float lifeSpan)
    {
        entityData.set(LIFESPAN, lifeSpan);
    }

    public void setChickenVariant(ChickenVariant variant) {
        this.entityData.set(CHICKEN_VARIANT, variant.id());
    }

    public ChickenVariant getChickenVariant() {
        return ChickenDataManager.getVariantOrMissing(this.entityData.get(CHICKEN_VARIANT));
    }

    public void setChickenVariant(ResourceLocation id) {
        this.entityData.set(CHICKEN_VARIANT, id);
    }

    public ResourceLocation getVariantID() {
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
            initRandomTraits(level, variant, 0);
        }
        setRooster(level.getRandom().nextBoolean());
        return super.finalizeSpawn(level, difficultyInstance, entitySpawnReason, spawnGroupData);
    }

    public void initRandomTraits(ServerLevelAccessor level, ChickenVariant variant, double forceValue) {
        for (TraitConfig config : variant.traitConfigs()) {
            double value = config.spawnMin() + ((config.spawnMax() - config.spawnMin()) * level.getRandom().nextDouble());
            if (forceValue > 0) value = forceValue;
            if (value <= 0) continue;
            setTrait(config.trait(), value);
        }
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
        output.store("chicken_variant", ResourceLocation.CODEC, getVariantID());
        output.putBoolean("is_rooster", isRooster());
        output.putDouble("taming_mod", getTamingModifier());
        output.putFloat("lifespan", getLifeSpan());

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
        setChickenVariant(input.read("chicken_variant", ResourceLocation.CODEC).orElse(ChickenVariant.MISSING.id()));
        setRooster(input.getBooleanOr("is_rooster", false));
        setTamingModifier(input.getDoubleOr("taming_mod", 0));
        setLifeSpan(input.getFloatOr("lifespan", (float) Config.INSTANCE.chickenLifeSpan));

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

        if (tick++ % 20 == 0) {
            //Handle taming modifier inheritance from nearby chickens
            if (isBaby()) {
                List<ChickensChicken> chickens = serverLevel.getNearbyEntities(ChickensChicken.class, TargetingConditions.forNonCombat(), this, getBoundingBox().inflate(Config.INSTANCE.tamingInheritanceRange));
                double averageMod = chickens.stream()
                        .filter(chicken -> !chicken.isBaby())
                        .mapToDouble(ChickensChicken::getTamingModifier)
                        .average()
                        .orElse(0);
                averageMod *= Config.INSTANCE.tamingInheritanceLimit;
                double mod = getTamingModifier();
                double diff = averageMod - mod;
                setTamingModifier(mod + (diff * Config.INSTANCE.tamingInheritanceRate));
            } else {
                List<Player> players = serverLevel.getNearbyPlayers(TargetingConditions.forNonCombat(), this, getBoundingBox().inflate(Config.INSTANCE.passiveTamingRange));
                //Handle taming reduction due to nearby hostile players.
                if (hostilePlayer != null) {
                    if (playerTime-- <= 0) {
                        hostilePlayer = null;
                    } else {
                        if (players.stream().anyMatch(player -> player.getUUID().equals(hostilePlayer))) {
                            addTamingModifier(-0.1);
                        }
                    }
                //Handle passive taming.
                } else if (!players.isEmpty()) {
                    playerTime += players.size();
                    if (playerTime >= Config.INSTANCE.passiveTamingTime) {
                        playerTime = 0;
                        addTamingModifier(Config.INSTANCE.passiveTamingAmount);
                    }
                } else if (playerTime > 0) {
                    playerTime--;
                }
            }
        }

        ChickenVariant variant = getChickenVariant();
        ChickenProduct product = variant.product();
        if (isRooster() || ChickenVariant.MISSING.equals(variant) || product == ChickenProduct.EMPTY) {
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

            double mod = data.getTraitValue(ChickenTraits.LIFESPAN.get(), 1);
            setLifeSpan(getLifeSpan() - (float) (Config.INSTANCE.lifespanReductionOnLay / mod));
        }
    }

    //TODO Vanilla Chicken Support?
    @Override
    public boolean canMate(Animal animal) {
        if (!(animal instanceof ChickensChicken other) || other.isRooster() == isRooster()) {
            return false;
        }
        return super.canMate(animal);
    }

    @Override
    public @Nullable Chicken getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        if (!(ageableMob instanceof ChickensChicken other)) {
            return null;
        }
        ChickenData thisData = ChickenData.fromEntity(this);
        ChickenData otherData = ChickenData.fromEntity(other);
        ChickenData childData = ChickenData.fromParents(thisData, otherData, serverLevel.random);

        ChickensChicken chicken = ModEntities.CHICKEN.get().create(serverLevel, EntitySpawnReason.BREEDING);
        if (chicken instanceof ChickensChicken child) {
            childData.apply(child);
        }

        return chicken;
    }

    @Override
    public void setInLove(@Nullable Player player) {
        super.setInLove(player);
        if (player != null) {
            if (hostilePlayer != null && player.getUUID().equals(hostilePlayer)) {
                hostilePlayer = null;
                playerTime = 0;
            } else {
                addTamingModifier(0.5);
            }
        }
    }

    //Taming modifier uses a similar scaling system to trait evolution. The greater the absolute value, the less the value is effected.
    //The limit and expo values are configurable via mod config.
    public void addTamingModifier(double amount) {
        double modifier = getTamingModifier();
        double scale = 1D - Math.pow(Math.abs(modifier) / Config.INSTANCE.tamingModifierLimit, Config.INSTANCE.tamingModifierExpo);
        setTamingModifier(modifier + (amount * scale));
    }

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource source, float damage) {
        if (source.getEntity() instanceof Player player) {
            hostilePlayer = player.getUUID();
            playerTime = Config.INSTANCE.playerHostileTime;
            if (damage < 0.5) {
                addTamingModifier(-0.5);
            } else {
                addTamingModifier(-damage);
            }
        }
        super.actuallyHurt(level, source, damage);
    }

    private static class ChickenAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final ChickensChicken chicken;

        public ChickenAvoidEntityGoal(ChickensChicken chicken, Class<T> targetClass, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(chicken, targetClass, maxDist, walkSpeedModifier, sprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.chicken = chicken;
        }

        public boolean canUse() {
            return chicken.getTamingModifier() < 0 && super.canUse();
        }

        public boolean canContinueToUse() {
            return chicken.getTamingModifier() < 0 && super.canContinueToUse();
        }
    }
}
