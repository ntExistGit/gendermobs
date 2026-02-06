package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ZombieService {

    /**
     * Когда кого-то заразили
     */
    public static void onInfected(LivingEntity original, LivingEntity zombie) {
        String originalId = getEntityId(original);
        String zombieId = getEntityId(zombie);

        System.out.println("[MCIdentityMobs] Инфекция: " + originalId + " → " + zombieId);

        // Проверяем, что это разрешенное превращение
        InfectionData infectionData = null;
        for (Map.Entry<String, InfectionData> entry : ConfigManager.CONFIG.canBeInfected.entrySet()) {
            if (entry.getKey().equals(originalId) && entry.getValue().zombie.equals(zombieId)) {
                infectionData = entry.getValue();
                break;
            }
        }

        if (infectionData == null) {
            System.out.println("[MCIdentityMobs] Ошибка: нет конфига для " + originalId + " → " + zombieId);
            return;
        }

        // Сохраняем оригинальный ID
        IdentityStorage.setOriginalId(zombie, originalId);

        // Сохраняем пол
        if (IdentityStorage.hasGender(original)) {
            String gender = IdentityStorage.getGender(original);
            IdentityStorage.setGender(zombie, gender);
            System.out.println("[MCIdentityMobs] Сохранен пол: " + gender);
        }

        // Сохраняем имя
        if (IdentityStorage.hasName(original)) {
            String name = IdentityStorage.getName(original);
            IdentityStorage.setZombieSavedName(zombie, name);
            System.out.println("[MCIdentityMobs] Сохранено имя: " + name);
        }

        // Сохраняем customName если его дал игрок
        if (original.getCustomName() != null && IdentityStorage.isPlayerNamed(original)) {
            String customName = original.getCustomName().getString();
            if (!customName.isEmpty()) {
                IdentityStorage.setZombieSavedName(zombie, customName);
                System.out.println("[MCIdentityMobs] Сохранено кастомное имя: " + customName);
            }
        }

        // Сохраняем флаг playerNamed
        if (IdentityStorage.isPlayerNamed(original)) {
            IdentityStorage.setPlayerNamed(zombie, true);
        }
    }

    /**
     * Когда зомби лечится - определяем, в кого превращаться
     */
    public static String onCured(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        String zombieId = getEntityId(zombie);
        System.out.println("[MCIdentityMobs] Лечение зомби: " + zombieId +
                ", эффект: " + (activeEffect != null ? ForgeRegistries.MOB_EFFECTS.getKey(activeEffect) : "null") +
                ", предмет: " + usedItem);

        // 1. Проверяем, был ли этот зомби кем-то до заражения
        String originalId = IdentityStorage.getOriginalId(zombie);
        if (originalId != null && !originalId.isEmpty()) {
            System.out.println("[MCIdentityMobs] Восстановление оригинального: " + originalId);
            return originalId;
        }

        // 2. "Дикий" зомби - ищем возможные превращения
        System.out.println("[MCIdentityMobs] Дикий зомби, поиск вариантов...");
        return findCureTargetForWildZombie(zombie, activeEffect, usedItem);
    }

    /**
     * Поиск цели для лечения "дикого" зомби
     */
    private static String findCureTargetForWildZombie(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        String zombieId = getEntityId(zombie);
        List<CureOption> possibleCures = new ArrayList<>();

        // Ищем все записи в canBeInfected где zombie = наш зомби
        for (Map.Entry<String, InfectionData> entry : ConfigManager.CONFIG.canBeInfected.entrySet()) {
            InfectionData data = entry.getValue();
            if (data.zombie.equals(zombieId) && data.curable) {
                possibleCures.add(new CureOption(entry.getKey(), data));
            }
        }

        System.out.println("[MCIdentityMobs] Найдено вариантов лечения: " + possibleCures.size());

        if (possibleCures.isEmpty()) {
            System.out.println("[MCIdentityMobs] Нет подходящих вариантов лечения");
            return null;
        }

        // Если только один вариант
        if (possibleCures.size() == 1) {
            String result = possibleCures.get(0).originalId;
            System.out.println("[MCIdentityMobs] Единственный вариант: " + result);
            return result;
        }

        // Если несколько вариантов - выбираем по условиям лечения
        return selectCureByConditions(zombie, possibleCures, activeEffect, usedItem);
    }

    /**
     * Выбор лечения по условиям (effect/item)
     */
    private static String selectCureByConditions(LivingEntity zombie, List<CureOption> options,
                                                 MobEffect activeEffect, String usedItem) {
        System.out.println("[MCIdentityMobs] Выбор из " + options.size() + " вариантов по условиям");

        // Получаем строковые ID для сравнения
        String activeEffectId = activeEffect != null ?
                ForgeRegistries.MOB_EFFECTS.getKey(activeEffect).toString() : "";

        // Фильтруем по точному совпадению условий
        List<CureOption> matchingOptions = new ArrayList<>();

        for (CureOption option : options) {
            boolean effectMatches = checkEffectMatch(option.data.effect, activeEffectId);
            boolean itemMatches = checkItemMatch(option.data.item, usedItem);

            // Проверяем логику И/ИЛИ
            boolean matches = false;
            if (!option.data.effect.isEmpty() && !option.data.item.isEmpty()) {
                matches = effectMatches && itemMatches; // Нужны оба
            } else if (!option.data.effect.isEmpty()) {
                matches = effectMatches; // Нужен только эффект
            } else if (!option.data.item.isEmpty()) {
                matches = itemMatches; // Нужен только предмет
            } else {
                matches = true; // Ничего не нужно
            }

            if (matches) {
                matchingOptions.add(option);
            }
        }

        System.out.println("[MCIdentityMobs] После фильтрации по условиям: " + matchingOptions.size());

        if (matchingOptions.isEmpty()) {
            // Ничего не подходит по условиям
            return null;
        } else if (matchingOptions.size() == 1) {
            // Один точный вариант
            return matchingOptions.get(0).originalId;
        } else {
            // Несколько вариантов с одинаковыми условиями - рандом
            CureOption selected = matchingOptions.get(new Random().nextInt(matchingOptions.size()));
            System.out.println("[MCIdentityMobs] Одинаковые условия, рандом: " + selected.originalId);
            return selected.originalId;
        }
    }

    private static boolean checkEffectMatch(String requiredEffect, String activeEffectId) {
        if (requiredEffect == null || requiredEffect.isEmpty()) {
            return true; // Эффект не требуется
        }
        return requiredEffect.equals(activeEffectId);
    }

    private static boolean checkItemMatch(String requiredItem, String usedItem) {
        if (requiredItem == null || requiredItem.isEmpty()) {
            return true; // Предмет не требуется
        }
        return requiredItem.equals(usedItem);
    }

    /**
     * После спавна вылеченной сущности
     */
    public static void applyRestoredIdentity(LivingEntity zombie, LivingEntity cured) {
        System.out.println("[MCIdentityMobs] Восстановление идентичности для: " + getEntityId(cured));

        // 1. Восстанавливаем пол
        if (IdentityStorage.hasGender(zombie)) {
            String gender = IdentityStorage.getGender(zombie);
            IdentityStorage.setGender(cured, gender);
            System.out.println("[MCIdentityMobs] Восстановлен пол: " + gender);
        } else {
            // Если у зомби не было пола, назначаем новый
            Gender newGender = GenderService.getOrAssignGender(cured);
            System.out.println("[MCIdentityMobs] Назначен новый пол: " + newGender);
        }

        // 2. Восстанавливаем имя
        String savedName = IdentityStorage.getZombieSavedName(zombie);
        if (savedName != null && !savedName.isEmpty()) {
            IdentityStorage.setName(cured, savedName);

            // Если у зомби было playerNamed или имя было в customName
            boolean hadCustomName = zombie.getCustomName() != null &&
                    !zombie.getCustomName().getString().isEmpty();

            if (IdentityStorage.isPlayerNamed(zombie) || hadCustomName) {
                cured.setCustomName(net.minecraft.network.chat.Component.literal(savedName));
                cured.setCustomNameVisible(true);
                System.out.println("[MCIdentityMobs] Восстановлено имя с CustomName: " + savedName);
            } else {
                System.out.println("[MCIdentityMobs] Восстановлено имя в NBT: " + savedName);
            }
        } else {
            // Генерируем новое имя
            Gender gender = MobIdentityAPI.getGender(cured);
            if (gender != null) {
                NameService.handleName(cured, gender);
            }
        }

        // 3. Восстанавливаем флаг playerNamed
        if (IdentityStorage.isPlayerNamed(zombie)) {
            IdentityStorage.setPlayerNamed(cured, true);
        }
    }

    // ---------------- Вспомогательные классы ----------------

    private static class CureOption {
        final String originalId;
        final InfectionData data;

        CureOption(String originalId, InfectionData data) {
            this.originalId = originalId;
            this.data = data;
        }
    }

    // ---------------- Utils ----------------

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }
}