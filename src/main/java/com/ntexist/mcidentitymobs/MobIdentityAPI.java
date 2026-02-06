package com.ntexist.mcidentitymobs;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

/**
 * Публичный API для других модов и будущих систем.
 * Единственная точка входа во всю биологию.
 */
public class MobIdentityAPI {

    // -------- Spawn --------
    public static void handleSpawn(LivingEntity entity) {
        SpawnPipeline.onSpawn(entity);
    }

    // -------- Gender --------
    public static Gender getGender(LivingEntity entity) {
        if (!IdentityStorage.hasGender(entity)) return null;
        return Gender.fromString(IdentityStorage.getGender(entity));
    }

    public static void setGender(LivingEntity entity, Gender gender) {
        IdentityStorage.setGender(entity, gender.name().toLowerCase());
    }

    // -------- Name --------
    public static String getName(LivingEntity entity) {
        if (!IdentityStorage.hasName(entity)) return null;
        return IdentityStorage.getName(entity);
    }

    public static void setName(LivingEntity entity, String name) {
        IdentityStorage.setName(entity, name);
    }

    public static void applyNameLogic(LivingEntity entity) {
        Gender gender = getGender(entity);
        if (gender == null) return;
        NameService.handleName(entity, gender);
    }

    // -------- Visual --------
    public static void applyColors(LivingEntity entity) {
        Gender gender = getGender(entity);
        if (gender == null) return;
        ColorService.applyColorIfNeeded(entity, gender);
    }

    // -------- Zombie --------
    public static void onInfected(LivingEntity original, LivingEntity zombie) {
        InfectionPipeline.onInfected(original, zombie);
    }

    public static String onCured(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        return InfectionPipeline.onCured(zombie, activeEffect, usedItem);
    }

    public static void afterCured(LivingEntity zombie, LivingEntity cured) {
        InfectionPipeline.afterCured(zombie, cured);
    }

    // -------- Identity --------
    public static boolean isPlayerNamed(LivingEntity entity) {
        return IdentityStorage.isPlayerNamed(entity);
    }

    public static void setPlayerNamed(LivingEntity entity, boolean val) {
        IdentityStorage.setPlayerNamed(entity, val);
    }

    public static String getOriginalId(LivingEntity entity) {
        return IdentityStorage.getOriginalId(entity);
    }

    // -------- Infection Rules --------
    public static boolean isCurable(LivingEntity zombie) {
        return InfectionRulesService.isCurable(zombie);
    }

    public static boolean canBeCuredNow(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        return InfectionRulesService.canBeCuredNow(zombie, activeEffect, usedItem);
    }

    public static int getCureTime(LivingEntity zombie) {
        return InfectionRulesService.getCureTime(zombie);
    }
}