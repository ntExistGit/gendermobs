package com.ntexist.mcidentitymobs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class VillagerLayer extends RenderLayer<Villager, PlayerModel<Villager>> {
    private final PartType partType;

    private static final Map<UUID, BlinkState> BLINK_CACHE = new WeakHashMap<>();

    private static class BlinkState {
        private long nextBlinkStart;
        private long blinkEnd;

        private static final int MIN_BLINK_DURATION = 4;
        private static final int MAX_BLINK_DURATION = 8;
        private static final int MIN_INTERVAL = 200;
        private static final int MAX_INTERVAL = 240;

        BlinkState(long currentTick) {
            RandomSource random = RandomSource.create();
            long interval = random.nextIntBetweenInclusive(MIN_INTERVAL, MAX_INTERVAL * 2);
            this.nextBlinkStart = currentTick + interval;
            this.blinkEnd = -1;
        }

        boolean update(long currentTick) {
            if (currentTick >= nextBlinkStart) {
                RandomSource random = RandomSource.create();
                long duration = random.nextIntBetweenInclusive(MIN_BLINK_DURATION, MAX_BLINK_DURATION);
                blinkEnd = currentTick + duration;
                long interval = random.nextIntBetweenInclusive(MIN_INTERVAL, MAX_INTERVAL);
                nextBlinkStart = currentTick + interval;
                return true;
            } else if (currentTick < blinkEnd) {
                return true;
            } else {
                return false;
            }
        }
    }

    public VillagerLayer(RenderLayerParent<Villager, PlayerModel<Villager>> renderer, PartType partType) {
        super(renderer);
        this.partType = partType;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Villager entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        int index;
        switch (partType) {
            case SKIN: index = MobIdentityAPI.getSkinIndex(entity); break;
            case FACE: index = MobIdentityAPI.getFaceIndex(entity); break;
            case CLOTH: index = MobIdentityAPI.getClothIndex(entity); break;
            case HAIR: index = MobIdentityAPI.getHairIndex(entity); break;
            default: return;
        }
        if (index <= 0) return;

        Gender gender = MobIdentityAPI.getGender(entity);
        String genderStr = gender == Gender.MALE ? "male" : "female";

        // ================== BASE TEXTURE ==================

        ResourceLocation baseTex;

        if (partType == PartType.CLOTH) {

            String folder;

            if (entity.isBaby()) {
                folder = "baby";
            } else {
                ResourceLocation profId = BuiltInRegistries.VILLAGER_PROFESSION
                        .getKey(entity.getVillagerData().getProfession());

                String profession = profId == null ? "none" : profId.getPath();
                folder = profession.equals("none") ? "none" : profession;
            }

            ResourceLocation tryTex = ResourceLocation.fromNamespaceAndPath(
                    "mcidentitymobs",
                    "textures/entity/villager/" + genderStr +
                            "/cloth/" + folder +
                            "/" + String.format("%02d", index) + ".png"
            );

            if (Minecraft.getInstance().getResourceManager().getResource(tryTex).isEmpty()) {
                tryTex = ResourceLocation.fromNamespaceAndPath(
                        "mcidentitymobs",
                        "textures/entity/villager/" + genderStr +
                                "/cloth/none/" +
                                String.format("%02d", index) + ".png"
                );
            }

            baseTex = tryTex;

        } else {
            baseTex = ResourceLocation.fromNamespaceAndPath(
                    "mcidentitymobs",
                    "textures/entity/villager/" + genderStr +
                            "/" + partType.getFolder() +
                            "/" + String.format("%02d", index) + ".png"
            );
        }

        if (Minecraft.getInstance().getResourceManager().getResource(baseTex).isEmpty()) {
            return;
        }

        PlayerModel<Villager> model = getParentModel();

        // ================== SKIN ==================

        if (partType == PartType.SKIN) {
            byte toneIndex = MobIdentityAPI.getSkinToneIndex(entity);
            if (toneIndex < 0) return;

            Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
            SkinGradientRule rule = SkinGradientLoader.getRuleForBiome(biome);
            if (rule == null) rule = SkinGradientLoader.getDefaultRule();

            ResourceLocation finalTex = TintedTextureCache.getOrCreate(
                    baseTex, rule.getGradientTexture(), toneIndex
            );
            if (finalTex == null) return;

            model.renderToBuffer(poseStack,
                    buffer.getBuffer(RenderType.entityTranslucent(finalTex)),
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                    1, 1, 1, 1);

            // ================== HAIR ==================

        } else if (partType == PartType.HAIR) {
            byte u = MobIdentityAPI.getHairColorU(entity);
            byte v = MobIdentityAPI.getHairColorV(entity);
            if (u < 0 || v < 0) return;

            ResourceLocation palette = ResourceLocation.fromNamespaceAndPath(
                    "mcidentitymobs", "textures/colormap/villager_hair.png"
            );

            ResourceLocation finalTex = TintedTextureCache.getColoredFromPalette(
                    baseTex, palette, u, v
            );
            if (finalTex == null) return;

            model.renderToBuffer(poseStack,
                    buffer.getBuffer(RenderType.entityTranslucent(finalTex)),
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                    1, 1, 1, 1);

            // ================== FACE + BLINK ==================

        } else if (partType == PartType.FACE) {
            UUID uuid = entity.getUUID();
            BlinkState state = BLINK_CACHE.get(uuid);
            if (state == null) {
                state = new BlinkState(entity.tickCount);
                BLINK_CACHE.put(uuid, state);
            }

            boolean blinking = state.update(entity.tickCount);

            if (entity.tickCount < BlinkState.MIN_INTERVAL / 2) {
                blinking = false;
            }

            boolean eyesClosed =
                    blinking ||
                            entity.isSleeping() ||
                            entity.hurtTime > 0 ||
                            entity.isDeadOrDying();

            ResourceLocation finalTex;
            if (eyesClosed) {
                String path = baseTex.getPath();
                String blinkPath = path.substring(0, path.length() - 4) + "_blink.png";
                ResourceLocation blinkTex = ResourceLocation.fromNamespaceAndPath(
                        baseTex.getNamespace(), blinkPath
                );

                if (Minecraft.getInstance().getResourceManager().getResource(blinkTex).isPresent()) {
                    finalTex = blinkTex;
                } else {
                    finalTex = baseTex;
                }
            } else {
                finalTex = baseTex;
            }

            model.renderToBuffer(poseStack,
                    buffer.getBuffer(RenderType.entityTranslucent(finalTex)),
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                    1, 1, 1, 1);

            // ================== DEFAULT ==================

        } else {
            model.renderToBuffer(poseStack,
                    buffer.getBuffer(RenderType.entityTranslucent(baseTex)),
                    packedLight,
                    LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                    1, 1, 1, 1);
        }
    }
}
