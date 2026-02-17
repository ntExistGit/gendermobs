package com.ntexist.mcidentitymobs.client;

import com.ntexist.mcidentitymobs.accessor.LivingEntityAccessor;
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

    // LRU кэш для окрашенных имен (максимум 500 записей)
    private static final Map<String, Component> COLORED_NAME_CACHE =
            new LinkedHashMap<>(500, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Component> eldest) {
                    return size() > 500;
                }
            };

    @SubscribeEvent
    public static void onRenderName(RenderNameTagEvent event) {
        // Быстрая проверка - выходим если окраска отключена в конфиге
        if (!ConfigManager.CONFIG.general.showColors) return;

        // Проверяем что entity - живое существо
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        // Проверяем что игрок существует и entity видим
        if (Minecraft.getInstance().player == null ||
                livingEntity.isInvisibleTo(Minecraft.getInstance().player)) {
            return;
        }

        // Проверяем что entity поддерживает наш Accessor
        if (!(livingEntity instanceof LivingEntityAccessor accessor)) return;

        // ВАЖНО: Если игрок сам назвал моба через бирку - НЕ применяем наши цвета
        // Игрок мог уже покрасить имя через цветовые коды в бирке
        if (accessor.mcidentitymobs$isPlayerNamed()) {
            // Проверяем, не покрасил ли уже игрок имя через цветовые коды
            Component currentName = event.getContent();
            if (hasColor(currentName)) {
                // Игрок уже покрасил имя - оставляем как есть
                return;
            }
            // Игрок дал простое имя без цвета - тоже НЕ красим
            return;
        }

        // Получаем пол существа
        String genderStr = accessor.mcidentitymobs$getGender();
        if (genderStr == null || genderStr.isEmpty()) return;

        // Конвертируем строку в Gender enum
        Gender gender = Gender.fromString(genderStr);
        if (gender == null) return;

        // Получаем цвет из конфига
        String colorHex = gender == Gender.FEMALE
                ? ConfigManager.CONFIG.colors.female
                : ConfigManager.CONFIG.colors.male;

        if (colorHex == null || colorHex.isEmpty()) return;

        // Получаем текущее имя
        Component original = event.getContent();
        if (original == null || original.getString().trim().isEmpty()) return;

        // Проверяем не окрашено ли имя уже (на всякий случай)
        if (hasColor(original)) {
            return;
        }

        // Применяем цвет и обновляем отображаемое имя
        Component coloredName = applyColor(original, colorHex, livingEntity.getId());
        event.setContent(coloredName);
    }

    /**
     * Проверяет содержит ли компонент цветовое форматирование
     */
    private static boolean hasColor(Component component) {
        if (component == null) return false;

        // Проверяем основной стиль на наличие цвета
        Style style = component.getStyle();
        if (style.getColor() != null) {
            return true;
        }

        // В Forge 1.20.1 нет простого способа получить ChatFormatting из Style
        // Но можно проверить строку имени на наличие цветовых кодов
        String nameString = component.getString();

        // Проверяем на символ § (цветовой код Minecraft)
        if (nameString.contains("§")) {
            return true;
        }

        // Проверяем на символ & (альтернативный цветовой код)
        if (nameString.contains("&")) {
            return true;
        }

        // Рекурсивно проверяем вложенные компоненты
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

        // Периодически очищаем кэш каждую минуту (1200 тиков)
        long gameTime = Minecraft.getInstance().level.getGameTime();
        if (gameTime % 1200 == 0) {
            int before = COLORED_NAME_CACHE.size();
            COLORED_NAME_CACHE.clear();
            if (before > 0) {
                // Можно добавить логирование если нужно
                // System.out.println("Cleared name color cache: " + before + " entries");
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinClient(EntityJoinLevelEvent event) {
        // При появлении entity на клиенте очищаем старые записи в кэше
        if (event.getLevel().isClientSide()) {
            int entityId = event.getEntity().getId();
            COLORED_NAME_CACHE.entrySet().removeIf(entry ->
                    entry.getKey().startsWith(entityId + ":")
            );
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveClient(EntityLeaveLevelEvent event) {
        // При удалении entity с клиента очищаем связанные записи в кэше
        if (event.getLevel().isClientSide()) {
            int entityId = event.getEntity().getId();
            COLORED_NAME_CACHE.entrySet().removeIf(entry ->
                    entry.getKey().startsWith(entityId + ":")
            );
        }
    }

    /**
     * Применяет цвет к компоненту с кэшированием
     */
    private static Component applyColor(Component component, String colorHex, int entityId) {
        // Создаем уникальный ключ для кэша: entityId:текст:цвет
        String text = component.getString();
        String cacheKey = entityId + ":" + text + ":" + colorHex;

        // Проверяем кэш
        if (COLORED_NAME_CACHE.containsKey(cacheKey)) {
            return COLORED_NAME_CACHE.get(cacheKey);
        }

        Component colored;
        try {
            // Пробуем распарсить HEX цвет
            String cleanHex = colorHex.startsWith("#") ? colorHex.substring(1) : colorHex;
            int color = Integer.parseInt(cleanHex, 16);
            TextColor textColor = TextColor.fromRgb(color);

            // Создаем окрашенный компонент
            colored = createColoredComponent(component, textColor);

        } catch (NumberFormatException e) {
            // Если не HEX, пробуем ChatFormatting
            ChatFormatting formatting = ChatFormatting.getByName(colorHex.toUpperCase());
            if (formatting != null && formatting != ChatFormatting.RESET && formatting.isColor()) {
                // Конвертируем ChatFormatting в TextColor
                TextColor textColor = TextColor.fromLegacyFormat(formatting);
                colored = createColoredComponent(component, textColor);
            } else {
                // Не удалось применить цвет - возвращаем оригинал
                colored = component;
            }
        }

        // Сохраняем в кэш
        COLORED_NAME_CACHE.put(cacheKey, colored);
        return colored;
    }

    private static Component createColoredComponent(Component component, TextColor textColor) {
        return component.copy().withStyle(style -> {
            // Создаем новый стиль с цветом
            Style newStyle = style.withColor(textColor);

            // Сохраняем существующие форматирования
            // (isBold(), isItalic() и т.д. возвращают boolean, так что проверяем их)
            if (style.isBold()) newStyle = newStyle.withBold(true);
            if (style.isItalic()) newStyle = newStyle.withItalic(true);
            if (style.isUnderlined()) newStyle = newStyle.withUnderlined(true);
            if (style.isStrikethrough()) newStyle = newStyle.withStrikethrough(true);
            if (style.isObfuscated()) newStyle = newStyle.withObfuscated(true);

            return newStyle;
        });
    }
}