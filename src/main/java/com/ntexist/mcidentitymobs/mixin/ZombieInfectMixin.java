package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.InfectionPipeline;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public class ZombieInfectMixin {

    @Inject(method = "doHurtTarget", at = @At("HEAD"))
    private void im_onInfect(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LivingEntity living) {
            LivingEntity zombie = (LivingEntity)(Object)this;

            // Проверяем, является ли зомби "особенным" (zombie_villager, husk и т.д.)
            String zombieId = getEntityId(zombie);
            String targetId = getEntityId(living);

            System.out.println("[MCIdentityMobs] Попытка заражения: " + targetId + " → " + zombieId);

            // Проверяем, есть ли в canBeInfected запись для этой цели
            boolean canInfect = ConfigManager.CONFIG.canBeInfected.entrySet().stream()
                    .anyMatch(entry -> {
                        // Проверяем что цель может быть заражена
                        if (entry.getKey().equals(targetId)) {
                            // Проверяем что этот зомби правильного типа для заражения
                            return entry.getValue().zombie.equals(zombieId);
                        }
                        return false;
                    });

            if (canInfect) {
                System.out.println("[MCIdentityMobs] Разрешено заражение: " + targetId + " → " + zombieId);
                InfectionPipeline.onInfected(living, zombie);
            } else {
                System.out.println("[MCIdentityMobs] Запрещено заражение: " + targetId + " → " + zombieId);
            }
        }
    }

    private static String getEntityId(Entity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }
}