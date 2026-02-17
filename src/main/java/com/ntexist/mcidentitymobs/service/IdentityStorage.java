package com.ntexist.mcidentitymobs.service;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class IdentityStorage {

    private static LivingEntityAccessor acc(LivingEntity e) {
        return (LivingEntityAccessor) e;
    }

    // -------- Gender --------

    public static boolean hasGender(LivingEntity e) {
        return !acc(e).mcidentitymobs$getGender().isEmpty();
    }
    public static String getGender(LivingEntity e) {
        return acc(e).mcidentitymobs$getGender();
    }
    public static void setGender(LivingEntity e, String gender) {
        acc(e).mcidentitymobs$setGender(gender);
    }

    // -------- Name --------

    public static boolean hasName(LivingEntity e) {
        return !acc(e).mcidentitymobs$getMobName().isEmpty();
    }
    public static String getName(LivingEntity e) {
        return acc(e).mcidentitymobs$getMobName();
    }
    public static void setName(LivingEntity e, String name) {
        acc(e).mcidentitymobs$setMobName(name);
    }

    // -------- Player Named --------

    public static boolean isPlayerNamed(LivingEntity e) {
        return acc(e).mcidentitymobs$isPlayerNamed();
    }
    public static void setPlayerNamed(LivingEntity e, boolean val) {
        acc(e).mcidentitymobs$setPlayerNamed(val);
    }

    // -------- Original ID --------

    public static String getOriginalId(LivingEntity e) {
        return acc(e).mcidentitymobs$getOriginalId();
    }
    public static void setOriginalId(LivingEntity e, String id) {
        acc(e).mcidentitymobs$setOriginalId(id);
    }

    // -------- Zombie --------

    public static String getZombieSavedName(LivingEntity e) {
        return acc(e).mcidentitymobs$getZombieSavedName();
    }
    public static void setZombieSavedName(LivingEntity e, @Nullable String name) {
        acc(e).mcidentitymobs$setZombieSavedName(name);
    }
    public static boolean isInConversion(LivingEntity e) {
        return acc(e).mcidentitymobs$isInConversion();
    }
    public static void setInConversion(LivingEntity e, boolean val) {
        acc(e).mcidentitymobs$setInConversion(val);
    }
    public static int getConversionTime(LivingEntity e) {
        return acc(e).mcidentitymobs$getConversionTime();
    }
    public static void setConversionTime(LivingEntity e, int ticks) {
        acc(e).mcidentitymobs$setConversionTime(ticks);
    }
    public static boolean isInfected(LivingEntity e) {
        return !acc(e).mcidentitymobs$getOriginalId().isEmpty();
    }
}
