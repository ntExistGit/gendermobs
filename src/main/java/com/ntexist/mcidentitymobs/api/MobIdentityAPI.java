package com.ntexist.mcidentitymobs.api;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.config.TextureCounts.GenderCounts;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.ntexist.mcidentitymobs.pipeline.SpawnPipeline;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.service.IdentityStorage;
import com.ntexist.mcidentitymobs.service.LayersService;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MobIdentityAPI {

    private MobIdentityAPI() {}

    // -------------------------------------------------------------------------
    // UUID
    // -------------------------------------------------------------------------
    @Nullable
    public static UUID getCuringPlayerUUID(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getCuringPlayerUUID();
    }
    public static void setCuringPlayerUUID(LivingEntity entity, @Nullable UUID uuid) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setCuringPlayerUUID(uuid);
    }
    public static boolean hasCuringPlayerUUID(LivingEntity entity) {
        return getCuringPlayerUUID(entity) != null;
    }

    // -------------------------------------------------------------------------
    // Spawn
    // -------------------------------------------------------------------------
    public static void handleSpawn(LivingEntity entity) {
        SpawnPipeline.onSpawn(entity);
    }

    // -------------------------------------------------------------------------
    // Gender
    // -------------------------------------------------------------------------
    @Nullable
    public static Gender getGender(LivingEntity entity) {
        if (!IdentityStorage.hasGender(entity)) return null;
        return Gender.fromString(IdentityStorage.getGender(entity));
    }
    public static void setGender(LivingEntity entity, @Nullable Gender gender) {
        if (gender == null) {
            IdentityStorage.setGender(entity, "");
        } else {
            IdentityStorage.setGender(entity, gender.name().toLowerCase());
        }
    }
    public static boolean hasGender(LivingEntity entity) {
        return IdentityStorage.hasGender(entity);
    }

    // -------------------------------------------------------------------------
    // Name
    // -------------------------------------------------------------------------
    @Nullable
    public static String getMobName(LivingEntity entity) {
        return IdentityStorage.hasName(entity) ? IdentityStorage.getName(entity) : null;
    }
    public static void setMobName(LivingEntity entity, @Nullable String name) {
        IdentityStorage.setName(entity, name != null ? name : "");
    }
    public static void applyNameLogic(LivingEntity entity) {
        Gender gender = getGender(entity);
        if (gender != null) {
            NameService.handleName(entity, gender);
        }
    }

    // -------------------------------------------------------------------------
    // Player Named
    // -------------------------------------------------------------------------
    public static boolean isPlayerNamed(LivingEntity entity) {
        return IdentityStorage.isPlayerNamed(entity);
    }
    public static void setPlayerNamed(LivingEntity entity, boolean value) {
        IdentityStorage.setPlayerNamed(entity, value);
    }

    // -------------------------------------------------------------------------
    // Original ID
    // -------------------------------------------------------------------------
    @Nullable
    public static String getOriginalId(LivingEntity entity) {
        String id = IdentityStorage.getOriginalId(entity);
        return id.isEmpty() ? null : id;
    }
    public static void setOriginalId(LivingEntity entity, @Nullable String originalId) {
        IdentityStorage.setOriginalId(entity, originalId != null ? originalId : "");
    }
    public static boolean hasOriginalId(LivingEntity entity) {
        return !IdentityStorage.getOriginalId(entity).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Conversion
    // -------------------------------------------------------------------------
    @Nullable
    public static String getZombieSavedName(LivingEntity entity) {
        String name = IdentityStorage.getZombieSavedName(entity);
        return name.isEmpty() ? null : name;
    }
    public static void setZombieSavedName(LivingEntity entity, @Nullable String name) {
        IdentityStorage.setZombieSavedName(entity, name != null ? name : "");
    }
    public static boolean hasZombieSavedName(LivingEntity entity) {
        return !IdentityStorage.getZombieSavedName(entity).isEmpty();
    }
    public static boolean isInConversion(LivingEntity entity) {
        return IdentityStorage.isInConversion(entity);
    }
    public static void setInConversion(LivingEntity entity, boolean value) {
        IdentityStorage.setInConversion(entity, value);
    }
    public static int getConversionTime(LivingEntity entity) {
        return IdentityStorage.getConversionTime(entity);
    }
    public static void setConversionTime(LivingEntity entity, int ticks) {
        IdentityStorage.setConversionTime(entity, ticks);
    }
    public static boolean isBeingCured(LivingEntity entity) {
        return IdentityStorage.getConversionTime(entity) > 0;
    }
    public static boolean isInfected(LivingEntity entity) {
        return IdentityStorage.isInfected(entity);
    }

    public static void copyIdentity(LivingEntity from, LivingEntity to) {

        Gender gender = getGender(from);
        setGender(to, gender);

        String mobName = getMobName(from);
        setMobName(to, mobName);

        boolean playerNamed = isPlayerNamed(from);
        setPlayerNamed(to, playerNamed);

        String originalId = getOriginalId(from);
        if (originalId != null) setOriginalId(to, originalId);

        LayersService.setLayerSettings(to, LayersService.getLayerSettings(from));
    }

    // -------------------------------------------------------------------------
    // Layers
    // -------------------------------------------------------------------------

    public static void randomizeHumanoidLayers(LivingEntity entity, Gender gender) {
        if (gender == null) return;
        LayersService.randomizeHumanoidLayers(entity, gender);
    }
    public static void assignClothIndex(Villager entity, GenderCounts counts){
        LayersService.assignClothIndex(entity, (LivingEntityAccessor) entity, counts);
    }

    public static CompoundTag getLayerSettings(LivingEntity entity) {
        return LayersService.getLayerSettings(entity);
    }
    public static void setLayerSettings(LivingEntity entity, CompoundTag tag) {
        LayersService.setLayerSettings(entity, tag);
    }
    public static int getSkinIndex(LivingEntity entity) {
        return LayersService.getSkinIndex(entity);
    }
    public static void setSkinIndex(LivingEntity entity, int value) {
        LayersService.setSkinIndex(entity, value);
    }
    public static int getFaceIndex(LivingEntity entity) {
        return LayersService.getFaceIndex(entity);
    }
    public static void setFaceIndex(LivingEntity entity, int value) {
        LayersService.setFaceIndex(entity, value);
    }
    public static int getClothIndex(LivingEntity entity) {
        return LayersService.getClothIndex(entity);
    }
    public static void setClothIndex(LivingEntity entity, int value) {
        LayersService.setClothIndex(entity, value);
    }
    public static int getHairIndex(LivingEntity entity) {
        return LayersService.getHairIndex(entity);
    }
    public static void setHairIndex(LivingEntity entity, int value) {
        LayersService.setHairIndex(entity, value);
    }
    public static byte getSkinToneIndex(LivingEntity entity) {
        return LayersService.getSkinToneIndex(entity);
    }
    public static void setSkinToneIndex(LivingEntity entity, byte value) {
        LayersService.setSkinToneIndex(entity, value);
    }
    public static byte getHairColorU(LivingEntity entity) {
        return LayersService.getHairColorU(entity);
    }
    public static void setHairColorU(LivingEntity entity, byte value) {
        LayersService.setHairColorU(entity, value);
    }
    public static byte getHairColorV(LivingEntity entity) {
        return LayersService.getHairColorV(entity);
    }
    public static void setHairColorV(LivingEntity entity, byte value) {
        LayersService.setHairColorV(entity, value);
    }
    public static float getBreastSize(LivingEntity entity) {
        return LayersService.getBreastSize(entity);
    }
    public static void setBreastSize(LivingEntity entity, float size) {
        LayersService.setBreastSize(entity, size);
    }
    public static float getBreastOffsetX(LivingEntity entity) {
        return LayersService.getBreastOffsetX(entity);
    }
    public static void setBreastOffsetX(LivingEntity entity, float x) {
        LayersService.setBreastOffsetX(entity, x);
    }
    public static float getBreastOffsetY(LivingEntity entity) {
        return LayersService.getBreastOffsetY(entity);
    }
    public static void setBreastOffsetY(LivingEntity entity, float y) {
        LayersService.setBreastOffsetY(entity, y);
    }
    public static float getBreastOffsetZ(LivingEntity entity) {
        return LayersService.getBreastOffsetZ(entity);
    }
    public static void setBreastOffsetZ(LivingEntity entity, float z) {
        LayersService.setBreastOffsetZ(entity, z);
    }
    public static float getBreastCleavage(LivingEntity entity) {
        return LayersService.getBreastCleavage(entity);
    }
    public static void setBreastCleavage(LivingEntity entity, float cleavage) {
        LayersService.setBreastCleavage(entity, cleavage);
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------
    public static boolean canBeCured(LivingEntity entity) {
        String origId = getOriginalId(entity);
        if (origId == null) return false;

        ResourceLocation loc = ResourceLocation.tryParse(origId);
        if (loc == null) return false;

        return ConfigManager.CONFIG.canBeInfected.containsKey(loc.toString());
    }
}