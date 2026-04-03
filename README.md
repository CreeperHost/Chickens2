# Chickens2

## Chicken Variants

All the previous chicken variants still exist, though there is now a Hen and Rooster for each varient.

Chicken variants are now defined via datapack data.  
For example, the following is the flint variant.  

```json
{
  "eggColour": 7039815,
  "id": "chickens:flint",
  "name": "Flint",
  "product": {
    "type": "ITEM",
    "id": "minecraft:flint",
    "max": 1,
    "maxLayTime": 12000,
    "min": 1,
    "minLayTime": 6000
  },
  "spawn": {
    "biomes": [
      "minecraft:is_mountain",
      "c:is_stony_shores",
      "c:is_mountain"
    ],
    "weight": 10
  },
  "texture": "chickens:textures/entity/flint_chicken.png",
  "traitConfigs": [
    {
      "evoExpo": 1.0,
      "evoLimit": 10.0,
      "evoRate": 0.1,
      "inheritChance": 1.0,
      "singleInheritChance": 0.25,
      "spawnMax": 1.0,
      "spawnMin": 0.25,
      "trait": "chickens:speed"
    },
    {
      "evoExpo": 1.0,
      "evoLimit": 10.0,
      "evoRate": 0.1,
      "inheritChance": 1.0,
      "singleInheritChance": 0.25,
      "spawnMax": 1.0,
      "spawnMin": 0.25,
      "trait": "chickens:maturation"
    },
    {
      "evoExpo": 1.0,
      "evoLimit": 10.0,
      "evoRate": 0.1,
      "inheritChance": 1.0,
      "singleInheritChance": 0.25,
      "spawnMax": 1.0,
      "spawnMin": 0.25,
      "trait": "chickens:lifespan"
    }
  ]
}
```

## Traits
There are currently only three implemented traits, speed, maturation and lifespan, though new traits can now be added relatively easily depending on the desired effect of the trait.
When a chicken varient is defined via the datapack, all possible traits for that varient need to be defined. Only traits defines within the varient definition can be applied to that particular chicken varient.

When defining a trait configuration on a chicken varient, there are 7 important parameters.
- `evoLimit` This is used to limit the maximum trait value, but the way this system works is a little complicated. 
As a traits value gets closer to this limit, it gets exponentially harder to further increase the trait value.
Meaning, it would be near impossible to actually reach this value, though it should be possible to get close. 
- `evoExpo` The closer a trait value is to its evoLimit, the harder it is to progress further. Meaning its likely impossible to actually ever get to the 'evoLimit' due to diminishing returns.
This value controls the difficulty curve. A value of 1 will result in a linear difficulty increase from 0 to evoLimit
A value greater than 1 will reduce the difficulty early on, but the difficulty will increase exponentially the closer you get to 'evoLimit', the further you increase this value, the further you will push back that exponential curve.
A value less than 1 will cause the opposite effect, the difficulty will increase rapidly early on, before tapering off.
- `evoRate` Controls the rate that this trait will increase with breeding, (Also effected by breeding modifier)
- `inheritChance` Sets the chance this trait will be passed onto the child if both parents have this trait. 0 = 0% chance, 1 = 100% chance.
- `singleInheritChance` Sets the chance this trait will be passed onto the child if only one parent has this trait. 0 = 0% chance, 1 = 100% chance.
- `spawnMax` The maximum trait value for natural spawned chickens.
- `spawnMin` The minimum trait value for natural spawned chickens. If min and max are both zero, the chicken will spawn without the trait. 

### Implemented Traits
- Speed, Effects the egg laying speed.
- Maturation, Effects how long it takes for chicks to mature. 
- Lifespan, Effects the lifespan of a chicken.

All of these current traits act as multipliers. meaning a value of 2 would double the lay speed / maturation speed / lifespan.
Values less than 1 are also possible and have the effect you would expect from a multiplier value less than 1.

## Breeding
Every chicken now has a "Taming Modifier" which has a significant impact when attempting to increase trait values.  
There are a number of ways this modifier can be increased, but it mostly comes down to spending time with and interacting with the chickens. 
Simply spending time in proximity to chickens will slowly increase the taming modifier, but it can also be increased by feeding them.
Though spamming food will not have any effect. 
There are also diminishing returns, meaning the higher the taming modifier goes, the harder it gets to increase it further. 

Attacking a chicken will significantly decrease the taming modifier, and the player who attacked the chicken will be perceived as hostile for a while after the attack. As long as a hostile player is near the chicken, the taming modifier will continue to decrease. 

Baby chickens can partially inherit their taming modifier form nearby chickens. When a baby chicken spends time around adults, its modifier will slowly increase to a certain percentage of the average adult taming modifier. The default is 80% of the average value, but pretty much everything is configurable.

When breeding chickens, the taming modifier can be though of as a multiplier that gets applied to the trait mutation. Meaning if the modifier is zero, traits will never increase, and a negative multiplier will result in traits regressing. 

## Eggs
There have been some slight changes to the way eggs work.
Eggs laid without a rooster are non-fertile and can not be hatched. 
But fertility has no effect on resource production. 
This change was made to bring thing more in line with real life. 
When making an omelet, it does not matter if the eggs are fertile or not. 

But when hatching a fertile egg using the incubator, the logic is pretty much unchanged from the previous version, and its still possible for eggs to become non-viable if not incubated properly. 
A non-viable egg is effectively trash as it can not hatch, and can no longer produce its resource.  

## Machines
Most machines works similar to the previous Chickens, though there are a couple changes.

The breeder now functions a nothing more than a block that exactly replicates how chickens function as entities.  
Meaning you no longer need two hens in order to lay egs. and a rooster is only required to create fertilized eggs. 

The incubator can now accept chicks and grown them into adults.

The Ovoscope has been renames to the "Sorter" and as the name suggests it now has the ability to sort both eggs and chickens based on varient, trait values and things like viability and fertility. 

## Commands
`summon_chicken <variant> [rooster] [trait values] [taming modifier]`
This is a simple command that allows you to summon chickens for testing purposes.  
There is currently no way to specify individual trait values, the trait parameter applies to all traits. 