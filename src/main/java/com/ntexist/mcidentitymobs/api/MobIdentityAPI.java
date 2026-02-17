package com.ntexist.mcidentitymobs.api;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.enums.Gender;
import com.ntexist.mcidentitymobs.pipeline.SpawnPipeline;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.service.IdentityStorage;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
        String name = ((LivingEntityAccessor) entity).mcidentitymobs$getZombieSavedName();
        return name.isEmpty() ? null : name;
    }
    public static void setZombieSavedName(LivingEntity entity, @Nullable String name) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setZombieSavedName(name != null ? name : "");
    }
    public static boolean hasZombieSavedName(LivingEntity entity) {
        return !((LivingEntityAccessor) entity).mcidentitymobs$getZombieSavedName().isEmpty();
    }
    public static boolean isInConversion(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$isInConversion();
    }
    public static void setInConversion(LivingEntity entity, boolean value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setInConversion(value);
    }
    public static int getConversionTime(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getConversionTime();
    }
    public static void setConversionTime(LivingEntity entity, int ticks) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setConversionTime(ticks);
    }
    public static boolean isBeingCured(LivingEntity entity) {
        return getConversionTime(entity) > 0;
    }
    public static boolean isInfected(LivingEntity entity) {
        return hasOriginalId(entity);
    }

    // -------------------------------------------------------------------------
    // Layers
    // -------------------------------------------------------------------------
    public static CompoundTag getLayerSettings(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getLayerSettings();
    }
    public static void setLayerSettings(LivingEntity entity, CompoundTag tag) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setLayerSettings(tag);
    }
    public static int getSkinIndex(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getSkinIndex();
    }
    public static void setSkinIndex(LivingEntity entity, int value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setSkinIndex(value);
    }
    public static int getFaceIndex(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getFaceIndex();
    }
    public static void setFaceIndex(LivingEntity entity, int value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setFaceIndex(value);
    }
    public static int getClothIndex(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getClothIndex();
    }
    public static void setClothIndex(LivingEntity entity, int value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setClothIndex(value);
    }
    public static int getHairIndex(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getHairIndex();
    }
    public static void setHairIndex(LivingEntity entity, int value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setHairIndex(value);
    }
    public static byte getSkinToneIndex(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getSkinToneIndex();
    }
    public static void setSkinToneIndex(LivingEntity entity, byte value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setSkinToneIndex(value);
    }
    public static byte getHairColorU(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getHairColorU();
    }
    public static void setHairColorU(LivingEntity entity, byte value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setHairColorU(value);
    }
    public static byte getHairColorV(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getHairColorV();
    }
    public static void setHairColorV(LivingEntity entity, byte value) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setHairColorV(value);
    }
    public static float getBreastSize(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getBreastSize();
    }
    public static void setBreastSize(LivingEntity entity, float size) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setBreastSize(size);
    }
    public static float getBreastOffsetX(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getBreastOffsetX();
    }
    public static void setBreastOffsetX(LivingEntity entity, float x) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setBreastOffsetX(x);
    }
    public static float getBreastOffsetY(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getBreastOffsetY();
    }
    public static void setBreastOffsetY(LivingEntity entity, float y) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setBreastOffsetY(y);
    }
    public static float getBreastOffsetZ(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getBreastOffsetZ();
    }
    public static void setBreastOffsetZ(LivingEntity entity, float z) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setBreastOffsetZ(z);
    }
    public static float getBreastCleavage(LivingEntity entity) {
        return ((LivingEntityAccessor) entity).mcidentitymobs$getBreastCleavage();
    }
    public static void setBreastCleavage(LivingEntity entity, float cleavage) {
        ((LivingEntityAccessor) entity).mcidentitymobs$setBreastCleavage(cleavage);
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