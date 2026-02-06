package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfectionRulesService {

    /**
     * Можно ли этого зомби лечить вообще
     */
    public static boolean isCurable(LivingEntity zombie) {
        InfectionData data = getInfectionDataForZombie(zombie);
        return data != null && data.curable;
    }

    /**
     * Проверка: выполнены ли условия лечения СЕЙЧАС
     */
    public static boolean canBeCuredNow(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        InfectionData data = getInfectionDataForZombie(zombie);
        if (data == null || !data.curable) return false;

        boolean hasEffect = checkEffect(zombie, data.effect, activeEffect);
        boolean hasItem = checkItem(data.item, usedItem);

        // Логика: если оба заданы → нужны оба
        if (!data.effect.isEmpty() && !data.item.isEmpty())
            return hasEffect && hasItem;

        if (!data.effect.isEmpty())
            return hasEffect;

        if (!data.item.isEmpty())
            return hasItem;

        return false; // если ничего не задано, но curable=true
    }

    private static boolean checkEffect(LivingEntity zombie, String requiredEffect, MobEffect activeEffect) {
        if (requiredEffect == null || requiredEffect.isEmpty()) return true;

        if (activeEffect == null) return false;

        String activeEffectId = ForgeRegistries.MOB_EFFECTS.getKey(activeEffect).toString();
        return requiredEffect.equals(activeEffectId);
    }

    private static boolean checkItem(String requiredItem, String usedItem) {
        if (requiredItem == null || requiredItem.isEmpty()) return true;

        if (usedItem == null || usedItem.isEmpty()) return false;

        return requiredItem.equals(usedItem);
    }

    /**
     * Время лечения
     */
    public static int getCureTime(LivingEntity zombie) {
        InfectionData data = getInfectionDataForZombie(zombie);
        if (data != null && data.curable) {
            return data.time > 0 ? data.time : 0; // Возвращаем время из конфига
        }
        return 0; // По умолчанию
    }

    /**
     * Получить данные инфекции для зомби
     */
    public static InfectionData getInfectionDataForZombie(LivingEntity zombie) {
        String zombieId = getEntityId(zombie);
        if (zombieId == null) return null;

        // Ищем данные где zombie = наш зомби
        for (InfectionData data : ConfigManager.CONFIG.canBeInfected.values()) {
            if (data.zombie.equals(zombieId)) {
                return data;
            }
        }

        return null;
    }

    /**
     * Получить ВСЕ возможные превращения для зомби
     */
    public static List<CureOption> getAllPossibleCures(LivingEntity zombie) {
        String zombieId = getEntityId(zombie);
        List<CureOption> result = new ArrayList<>();

        if (zombieId != null) {
            ConfigManager.CONFIG.canBeInfected.forEach((originalId, data) -> {
                if (data.zombie.equals(zombieId) && data.curable) {
                    result.add(new CureOption(originalId, data));
                }
            });
        }

        return result;
    }

    /**
     * Получить конкретный вариант лечения по условиям
     */
    public static CureOption getCureOptionByConditions(LivingEntity zombie, MobEffect activeEffect, String usedItem) {
        List<CureOption> allOptions = getAllPossibleCures(zombie);

        for (CureOption option : allOptions) {
            boolean effectMatches = checkEffect(zombie, option.data.effect, activeEffect);
            boolean itemMatches = checkItem(option.data.item, usedItem);

            if ((!option.data.effect.isEmpty() && !option.data.item.isEmpty() && effectMatches && itemMatches) ||
                    (!option.data.effect.isEmpty() && option.data.item.isEmpty() && effectMatches) ||
                    (option.data.effect.isEmpty() && !option.data.item.isEmpty() && itemMatches) ||
                    (option.data.effect.isEmpty() && option.data.item.isEmpty())) {
                return option;
            }
        }

        return null;
    }

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }

    public static class CureOption {
        public final String originalId;
        public final InfectionData data;

        public CureOption(String originalId, InfectionData data) {
            this.originalId = originalId;
            this.data = data;
        }
    }
}