package com.ntexist.mcidentitymobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.ntexist.mcidentitymobs.enums.PartType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;

public class VillagerAsPlayerRenderer extends LivingEntityRenderer<Villager, PlayerModel<Villager>> {

    private final PlayerModel<Villager> normalModel;
    private final PlayerModel<Villager> slimModel;

    private static final ResourceLocation TRANSPARENT = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/entity/empty.png");

    public VillagerAsPlayerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);

        this.normalModel = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true);

        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()));

        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer<>(ctx, this));

        this.addLayer(new VillagerLayer(this, PartType.SKIN));
        this.addLayer(new VillagerLayer(this, PartType.FACE));
        this.addLayer(new VillagerLayer(this, PartType.CLOTH));
        this.addLayer(new VillagerLayer(this, PartType.HAIR));
        this.addLayer(new BreastLayer(this, PartType.SKIN));
        this.addLayer(new BreastLayer(this, PartType.CLOTH));
        this.addLayer(new BreastLayer(this, PartType.HAIR));
    }

    @Override
    public void render(Villager entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        Gender gender = MobIdentityAPI.getGender(entity);
        boolean isSlim = (gender == Gender.FEMALE);

        this.model = isSlim ? slimModel : normalModel;

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Villager entity) {
        return TRANSPARENT;
    }
}
