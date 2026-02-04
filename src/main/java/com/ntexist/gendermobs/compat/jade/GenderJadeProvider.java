package com.ntexist.gendermobs.compat.jade;

import com.ntexist.gendermobs.config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum GenderJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    // Новые теги из нашей системы
    private static final String NBT_GENDER = "GM_Gender";
    private static final String NBT_NAME = "GM_Name";

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            String gender = accessor.getServerData().getString(NBT_GENDER);
            String customName = accessor.getServerData().getString(NBT_NAME);

            String pathBase;
            int colorInt;

            if (gender == null || gender.isEmpty()) {
                pathBase = "unknown";
                colorInt = 0xFFAAAAAA;
            } else if ("Male".equals(gender)) {
                pathBase = "male";
                colorInt = parseHex(ConfigManager.CONFIG.colors.male);
            } else {
                pathBase = "female";
                colorInt = parseHex(ConfigManager.CONFIG.colors.female);
            }

            IElementHelper helper = tooltip.getElementHelper();
            MutableComponent nameText = living.getDisplayName().copy();

            if (customName != null && !customName.isEmpty() &&
                    ConfigManager.CONFIG.general.showNames) {
                nameText = Component.literal(customName);
            }

            if (ConfigManager.CONFIG.general.jadeIcons) {
                tooltip.add(0, new GenderIconElement(pathBase, colorInt));
                tooltip.append(0, helper.text(Component.literal("  ")));
            }

            if (ConfigManager.CONFIG.general.showColors) {
                nameText = nameText.withStyle(style -> style.withColor(colorInt));
            }

            if (ConfigManager.CONFIG.general.jadeIcons) {
                tooltip.append(0, helper.text(nameText));
            } else {
                tooltip.add(0, helper.text(nameText));
            }

            tooltip.remove(ResourceLocation.tryBuild("jade", "object_name"));
        }
    }

    private static class GenderIconElement implements IElement {
        private final ResourceLocation bg;
        private final ResourceLocation fg;
        private final int color;
        private final Vec2 size = new Vec2(11, 11);

        public GenderIconElement(String base, int color) {
            this.bg = ResourceLocation.tryBuild(
                    "gendermobs",
                    "textures/gui/sprites/icon/" + base + "_bg.png"
            );
            this.fg = ResourceLocation.tryBuild(
                    "gendermobs",
                    "textures/gui/sprites/icon/" + base + ".png"
            );
            this.color = color;
        }

        @Override public Vec2 getSize() { return size; }
        @Override public Vec2 getCachedSize() { return size; }
        @Override public IElement size(@Nullable Vec2 v) { return this; }
        @Override public Align getAlignment() { return Align.LEFT; }
        @Override public IElement align(Align v) { return this; }
        @Override public Vec2 getTranslation() { return Vec2.ZERO; }
        @Override public IElement translate(Vec2 v) { return this; }
        @Override public @Nullable ResourceLocation getTag() { return null; }
        @Override public IElement tag(ResourceLocation v) { return this; }
        @Override public @Nullable String getCachedMessage() { return null; }
        @Override public IElement clearCachedMessage() { return this; }
        @Override public IElement message(@Nullable String v) { return this; }

        @Override
        public void render(GuiGraphics context, float x, float y, float width, float height) {
            float finalY = y + ConfigManager.CONFIG.general.offsetY;

            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;

            context.setColor(r, g, b, 1.0F);
            context.blit(
                    bg,
                    (int) x, (int) finalY,
                    0, 0,
                    (int) width, (int) height,
                    (int) width, (int) height
            );

            context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.blit(
                    fg,
                    (int) x, (int) finalY,
                    0, 0,
                    (int) width, (int) height,
                    (int) width, (int) height
            );
        }
    }

    private int parseHex(String hex) {
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            // ARGB формат для Minecraft
            if (hex.length() <= 6) {
                hex = "FF" + hex;
            }
            return (int) Long.parseLong(hex, 16);
        } catch (Exception e) {
            return 0xFFFFFFFF;
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.tryBuild("gendermobs", "gender_info");
    }

    @Override
    public int getDefaultPriority() {
        return 1000;
    }
}