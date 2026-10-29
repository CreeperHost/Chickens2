package net.creeperhost.chickens.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.creeperhost.chickens.blockentities.IncubatorBlockEntity;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.item.ItemChickenEgg;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RenderIncubator implements BlockEntityRenderer<IncubatorBlockEntity, RenderIncubator.State> {
    private final ItemModelResolver resolver;
    public RenderIncubator(BlockEntityRendererProvider.Context context) { resolver = context.itemModelResolver(); }
    public static class State extends BlockEntityRenderState {
        final ItemStackRenderState[] items = new ItemStackRenderState[9];
        float rotation;
        State() { for (int i = 0; i < items.length; i++) items[i] = new ItemStackRenderState(); }
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(IncubatorBlockEntity block, State state, float partial, Vec3 camera, @Nullable ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderer.super.extractRenderState(block, state, partial, camera, breaking);
        state.rotation = block.getLevel() == null ? 0 : (block.getLevel().getGameTime() + partial) * 4.5F;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = block.inventory.getItem(i);
            state.items[i].clear();
            if (stack.getItem() instanceof ItemChicken || stack.getItem() instanceof ItemChickenEgg)
                resolver.updateForTopItem(state.items[i], stack, ItemDisplayContext.FIXED, block.getLevel(), null, i);
        }
    }
    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int i = 0; i < 9; i++) {
            if (state.items[i].isEmpty()) continue;
            pose.pushPose();
            pose.translate(0.25F + (i % 3) * 0.25F, 0.3F, 0.25F + (i / 3) * 0.25F);
            pose.scale(0.2F, 0.2F, 0.2F);
            pose.rotateDegrees(Axis.YP, state.rotation);
            state.items[i].submit(pose, collector, state.lightCoords, 0, 0);
            pose.popPose();
        }
    }
}
