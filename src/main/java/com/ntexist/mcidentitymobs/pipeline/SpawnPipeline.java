package com.ntexist.mcidentitymobs.pipeline;

import com.ntexist.mcidentitymobs.service.ColorService;
import com.ntexist.mcidentitymobs.service.GenderService;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.world.entity.LivingEntity;

public class SpawnPipeline {

    public static void onSpawn(LivingEntity entity) {
        var gender = GenderService.getOrAssignGender(entity);
        if (gender == null) return;

        NameService.handleName(entity, gender);
        ColorService.applyColorIfNeeded(entity, gender);
    }
}
