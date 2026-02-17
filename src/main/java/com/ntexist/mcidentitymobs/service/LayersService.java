package com.ntexist.mcidentitymobs.service;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.TextureCounts.GenderCounts;
import com.ntexist.mcidentitymobs.data.SkinGradientLoader;
import com.ntexist.mcidentitymobs.data.SkinGradientRule;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class LayersService {
    public static CompoundTag getLayerSettings(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getLayerSettings();
    }
    public static void setLayerSettings(LivingEntity e, CompoundTag tag) {
        ((LivingEntityAccessor) e).mcidentitymobs$setLayerSettings(tag);
    }
    public static int getSkinIndex(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getSkinIndex();
    }
    public static void setSkinIndex(LivingEntity e, int val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setSkinIndex(val);
    }
    public static int getFaceIndex(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getFaceIndex();
    }
    public static void setFaceIndex(LivingEntity e, int val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setFaceIndex(val);
    }
    public static int getClothIndex(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getClothIndex();
    }
    public static void setClothIndex(LivingEntity e, int val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setClothIndex(val);
    }
    public static int getHairIndex(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getHairIndex();
    }
    public static void setHairIndex(LivingEntity e, int val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setHairIndex(val);
    }
    public static byte getSkinToneIndex(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getSkinToneIndex();
    }
    public static void setSkinToneIndex(LivingEntity e, byte val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setSkinToneIndex(val);
    }
    public static byte getHairColorU(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getHairColorU();
    }
    public static void setHairColorU(LivingEntity e, byte val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setHairColorU(val);
    }
    public static byte getHairColorV(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getHairColorV();
    }
    public static void setHairColorV(LivingEntity e, byte val) {
        ((LivingEntityAccessor) e).mcidentitymobs$setHairColorV(val);
    }
    public static float getBreastSize(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getBreastSize();
    }
    public static void setBreastSize(LivingEntity e, float size) {
        ((LivingEntityAccessor) e).mcidentitymobs$setBreastSize(size);
    }
    public static float getBreastOffsetX(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getBreastOffsetX();
    }
    public static void setBreastOffsetX(LivingEntity e, float x) {
        ((LivingEntityAccessor) e).mcidentitymobs$setBreastOffsetX(x);
    }
    public static float getBreastOffsetY(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getBreastOffsetY();
    }
    public static void setBreastOffsetY(LivingEntity e, float y) {
        ((LivingEntityAccessor) e).mcidentitymobs$setBreastOffsetY(y);
    }
    public static float getBreastOffsetZ(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getBreastOffsetZ();
    }
    public static void setBreastOffsetZ(LivingEntity e, float z) {
        ((LivingEntityAccessor) e).mcidentitymobs$setBreastOffsetZ(z);
    }
    public static float getBreastCleavage(LivingEntity e) {
        return ((LivingEntityAccessor) e).mcidentitymobs$getBreastCleavage();
    }
    public static void setBreastCleavage(LivingEntity e, float cleavage) {
        ((LivingEntityAccessor) e).mcidentitymobs$setBreastCleavage(cleavage);
    }
    public static void randomizeHumanoidLayers(LivingEntity e, Gender g){
        if (e instanceof Villager) {
            LivingEntityAccessor acc = (LivingEntityAccessor) e;
            RandomSource random = e.getRandom();

            GenderCounts counts = (g == Gender.MALE)
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
                assignClothIndex((Villager) e, acc, counts);
            }
            if (acc.mcidentitymobs$getHairIndex() == 0) {
                int max = counts.hair;
                acc.mcidentitymobs$setHairIndex(1 + random.nextInt(max));
            }
            if (acc.mcidentitymobs$getSkinToneIndex() == -1) {
                Holder<Biome> biome = e.level().getBiome(e.blockPosition());
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
            if (g == Gender.FEMALE && !e.isBaby()) {
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
                    acc.mcidentitymobs$setBreastOffsetX(
                            MIN_OFFSET_X + random.nextFloat() * (MAX_OFFSET_X - MIN_OFFSET_X)
                    );
                }
                if (acc.mcidentitymobs$getBreastOffsetY() == 0.0f) {
                    acc.mcidentitymobs$setBreastOffsetY(
                            MIN_OFFSET_Y + random.nextFloat() * (MAX_OFFSET_Y - MIN_OFFSET_Y)
                    );
                }
                if (acc.mcidentitymobs$getBreastOffsetZ() == 0.0f) {
                    acc.mcidentitymobs$setBreastOffsetY(
                            MIN_OFFSET_Y + random.nextFloat() * (MAX_OFFSET_Y - MIN_OFFSET_Y)
                    );
                }
                if (acc.mcidentitymobs$getBreastCleavage() == 0.0f) {
                    acc.mcidentitymobs$setBreastCleavage(
                            MIN_CLEAVAGE + random.nextFloat() * (MAX_CLEAVAGE - MIN_CLEAVAGE)
                    );
                }
            }
        }
    }
    public static void assignClothIndex(Villager e, LivingEntityAccessor acc, GenderCounts counts) {
        RandomSource random = e.getRandom();

        int maxIndex;

        if (e.isBaby()) {
            maxIndex = counts.clothing.baby;
        } else {
            ResourceLocation profId = ForgeRegistries.VILLAGER_PROFESSIONS
                    .getKey(e.getVillagerData().getProfession());
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
