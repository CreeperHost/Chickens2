package net.creeperhost.chickens.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import java.util.function.Consumer;

public class ChickenItemRender implements SpecialModelRenderer<EntityRenderState> {
    @Override
    public void submit(EntityRenderState state, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean foil, int outline) {
        if (state == null) return;
        pose.pushPose();
        pose.translate(0.5F, 0.0F, 0.5F);
        pose.rotateDegrees(Axis.YP, 45);
        pose.rotateDegrees(Axis.XP, 10);
        pose.rotateDegrees(Axis.ZP, 10);
        state.lightCoords = light;
        Minecraft.getInstance().getEntityRenderDispatcher().submit(state, new CameraRenderState(), 0, 0, 0, pose, collector);
        pose.popPose();
    }
    @Override public void getExtents(Consumer<Vector3fc> output) { output.accept(new Vector3f()); }
    @Override public EntityRenderState extractArgument(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        ChickenData data = ChickenData.fromItem(stack);
        if (data == null) return null;
        ChickensChicken chicken = ModEntities.CHICKEN.get().create(mc.level, EntitySpawnReason.SPAWNER);
        if (chicken == null) return null;
        // This preview entity is never added to a level, so it needs its own nonzero ID.
        chicken.setId(-1);
        data.apply(chicken);
        chicken.yHeadRot = 0;
        return mc.getEntityRenderDispatcher().extractEntity(chicken, 0);
    }
    public record Unbaked() implements SpecialModelRenderer.Unbaked<EntityRenderState> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());
        public MapCodec<Unbaked> type() { return MAP_CODEC; }
        public SpecialModelRenderer<EntityRenderState> bake(SpecialModelRenderer.BakingContext context) { return new ChickenItemRender(); }
    }
}
