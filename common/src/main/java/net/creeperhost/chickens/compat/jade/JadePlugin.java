package net.creeperhost.chickens.compat.jade;

import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.trait.Trait;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadePlugin implements IWailaPlugin
{
    private static final ResourceLocation ENTITY_DATA_ID = ResourceLocation.fromNamespaceAndPath(Chickens.MOD_ID, "entity_data");

    @Override
    public void register(IWailaCommonRegistration registration)
    {
        registration.registerEntityDataProvider(new IServerDataProvider<>()
        {
            @Override
            public void appendServerData(CompoundTag tag, EntityAccessor accessor)
            {
                if(accessor.getEntity() instanceof ChickensChicken chicken)
                {
                    tag.putDouble("tame", chicken.getTamingModifier());
                    tag.putFloat("life", chicken.getLifeSpan());
                    CompoundTag traits = new CompoundTag();
                    chicken.getTraits().forEach((trait, value) -> {
                        ResourceLocation key = Chickens.TRAIT_REGISTRY.getKey(trait);
                        if (key != null) {
                            traits.putDouble(key.toString(), value);
                        }
                    });
                    tag.put("traits", traits);
                }
            }

            @Override
            public ResourceLocation getUid()
            {
                return ENTITY_DATA_ID;
            }
        }, ChickensChicken.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(new IEntityComponentProvider() {
            @Override
            public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig iPluginConfig) {
                CompoundTag traits = accessor.getServerData().getCompoundOrEmpty("traits");
                for (String traitName : traits.keySet()) {
                    Trait trait = Chickens.TRAIT_REGISTRY.getValue(ResourceLocation.parse(traitName));
                    if (trait != null) {
                        tooltip.add(Component.translatable(trait.getDescriptionId()).append(": ").append(Component.literal(String.format("%.3f", traits.getDoubleOr(traitName, 0))).withStyle(ChatFormatting.WHITE)));
                    }
                }
                tooltip.add(Component.literal("Taming Modifier: ").append(Component.literal(String.format("%.3f", accessor.getServerData().getDoubleOr("tame", 0))).withStyle(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("Remaining Life: ").append(Component.literal(String.format("%.1f%%", accessor.getServerData().getFloatOr("life", 0))).withStyle(ChatFormatting.WHITE)));
            }

            @Override
            public ResourceLocation getUid() {
                return ENTITY_DATA_ID;
            }
        }, ChickensChicken.class);
    }
}
