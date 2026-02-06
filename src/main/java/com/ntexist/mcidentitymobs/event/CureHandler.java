package com.ntexist.mcidentitymobs.event;

import com.ntexist.mcidentitymobs.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CureHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide) return;

        LivingEntity target = (LivingEntity) event.getTarget();
        if (!(target instanceof ZombieVillager zombieVillager)) return; // пока только для зомби-жителей

        ItemStack heldItem = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);

        // Ищем подходящую запись в canBeInfected
        ResourceLocation currentType = EntityType.getKey(target.getType());
        InfectionData data = null;
        String originalId = null;

        for (var entry : ConfigManager.CONFIG.canBeInfected.entrySet()) {
            InfectionData d = entry.getValue();
            if (d.curable && d.zombie.equals(currentType.toString())) {
                // Проверяем предмет (если указан)
                if (!d.item.isEmpty()) {
                    ResourceLocation reqItem = ResourceLocation.tryParse(d.item);
                    if (reqItem == null || !heldItem.is(reqItem)) continue;
                }

                // Проверяем эффект (если указан)
                if (!d.effect.isEmpty()) {
                    ResourceLocation effLoc = ResourceLocation.tryParse(d.effect);
                    if (effLoc == null || !target.hasEffect(BuiltInRegistries.MOB_EFFECT.get(effLoc))) {
                        continue;
                    }
                }

                data = d;
                originalId = MobIdentityAPI.getOriginalId(target);
                break;
            }
        }

        if (data == null || originalId == null || originalId.isEmpty()) return;

        // Условия выполнены — запускаем лечение
        event.setCanceled(true);

        // Уменьшаем предмет (если использовался)
        if (!data.item.isEmpty()) {
            heldItem.shrink(1);
        }

        // Рассчитываем реальное время лечения в зависимости от сложности
        int baseTime = data.time;
        if (baseTime <= 0) baseTime = 3600; // fallback, если 0

        ServerLevel level = (ServerLevel) event.getLevel();
        float minMultiplier, maxMultiplier;

        switch (level.getDifficulty()) {
            case PEACEFUL, EASY -> {
                minMultiplier = 0.5f;
                maxMultiplier = 1.0f;
            }
            case NORMAL -> {
                minMultiplier = 0.75f;
                maxMultiplier = 1.25f;
            }
            case HARD -> {
                minMultiplier = 1.0f;
                maxMultiplier = 1.75f;
            }
            default -> {
                minMultiplier = 1.0f;
                maxMultiplier = 1.0f;
            }
        }

        int conversionTime = (int) (baseTime * (minMultiplier + level.random.nextFloat() * (maxMultiplier - minMultiplier)));

        // Запускаем процесс
        if (target instanceof LivingEntityAccessor acc) {
            acc.mcidentitymobs$setConversionTime(conversionTime);
            acc.mcidentitymobs$setInConversion(true);
        }

        // Звук начала
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}