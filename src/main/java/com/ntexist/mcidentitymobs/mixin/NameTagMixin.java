package com.ntexist.mcidentitymobs.mixin;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
import com.ntexist.mcidentitymobs.service.IdentityStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public class NameTagMixin {

    @Inject(method = "interactLivingEntity", at = @At("TAIL"))
    private void mi_onNameTag(
            ItemStack stack,
            Player player,
            LivingEntity entity,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!entity.level().isClientSide) {
            // Проверяем что на бирке есть кастомное имя
            if (stack.hasCustomHoverName()) {
                Component nameComponent = stack.getHoverName();
                String plainName = nameComponent.getString();

                // 1. Сохраняем имя в нашем хранилище
                IdentityStorage.setName(entity, plainName);

                // 2. Помечаем что игрок сам назвал моба
                IdentityStorage.setPlayerNamed(entity, true);

                // 3. Синхронизируем с клиентами
                LivingEntityAccessor accessor = (LivingEntityAccessor) entity;
                accessor.mcidentitymobs$setMobName(plainName);
                accessor.mcidentitymobs$setPlayerNamed(true);

                // 4. Также сохраняем информацию о том, что имя уже окрашено игроком
                // Проверяем содержит ли имя цветовые коды
                boolean hasPlayerColors = hasColorCodes(nameComponent);
                if (hasPlayerColors) {
                    System.out.println("[MCIdentityMobs] Player used colored name tag, skipping our colors");
                }

                // Логирование
                System.out.println("[MCIdentityMobs] Player named entity: " +
                        entity.getType().getDescription().getString() +
                        " with name: '" + plainName + "'" +
                        ", has colors: " + hasPlayerColors);
            }
        }
    }

    private boolean hasColorCodes(Component component) {
        if (component == null) return false;

        // Проверяем наличие цвета в стиле
        if (component.getStyle().getColor() != null) {
            return true;
        }

        // Проверяем строку имени на цветовые коды
        String nameString = component.getString();

        // Проверяем символ § (цветовой код Minecraft)
        if (nameString.contains("§")) {
            return true;
        }

        // Проверяем символ & (альтернативный цветовой код)
        if (nameString.contains("&")) {
            return true;
        }

        // Проверяем вложенные компоненты
        for (Component sibling : component.getSiblings()) {
            if (hasColorCodes(sibling)) {
                return true;
            }
        }

        return false;
    }
}