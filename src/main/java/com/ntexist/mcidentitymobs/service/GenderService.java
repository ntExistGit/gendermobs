package com.ntexist.mcidentitymobs.service;

import com.ntexist.mcidentitymobs.Gender;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.EntryData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenderService {

    public static Gender getOrAssignGender(LivingEntity entity) {
        if (IdentityStorage.hasGender(entity)) {
            String genderStr = IdentityStorage.getGender(entity);
            return Gender.fromString(genderStr);
        }

        String id = getEntityId(entity);
        if (id == null) return null;

        Double chance = getChance(id);
        if (chance == null) return null;

        Gender gender = rollGender(chance);
        IdentityStorage.setGender(entity, gender.name().toLowerCase());

        return gender;
    }

    private static Gender rollGender(double chance) {
        return Math.random() <= chance ? Gender.FEMALE : Gender.MALE;
    }

    private static Double getChance(String id) {
        List<Map<String, EntryData>> mapsToCheck = Arrays.asList(
                ConfigManager.CONFIG.vanillaHumanoid,
                ConfigManager.CONFIG.customHumanoid,
                ConfigManager.CONFIG.vanillaNonHumanoid,
                ConfigManager.CONFIG.customNonHumanoid
        );

        for (Map<String, EntryData> map : mapsToCheck) {
            Double chance = getChanceFromMap(map, id);
            if (chance != null) {
                return chance;
            }
        }

        return null;
    }

    private static Double getChanceFromMap(Map<String, ?> map, String id) {
        Object obj = map.get(id);
        if (!(obj instanceof EntryData entry)) {
            return null;
        }
        return (double) entry.chance;
    }

    private static String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }
}