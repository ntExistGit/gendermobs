package com.ntexist.mcidentitymobs.client;

import com.ntexist.mcidentitymobs.api.MobIdentityAPI;
import com.ntexist.mcidentitymobs.config.ConfigManager;
import com.ntexist.mcidentitymobs.enums.Gender;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "mcidentitymobs", value = Dist.CLIENT)
public class ClientNameRenderHandler {

    private static final Map<String, Component> COLORED_NAME_CACHE =
            new LinkedHashMap<>(500, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Component> eldest) {
                    return size() > 500;
                }
            };

    @SubscribeEvent
    public static void onRenderName(RenderNameTagEvent event) {
        if (!ConfigManager.CONFIG.general.showColors) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (Minecraft.getInstance().player == null ||
                livingEntity.isInvisibleTo(Minecraft.getInstance().player)) {
            return;
        }

        if (MobIdentityAPI.isPlayerNamed(livingEntity)) {
            Component currentName = event.getContent();
            if (hasColor(currentName)) {
                return;
            }
            return;
        }

        Gender gender = MobIdentityAPI.getGender(livingEntity);
        if (gender == null) return;

        String colorHex = gender == Gender.FEMALE
                ? ConfigManager.CONFIG.colors.female
                : ConfigManager.CONFIG.colors.male;

        if (colorHex == null || colorHex.isEmpty()) return;

        Component original = event.getContent();
        if (original == null || original.getString().trim().isEmpty()) return;

        if (hasColor(original)) {
            return;
        }

        Component coloredName = applyColor(original, colorHex, livingEntity.getId());
        event.setContent(coloredName);
    }

    private static boolean hasColor(Component component) {
        if (component == null) return false;

        Style style = component.getStyle();
        if (style.getColor() != null) {
            return true;
        }

        String nameString = component.getString();
        if (nameString.contains("§") || nameString.contains("&")) {
            return true;
        }

        for (Component sibling : component.getSiblings()) {
            if (hasColor(sibling)) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().level == null) return;

        long gameTime = Minecraft.getInstance().level.getGameTime();
        if (gameTime % 1200 == 0) {
            COLORED_NAME_CACHE.clear();
        }
    }

    @SubscribeEvent
    public static void onEntityJoinClient(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            int entityId = event.getEntity().getId();
            COLORED_NAME_CACHE.entrySet().removeIf(entry ->
                    entry.getKey().startsWith(entityId + ":")
            );
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveClient(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            int entityId = event.getEntity().getId();
            COLORED_NAME_CACHE.entrySet().removeIf(entry ->
                    entry.getKey().startsWith(entityId + ":")
            );
        }
    }

    private static Component applyColor(Component component, String colorHex, int entityId) {
        String text = component.getString();
        String cacheKey = entityId + ":" + text + ":" + colorHex;

        if (COLORED_NAME_CACHE.containsKey(cacheKey)) {
            return COLORED_NAME_CACHE.get(cacheKey);
        }

        Component colored;
        try {
            String cleanHex = colorHex.startsWith("#") ? colorHex.substring(1) : colorHex;
            int color = Integer.parseInt(cleanHex, 16);
            TextColor textColor = TextColor.fromRgb(color);
            colored = createColoredComponent(component, textColor);
        } catch (NumberFormatException e) {
            ChatFormatting formatting = ChatFormatting.getByName(colorHex.toUpperCase());
            if (formatting != null && formatting != ChatFormatting.RESET && formatting.isColor()) {
                TextColor textColor = TextColor.fromLegacyFormat(formatting);
                colored = createColoredComponent(component, textColor);
            } else {
                colored = component;
            }
        }

        COLORED_NAME_CACHE.put(cacheKey, colored);
        return colored;
    }

    private static Component createColoredComponent(Component component, TextColor textColor) {
        return component.copy().withStyle(style -> {
            Style newStyle = style.withColor(textColor);
            if (style.isBold()) newStyle = newStyle.withBold(true);
            if (style.isItalic()) newStyle = newStyle.withItalic(true);
            if (style.isUnderlined()) newStyle = newStyle.withUnderlined(true);
            if (style.isStrikethrough()) newStyle = newStyle.withStrikethrough(true);
            if (style.isObfuscated()) newStyle = newStyle.withObfuscated(true);
            return newStyle;
        });
    }
}