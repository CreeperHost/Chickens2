package net.creeperhost.chickens.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Created by brandon3055 on 10/11/2025
 *
 * @param trait               The internal trait ID that this config is referring to.
 * @param spawnMin            The minimum trait value for natural spawned chickens.
 * @param spawnMax            The maximum trait value for natural spawned chickens,
 *                            The actual value will be a random value between min and max.
 *                            If the resulting random value is less or equal to zero, then the trait will not be applied.
 *                            Meaning its possible to have the trait be randomly applied or not by setting spawnMin less than zero.
 *                            If min and max are both zero, then chickens will never spawn with this trait.
 * @param evoRate             Controls the rate that this trait will increase with breeding,
 *                            This sets the initial value for the trait evolution calculation, or more specifically, it is a random value between zero and evoRate.
 *                            That is then effected by the timing modifier of the parents, and the evoLimit / evoExpo calculations before it gets added onto the final child trait value.
 * @param evoLimit            "Theoretical" trait value limit, though it won't actually be possible to reach this value.
 * @param evoExpo             The closer a trait value is to its evoLimit, the harder it is to progress further. Meaning its likely impossible to actually ever get to the 'evoLimit' due to diminishing returns.
 *                            This value controls the difficulty curve. A value of 1 will result in a linear difficulty increase from 0 to evoLimit
 *                            A value greater than 1 will reduce the difficulty early on, but the difficulty will increase exponentially the closer you get to 'evoLimit', the further you increase this value, the further you will push back that exponential curve.
 *                            A value less than 1 will cause the opposite effect, the difficulty will increase rapidly early on, before tapering off.
 *                            The equation for evolution difficulty is: (traitValue / evoLimit) ^ evoExponent (Once the result of this equation reaches 1, no further trait progression is possible)
 *                            Example difficulty curves <a href="https://ss.brandon3055.com/0f888.png">value: 1</a>, <a href="https://ss.brandon3055.com/fb81a.png">value: 5</a>, <a href="https://ss.brandon3055.com/78c39.png">value: 0.5</a>, <a href="https://ss.brandon3055.com/63952.png">value: 0.1</a>
 * @param inheritChance       Sets the chance this trait will be passed onto the child if both parents have this trait. 0 = 0% chance, 1 = 100% chance.
 * @param singleInheritChance Sets the chance this trait will be passed onto the child if only one parent has this trait. 0 = 0% chance, 1 = 100% chance.
 */
public record TraitConfig(Trait trait, double spawnMin, double spawnMax, double evoRate, double evoLimit, double evoExpo, double inheritChance, double singleInheritChance) {

    public static final Codec<TraitConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Chickens.TRAIT_REGISTRY.byNameCodec().fieldOf("trait").forGetter(TraitConfig::trait),
            Codec.DOUBLE.fieldOf("spawnMin").forGetter(TraitConfig::spawnMin),
            Codec.DOUBLE.fieldOf("spawnMax").forGetter(TraitConfig::spawnMax),
            Codec.DOUBLE.fieldOf("evoRate").forGetter(TraitConfig::evoRate),
            Codec.DOUBLE.fieldOf("evoLimit").forGetter(TraitConfig::evoLimit),
            Codec.DOUBLE.fieldOf("evoExpo").forGetter(TraitConfig::evoExpo),
            Codec.DOUBLE.fieldOf("inheritChance").forGetter(TraitConfig::inheritChance),
            Codec.DOUBLE.fieldOf("singleInheritChance").forGetter(TraitConfig::singleInheritChance)
    ).apply(builder, TraitConfig::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TraitConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Chickens.TRAIT_KEY), TraitConfig::trait,
            ByteBufCodecs.DOUBLE, TraitConfig::spawnMin,
            ByteBufCodecs.DOUBLE, TraitConfig::spawnMax,
            ByteBufCodecs.DOUBLE, TraitConfig::evoRate,
            ByteBufCodecs.DOUBLE, TraitConfig::evoLimit,
            ByteBufCodecs.DOUBLE, TraitConfig::evoExpo,
            ByteBufCodecs.DOUBLE, TraitConfig::inheritChance,
            ByteBufCodecs.DOUBLE, TraitConfig::singleInheritChance,
            TraitConfig::new
    );
}
