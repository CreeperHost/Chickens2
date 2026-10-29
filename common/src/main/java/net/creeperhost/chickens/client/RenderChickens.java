package net.creeperhost.chickens.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.chickens.Chickens;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.entity.ChickensChicken;
import net.creeperhost.chickens.entity.EntityChickensChicken;
import net.creeperhost.chickens.entity.EntityRooster;

import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.animal.chicken.ChickenModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.chicken.Chicken;

import java.util.Map;

public class RenderChickens extends MobRenderer<Chicken, RenderChickens.ChickensRenderState, ChickensModel> {
    private final ChickensModel adult;
    private final ChickensModel baby;

    public RenderChickens(EntityRendererProvider.Context context) {
        super(context, new ChickensModel(context.bakeLayer(ChickensModel.LAYER_LOCATION)), 0.5F);
        this.adult = new ChickensModel(context.bakeLayer(ChickensModel.LAYER_LOCATION));
        this.baby = new ChickensModel(context.bakeLayer(ChickensModel.BABY_LAYER_LOCATION));
    }

    @Override
    public ChickensRenderState createRenderState() {
        return new ChickensRenderState();
    }

    @Override
    public Identifier getTextureLocation(ChickensRenderState state) {
        return state.texture;
    }

    @Override
    public void submit(ChickensRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        this.model = state.isBaby ? baby : adult;
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    public void extractRenderState(Chicken entity, ChickensRenderState state, float f) {
        super.extractRenderState(entity, state, f);
        state.flap = Mth.lerp(f, entity.oFlap, entity.flap);
        state.flapSpeed = Mth.lerp(f, entity.oFlapSpeed, entity.flapSpeed);
        if (entity instanceof ChickensChicken chicken) {
            ChickenVariant variant = chicken.getChickenVariant();
            state.texture = variant.texture();
            state.isRooster = chicken.isRooster();
        } else {

            if (entity instanceof EntityChickensChicken chickensChicken) {
                state.texture = chickensChicken.getTexture();
            } else {
                //TODO, temporary, just until i get the rooster variants figured out.
                state.texture = Identifier.fromNamespaceAndPath(Chickens.MOD_ID, "textures/entity/rooster.png");
            }
            state.isRooster = entity instanceof EntityRooster;
        }
    }

    public static class ChickensRenderState extends ChickenRenderState {
        public boolean isRooster = false;
        public Identifier texture;
    }
}
