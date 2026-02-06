package com.ntexist.mcidentitymobs;

import net.minecraft.world.entity.LivingEntity;

public class SpawnPipeline {

    public static void onSpawn(LivingEntity entity) {
        var gender = GenderService.getOrAssignGender(entity);
        if (gender == null) return;

        NameService.handleName(entity, gender);
        ColorService.applyColorIfNeeded(entity, gender);
    }
}
