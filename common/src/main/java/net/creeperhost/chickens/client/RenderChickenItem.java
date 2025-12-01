package net.creeperhost.chickens.client;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.creeperhost.chickens.api.ChickensRegistry;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Collections;

public class RenderChickenItem {
    public static void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return;
        }

        ChickenData data = ChickenData.fromItem(itemStack);
        if (data == null) {
            ChickenVariant variant = Iterables.get(ChickenDataManager.getVariants(), (int) ((System.currentTimeMillis() / 1000) % ChickensRegistry.getItems().size()));
            data = new ChickenData(variant, false, Collections.emptyList(), new ChickenData.EntityData(0, 0));
        }

        ChickensChicken chicken = ModEntities.CHICKEN.get().create(mc.level, EntitySpawnReason.SPAWNER);
        if (chicken == null) return;
        data.apply(chicken);

        //Force the head rot in position to stop it bouncing
        chicken.yHeadRot = 0;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.mulPose(Axis.XP.rotationDegrees(10));
        poseStack.mulPose(Axis.ZP.rotationDegrees(10));
        EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
//                if (transformType == ItemDisplayContext.GUI)
//                {
//                    Lighting.setupForFlatItems();
//                }
//                entityRenderDispatcher.getRenderer(chicken).render(chicken, 0, 0, poseStack, bufferSource, combinedOverlay);
        entityRenderDispatcher.render(chicken, 0.0, 0.0, 0.0, mc.getDeltaTracker().getGameTimeDeltaPartialTick(false), poseStack, bufferSource, combinedLight);

        poseStack.popPose();
    }
}
