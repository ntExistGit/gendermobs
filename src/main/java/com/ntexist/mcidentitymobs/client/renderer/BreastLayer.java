package com.ntexist.mcidentitymobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.client.model.BreastBoxes;
import com.ntexist.mcidentitymobs.client.model.ModelBox;
import com.ntexist.mcidentitymobs.data.SkinGradientLoader;
import com.ntexist.mcidentitymobs.data.SkinGradientRule;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.ntexist.mcidentitymobs.enums.PartType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;
import org.joml.Quaternionf;

public class BreastLayer extends RenderLayer<Villager, PlayerModel<Villager>> {
    private final PartType partType;
    private final BreastBoxes boxes = new BreastBoxes();

    public BreastLayer(RenderLayerParent<Villager, PlayerModel<Villager>> renderer, PartType partType) {
        super(renderer);
        this.partType = partType;
    }

    private void renderBox(ModelBox box, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                           int overlay, float r, float g, float b, float a, ResourceLocation tex) {
        var pose = poseStack.last();
        for (var quad : box.quads) {
            if (quad != null) {
                quad.render(pose, buffer.getBuffer(RenderType.entityTranslucent(tex)),
                        r, g, b, a, packedLight, overlay);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Villager entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (MobIdentityAPI.getGender(entity) != Gender.FEMALE || entity.isBaby()) return;

        Gender gender = MobIdentityAPI.getGender(entity);
        String genderStr = gender == Gender.MALE ? "male" : "female";

        ResourceLocation tex;
        if (partType == PartType.SKIN) {
            int skinIndex = MobIdentityAPI.getSkinIndex(entity);
            if (skinIndex <= 0) return;
            ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath("mcidentitymobs",
                    "textures/entity/villager/" + genderStr + "/skin/" + String.format("%02d", skinIndex) + ".png");
            if (Minecraft.getInstance().getResourceManager().getResource(baseTex).isEmpty()) return;
            byte toneIndex = MobIdentityAPI.getSkinToneIndex(entity);
            if (toneIndex < 0) return;
            Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
            SkinGradientRule rule = SkinGradientLoader.getRuleForBiome(biome);
            if (rule == null) rule = SkinGradientLoader.getDefaultRule();
            tex = TintedTextureCache.getOrCreate(baseTex, rule.getGradientTexture(), toneIndex);
            if (tex == null) return;
        } else if (partType == PartType.CLOTH) {
            int clothIndex = MobIdentityAPI.getClothIndex(entity);
            if (clothIndex <= 0) return;
            String folder;
            if (entity.isBaby()) {
                folder = "baby";
            } else {
                ResourceLocation profId = BuiltInRegistries.VILLAGER_PROFESSION
                        .getKey(entity.getVillagerData().getProfession());
                String profession = profId == null ? "none" : profId.getPath();
                folder = profession.equals("none") ? "none" : profession;
            }
            ResourceLocation tryTex = ResourceLocation.fromNamespaceAndPath("mcidentitymobs",
                    "textures/entity/villager/" + genderStr + "/cloth/" + folder + "/" + String.format("%02d", clothIndex) + ".png");
            if (Minecraft.getInstance().getResourceManager().getResource(tryTex).isEmpty()) {
                tryTex = ResourceLocation.fromNamespaceAndPath("mcidentitymobs",
                        "textures/entity/villager/" + genderStr + "/cloth/none/" + String.format("%02d", clothIndex) + ".png");
            }
            if (Minecraft.getInstance().getResourceManager().getResource(tryTex).isEmpty()) return;
            tex = tryTex;
        } else if (partType == PartType.HAIR) {
            int hairIndex = MobIdentityAPI.getHairIndex(entity);
            if (hairIndex <= 0) return;
            ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath("mcidentitymobs",
                    "textures/entity/villager/" + genderStr + "/hair/" + String.format("%02d", hairIndex) + ".png");
            if (Minecraft.getInstance().getResourceManager().getResource(baseTex).isEmpty()) return;
            byte u = MobIdentityAPI.getHairColorU(entity);
            byte v = MobIdentityAPI.getHairColorV(entity);
            if (u < 0 || v < 0) return;
            ResourceLocation palette = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/colormap/villager_hair.png");
            tex = TintedTextureCache.getColoredFromPalette(baseTex, palette, u, v);
            if (tex == null) return;
        } else {
            return;
        }

        PlayerModel<Villager> model = getParentModel();
        var body = model.body;

        poseStack.pushPose();
        body.translateAndRotate(poseStack);

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        // Левая грудь: основной и оверлей
        poseStack.pushPose();
        poseStack.translate(2.0F / 16.0F, 2.0F / 16.0F, 0.0F);
        poseStack.mulPose(new Quaternionf().rotationX(-0.3491F));
        renderBox(boxes.leftMain, poseStack, buffer, packedLight, overlay, 1,1,1,1, tex);
        renderBox(boxes.leftOverlay, poseStack, buffer, packedLight, overlay, 1,1,1,1, tex);
        poseStack.popPose();

        // Правая грудь: основной и оверлей
        poseStack.pushPose();
        poseStack.translate(-2.0F / 16.0F, 2.0F / 16.0F, 0.0F);
        poseStack.mulPose(new Quaternionf().rotationX(-0.3491F));
        renderBox(boxes.rightMain, poseStack, buffer, packedLight, overlay, 1,1,1,1, tex);
        renderBox(boxes.rightOverlay, poseStack, buffer, packedLight, overlay, 1,1,1,1, tex);
        poseStack.popPose();

        poseStack.popPose();
    }
}