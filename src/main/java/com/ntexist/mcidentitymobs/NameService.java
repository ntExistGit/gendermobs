package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.EntryData;
import com.ntexist.mcidentitymobs.config.NameLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class NameService {

    public static void handleName(LivingEntity entity, Gender gender) {
        if (!shouldProcess(entity)) return;

        String name = generateName(gender);
        IdentityStorage.setName(entity, name);
        applyCustomNameIfNeeded(entity, name);
    }

    private static boolean shouldProcess(LivingEntity entity) {
        return HumanoidService.isHumanoid(entity)
                && !IdentityStorage.isPlayerNamed(entity)
                && !IdentityStorage.hasName(entity);
    }

    private static String generateName(Gender gender) {
        return NameLoader.getRandomName(gender, RandomSource.create());
    }

    private static void applyCustomNameIfNeeded(LivingEntity entity, String name) {
        if (!ConfigManager.CONFIG.general.showNames) return;

        String id = getEntityId(entity);
        if (id == null || !shouldForceName(id)) return;

        entity.setCustomName(Component.literal(name));
        entity.setCustomNameVisible(false);
    }

    private static boolean shouldForceName(String entityId) {
        return getForceFromMap(ConfigManager.CONFIG.vanillaHumanoid, entityId)
                || getForceFromMap(ConfigManager.CONFIG.customHumanoid, entityId);
    }

    private static boolean getForceFromMap(Map<String, ?> map, String id) {
        Object obj = map.get(id);
        return obj instanceof EntryData entry && entry.force;
    }

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }
}