package com.ntexist.mcidentitymobs.api;

import com.ntexist.mcidentitymobs.*;
import com.ntexist.mcidentitymobs.pipeline.SpawnPipeline;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.service.ColorService;
import com.ntexist.mcidentitymobs.service.IdentityStorage;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class MobIdentityAPI {

    private MobIdentityAPI() {}

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
    // Visual
    // -------------------------------------------------------------------------
    public static void applyColors(LivingEntity entity) {
        Gender gender = getGender(entity);
        if (gender != null) {
            ColorService.applyColorIfNeeded(entity, gender);
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
    // Utilities
    // -------------------------------------------------------------------------

    public static void resetAll(LivingEntity entity) {
        LivingEntityAccessor acc = (LivingEntityAccessor) entity;
        acc.mcidentitymobs$setGender("");
        acc.mcidentitymobs$setMobName("");
        acc.mcidentitymobs$setOriginalId("");
        acc.mcidentitymobs$setPlayerNamed(false);
        acc.mcidentitymobs$setZombieSavedName("");
        acc.mcidentitymobs$setConversionTime(-1);
        acc.mcidentitymobs$setInConversion(false);
    }

    public static boolean canBeCured(LivingEntity entity) {
        String origId = getOriginalId(entity);
        if (origId == null) return false;

        ResourceLocation loc = ResourceLocation.tryParse(origId);
        if (loc == null) return false;

        return ConfigManager.CONFIG.canBeInfected.containsKey(loc.toString());
    }
}