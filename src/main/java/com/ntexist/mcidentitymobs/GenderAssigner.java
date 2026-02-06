package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.service.ColorService;
import com.ntexist.mcidentitymobs.service.GenderService;
import com.ntexist.mcidentitymobs.service.NameService;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GenderAssigner {

    public static void handleEntity(LivingEntity entity) {
        Gender gender = GenderService.getOrAssignGender(entity);
        if (gender == null) return;

        NameService.handleName(entity, gender);
        ColorService.applyColorIfNeeded(entity, gender);
    }
}
