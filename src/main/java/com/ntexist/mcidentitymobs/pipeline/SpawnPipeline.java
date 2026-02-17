package com.ntexist.mcidentitymobs.pipeline;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.TextureCounts;
import com.ntexist.mcidentitymobs.data.SkinGradientLoader;
import com.ntexist.mcidentitymobs.data.SkinGradientRule;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.ntexist.mcidentitymobs.service.GenderService;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnPipeline {

    public static void onSpawn(LivingEntity entity) {
        Gender gender = GenderService.getOrAssignGender(entity);
        if (gender == null) return;

        NameService.handleName(entity, gender);

        if (entity instanceof Villager) {
            LivingEntityAccessor acc = (LivingEntityAccessor) entity;
            RandomSource random = entity.getRandom();

            TextureCounts.GenderCounts counts = (gender == Gender.MALE)
                    ? ConfigManager.TEXTURE_COUNTS_DATA.male
                    : ConfigManager.TEXTURE_COUNTS_DATA.female;

            if (counts == null) return;

            if (acc.mcidentitymobs$getSkinIndex() == 0) {
                int max = counts.skin;
                acc.mcidentitymobs$setSkinIndex(1 + random.nextInt(max));
            }
            if (acc.mcidentitymobs$getFaceIndex() == 0) {
                int max = counts.face;
                acc.mcidentitymobs$setFaceIndex(1 + random.nextInt(max));
            }
            if (acc.mcidentitymobs$getClothIndex() == 0) {
                assignClothIndex((Villager) entity, acc, counts);
            }
            if (acc.mcidentitymobs$getHairIndex() == 0) {
                int max = counts.hair;
                acc.mcidentitymobs$setHairIndex(1 + random.nextInt(max));
            }
            if (acc.mcidentitymobs$getSkinToneIndex() == -1) {
                Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
                SkinGradientRule rule = SkinGradientLoader.getRuleForBiome(biome);
                if (rule == null) {
                    rule = SkinGradientLoader.getDefaultRule();
                }
                int maxTone = rule.getToneCount();
                acc.mcidentitymobs$setSkinToneIndex((byte) (maxTone > 0 ? random.nextInt(maxTone) : 0));
            }
            if (acc.mcidentitymobs$getHairColorU() == -1 || acc.mcidentitymobs$getHairColorV() == -1) {
                acc.mcidentitymobs$setHairColorU((byte) random.nextInt(32));
                acc.mcidentitymobs$setHairColorV((byte) random.nextInt(32));
            }
            if (gender == Gender.FEMALE && !entity.isBaby()) {
                final float MIN_BUST = 0.4f;
                final float MAX_BUST = 1.0f;
                final float MIN_OFFSET_X = -0.1f;
                final float MAX_OFFSET_X = 0.1f;
                final float MIN_OFFSET_Y = -0.1f;
                final float MAX_OFFSET_Y = 0.1f;
                final float MIN_OFFSET_Z = -0.1f;
                final float MAX_OFFSET_Z = 0.1f;
                final float MIN_CLEAVAGE = 0.0f;
                final float MAX_CLEAVAGE = 0.01f;

                if (acc.mcidentitymobs$getBreastSize() == 0.0f) {
                    acc.mcidentitymobs$setBreastSize(MIN_BUST + random.nextFloat() * (MAX_BUST - MIN_BUST));
                }
                if (acc.mcidentitymobs$getBreastOffsetX() == 0.0f) {
                    acc.mcidentitymobs$setBreastOffsetX(MIN_OFFSET_X + random.nextFloat() * (MIN_OFFSET_X - MAX_OFFSET_X));
                }
                if (acc.mcidentitymobs$getBreastOffsetY() == 0.0f) {
                    acc.mcidentitymobs$setBreastOffsetY(MIN_OFFSET_Y + random.nextFloat() * (MIN_OFFSET_Y - MAX_OFFSET_Y));
                }
                if (acc.mcidentitymobs$getBreastOffsetZ() == 0.0f) {
                    acc.mcidentitymobs$setBreastOffsetZ(MIN_OFFSET_Z + random.nextFloat() * (MIN_OFFSET_Z - MAX_OFFSET_Z));
                }
                if (acc.mcidentitymobs$getBreastCleavage() == 0.0f) {
                    acc.mcidentitymobs$setBreastCleavage(MIN_CLEAVAGE + random.nextFloat() * (MIN_CLEAVAGE - MAX_CLEAVAGE));
                }
            }
        }
    }

    public static void assignClothIndex(Villager entity, LivingEntityAccessor acc, TextureCounts.GenderCounts counts) {
        RandomSource random = entity.getRandom();

        int maxIndex;

        if (entity.isBaby()) {
            maxIndex = counts.clothing.baby;
        } else {
            ResourceLocation profId = BuiltInRegistries.VILLAGER_PROFESSION
                    .getKey(entity.getVillagerData().getProfession());
            String profession = profId == null ? "none" : profId.getPath();

            if (!profession.equals("none")) {
                maxIndex = counts.clothing.byProfession.getOrDefault(profession, counts.clothing.none);
            } else {
                maxIndex = counts.clothing.none;
            }
        }

        if (maxIndex > 0) {
            int newIndex = 1 + random.nextInt(maxIndex);
            acc.mcidentitymobs$setClothIndex(newIndex);
        }
    }
}
