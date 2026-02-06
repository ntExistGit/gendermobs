package com.ntexist.mcidentitymobs;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;

public class ColorService {

    public static void applyColorIfNeeded(LivingEntity entity, Gender gender) {
        if (!ConfigManager.CONFIG.general.showColors) return;
        if (entity.getCustomName() == null) return;

        String colorHex = gender == Gender.FEMALE
                ? ConfigManager.CONFIG.colors.female
                : ConfigManager.CONFIG.colors.male;

        if (colorHex == null || colorHex.isEmpty()) return;

        try {
            int color = Integer.parseInt(colorHex.replace("#", ""), 16);
            TextColor textColor = TextColor.fromRgb(color);

            Component colored = entity.getCustomName().copy()
                    .withStyle(Style.EMPTY.withColor(textColor));
            entity.setCustomName(colored);

        } catch (Exception e) {
            ChatFormatting formatting = ChatFormatting.getByName(colorHex);
            if (formatting != null) {
                Component colored = entity.getCustomName().copy().withStyle(formatting);
                entity.setCustomName(colored);
            }
        }
    }
}