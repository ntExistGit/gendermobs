package com.ntexist.mcidentitymobs;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class InfectionPipeline {

    public static void onInfected(LivingEntity original, LivingEntity zombie) {
        System.out.println("[MCIdentityMobs] InfectionPipeline: Заражение");
        ZombieService.onInfected(original, zombie);
    }

    public static String onCured(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        System.out.println("[MCIdentityMobs] InfectionPipeline: Лечение с эффектом=" +
                (activeEffect != null ? activeEffect : "null") +
                ", предмет=" + usedItem);
        return ZombieService.onCured(zombie, activeEffect, usedItem);
    }

    public static void afterCured(LivingEntity zombie, LivingEntity cured) {
        System.out.println("[MCIdentityMobs] InfectionPipeline: После лечения");
        ZombieService.applyRestoredIdentity(zombie, cured);

        Gender gender = MobIdentityAPI.getGender(cured);
        if (gender != null) {
            NameService.handleName(cured, gender);
            ColorService.applyColorIfNeeded(cured, gender);
        }
    }
}