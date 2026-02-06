package com.ntexist.mcidentitymobs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;

import java.util.HashMap;
import java.util.Map;

public class ZombieCureHandler {

    private static final Map<Integer, CureData> CURING_ZOMBIES = new HashMap<>();

    public static void startCure(LivingEntity zombie, MobEffect activeEffect, String usedItem, String targetId) {
        if (!(zombie instanceof ZombieVillager)) return;

        int entityId = zombie.getId();
        CureData cureData = new CureData(zombie, activeEffect, usedItem, targetId);
        CURING_ZOMBIES.put(entityId, cureData);

        System.out.println("[MCIdentityMobs] Запуск лечения зомби " + entityId +
                " → " + targetId + " за " + cureData.cureTime + " тиков");
    }

    public static void tick() {
        if (CURING_ZOMBIES.isEmpty()) return;

        // Копируем для безопасной итерации
        Map<Integer, CureData> copy = new HashMap<>(CURING_ZOMBIES);

        for (Map.Entry<Integer, CureData> entry : copy.entrySet()) {
            CureData cureData = entry.getValue();

            if (cureData.zombie.isRemoved() || !cureData.zombie.isAlive()) {
                CURING_ZOMBIES.remove(entry.getKey());
                continue;
            }

            cureData.ticksRemaining--;

            if (cureData.ticksRemaining <= 0) {
                completeCure(cureData);
                CURING_ZOMBIES.remove(entry.getKey());
            }
        }
    }

    private static void completeCure(CureData cureData) {
        System.out.println("[MCIdentityMobs] Завершение лечения зомби → " + cureData.targetId);

        // TODO: Здесь нужно спавнить новую сущность
        // LivingEntity cured = spawnEntity(cureData.targetId, cureData.zombie.level(), cureData.zombie.position());
        // if (cured != null) {
        //     InfectionPipeline.afterCured(cureData.zombie, cured);
        //     cureData.zombie.discard();
        // }
    }

    public static void save(CompoundTag tag) {
        CompoundTag curingTag = new CompoundTag();
        for (Map.Entry<Integer, CureData> entry : CURING_ZOMBIES.entrySet()) {
            entry.getValue().save(curingTag, String.valueOf(entry.getKey()));
        }
        tag.put("CuringZombies", curingTag);
    }

    public static void load(CompoundTag tag) {
        if (tag.contains("CuringZombies")) {
            // TODO: Загрузка сохраненных процессов лечения
        }
    }

    private static class CureData {
        final LivingEntity zombie;
        final MobEffect effect;
        final String item;
        final String targetId;
        final int cureTime;
        int ticksRemaining;

        CureData(LivingEntity zombie, MobEffect effect, String item, String targetId) {
            this.zombie = zombie;
            this.effect = effect;
            this.item = item;
            this.targetId = targetId;
            this.cureTime = InfectionRulesService.getCureTime(zombie);
            this.ticksRemaining = this.cureTime;
        }

        void save(CompoundTag tag, String key) {
            CompoundTag dataTag = new CompoundTag();
            dataTag.putInt("ZombieId", zombie.getId());
            // ... сохранить остальные данные
            tag.put(key, dataTag);
        }

        static CureData load(CompoundTag tag) {
            // TODO: Загрузка
            return null;
        }
    }
}