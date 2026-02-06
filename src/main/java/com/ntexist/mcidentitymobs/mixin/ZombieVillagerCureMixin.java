package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.MobIdentityAPI;
import com.ntexist.mcidentitymobs.ZombieCureHandler;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.config.InfectionData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ZombieVillager.class)
public class ZombieVillagerCureMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void im_onCureAttempt(Player player, InteractionHand hand,
                                  CallbackInfoReturnable<InteractionResult> cir) {
        ZombieVillager zombie = (ZombieVillager)(Object)this;
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.isEmpty()) {
            return;
        }

        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (itemKey == null) {
            return;
        }

        String itemId = itemKey.toString();
        System.out.println("[MCIdentityMobs] Попытка лечения предметом: " + itemId);

        if (!MobIdentityAPI.isCurable(zombie)) {
            System.out.println("[MCIdentityMobs] Этот зомби неизлечим");
            return;
        }

        String zombieId = getEntityId(zombie);
        if (zombieId == null) {
            return;
        }

        // Ищем подходящее правило лечения
        InfectionData cureRule = findCureRule(zombieId, itemId);
        if (cureRule == null) {
            System.out.println("[MCIdentityMobs] Предмет " + itemId + " не используется для лечения этого зомби");
            return;
        }

        System.out.println("[MCIdentityMobs] Найдено правило лечения");

        // Проверяем эффект
        MobEffect requiredEffect = getEffectById(cureRule.effect);
        if (requiredEffect != null) {
            MobEffectInstance effectInstance = zombie.getEffect(requiredEffect);
            boolean hasRequiredEffect = effectInstance != null;
            System.out.println("[MCIdentityMobs] Требуется эффект " + cureRule.effect + ": " + (hasRequiredEffect ? "ЕСТЬ" : "НЕТ"));

            if (!hasRequiredEffect) {
                System.out.println("[MCIdentityMobs] Лечение невозможно: требуется эффект " + cureRule.effect);
                return;
            }
        } else if (cureRule.effect != null && !cureRule.effect.isEmpty()) {
            System.out.println("[MCIdentityMobs] Эффект не найден в регистре: " + cureRule.effect);
            return;
        }

        // Проверяем условия
        if (!checkCureConditions(cureRule, itemId)) {
            System.out.println("[MCIdentityMobs] Условия лечения не выполнены");
            return;
        }

        System.out.println("[MCIdentityMobs] Условия лечения выполнены!");

        // Определяем цель лечения
        String targetId = MobIdentityAPI.onCured(zombie, requiredEffect, itemId);
        if (targetId == null) {
            System.out.println("[MCIdentityMobs] Не удалось определить цель лечения");
            return;
        }

        System.out.println("[MCIdentityMobs] Будет превращен в: " + targetId);

        // Запускаем лечение
        ZombieCureHandler.startCure(zombie, requiredEffect, itemId, targetId);

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        startCureProcess(zombie, player.getUUID(), cureRule.time);

        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }

    @Unique
    private InfectionData findCureRule(String zombieId, String itemId) {
        // Ищем правило где zombie совпадает и item совпадает
        for (InfectionData rule : ConfigManager.CONFIG.canBeInfected.values()) {
            if (rule.zombie.equals(zombieId) && rule.curable) {
                // Проверяем предмет
                if ((rule.item != null && rule.item.equals(itemId)) ||
                        (rule.item == null || rule.item.isEmpty())) {
                    return rule;
                }
            }
        }
        return null;
    }

    @Unique
    private boolean checkCureConditions(InfectionData rule, String usedItemId) {
        boolean effectRequired = rule.effect != null && !rule.effect.isEmpty();
        boolean itemRequired = rule.item != null && !rule.item.isEmpty();

        if (effectRequired && itemRequired) {
            // Нужны и эффект и предмет
            return rule.item.equals(usedItemId);
        } else if (effectRequired) {
            // Нужен только эффект (уже проверен выше)
            return true;
        } else if (itemRequired) {
            // Нужен только предмет
            return rule.item.equals(usedItemId);
        } else {
            // Ничего не требуется
            return true;
        }
    }

    @Unique
    private MobEffect getEffectById(String effectId) {
        if (effectId == null || effectId.isEmpty()) {
            return null;
        }

        try {
            ResourceLocation effectKey = ResourceLocation.tryParse(effectId);
            if (effectKey != null) {
                return ForgeRegistries.MOB_EFFECTS.getValue(effectKey);
            }
        } catch (Exception e) {
            System.err.println("[MCIdentityMobs] Ошибка парсинга эффекта: " + effectId);
        }

        return null;
    }

    @Unique
    private String getEntityId(LivingEntity entity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : null;
    }

    @Unique
    private void startCureProcess(ZombieVillager zombie, UUID playerUUID, int cureTime) {
        if (cureTime <= 0) {
            cureTime = zombie.getRandom().nextInt(2401) + 3600;
            System.out.println("[MCIdentityMobs] Используем ванильное время лечения: " + cureTime + " тиков");
        } else {
            System.out.println("[MCIdentityMobs] Используем время из конфига: " + cureTime + " тиков");
        }

        try {
            ZombieVillagerAccessorMixin accessor = (ZombieVillagerAccessorMixin) zombie;
            accessor.callStartConverting(playerUUID, cureTime);
            System.out.println("[MCIdentityMobs] Лечение успешно запущено");
        } catch (Exception e) {
            System.err.println("[MCIdentityMobs] Ошибка запуска лечения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}