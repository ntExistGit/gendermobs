package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class HumanoidService {

    public static boolean isHumanoid(LivingEntity entity) {
        String id = getEntityId(entity);
        if (id == null) return false;

        return ConfigManager.CONFIG.vanillaHumanoid.containsKey(id)
                || ConfigManager.CONFIG.customHumanoid.containsKey(id);
    }

    public static boolean isNonHumanoid(LivingEntity entity) {
        String id = getEntityId(entity);
        if (id == null) return false;

        return ConfigManager.CONFIG.vanillaNonHumanoid.containsKey(id)
                || ConfigManager.CONFIG.customNonHumanoid.containsKey(id);
    }

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }
}
