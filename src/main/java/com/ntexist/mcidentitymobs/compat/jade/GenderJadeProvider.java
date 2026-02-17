package com.ntexist.mcidentitymobs.compat.jade;

import com.ntexist.mcidentitymobs.config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import javax.annotation.Nullable;

public enum GenderJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    private static final ResourceLocation ICON_MALE_BG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/male_bg.png");
    private static final ResourceLocation ICON_MALE_FG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/male.png");
    private static final ResourceLocation ICON_FEMALE_BG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/female_bg.png");
    private static final ResourceLocation ICON_FEMALE_FG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/female.png");
    private static final ResourceLocation ICON_UNKNOWN_BG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/unknown_bg.png");
    private static final ResourceLocation ICON_UNKNOWN_FG = ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "textures/gui/sprites/icon/unknown.png");

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof LivingEntity living)) return;

        String gender = accessor.getServerData().getString("MI_Gender");
        int convTime = accessor.getServerData().getInt("MI_ConversionTime");

        ResourceLocation bgIcon, fgIcon;
        int colorInt;

        String genderLower = gender.toLowerCase();
        if (gender.isEmpty() || "unknown".equals(genderLower)) {
            bgIcon = ICON_UNKNOWN_BG;
            fgIcon = ICON_UNKNOWN_FG;
            colorInt = 0xFFAAAAAA;
        } else if ("male".equals(genderLower)) {
            bgIcon = ICON_MALE_BG;
            fgIcon = ICON_MALE_FG;
            colorInt = parseHex(ConfigManager.CONFIG.colors.male);
        } else {
            bgIcon = ICON_FEMALE_BG;
            fgIcon = ICON_FEMALE_FG;
            colorInt = parseHex(ConfigManager.CONFIG.colors.female);
        }

        IElementHelper helper = tooltip.getElementHelper();

        MutableComponent nameText = living.getDisplayName().copy();

        if (ConfigManager.CONFIG.jade.jadeIcons) {
            boolean showIcon = true;
            if (gender.isEmpty() || "unknown".equals(genderLower)) {
                showIcon = ConfigManager.CONFIG.jade.jadeIconUnknown;
            }
            if (showIcon) {
                tooltip.add(0, new GenderIconElement(bgIcon, fgIcon, colorInt));
                tooltip.append(0, helper.text(Component.literal("  ")));
            }
        }

        if (ConfigManager.CONFIG.general.showColors) {
            nameText = nameText.withStyle(style -> style.withColor(colorInt));
        }

        if (ConfigManager.CONFIG.jade.jadeIcons) {
            tooltip.append(0, helper.text(nameText));
        } else {
            tooltip.add(0, helper.text(nameText));
        }

        if (ConfigManager.CONFIG.jade.conversionTime) {
            if (convTime > 0) {
                int seconds = convTime / 20;
                Component cureText = Component.translatable("mcidentitymobs.config.jade.zombieConversion.time", seconds);
                tooltip.add(helper.text(cureText));
            }
        }

        tooltip.remove(ResourceLocation.fromNamespaceAndPath("jade", "object_name"));
    }

    private static class GenderIconElement implements IElement {
        private final ResourceLocation bg;
        private final ResourceLocation fg;
        private final int color;
        private final Vec2 size = new Vec2(11, 11);

        public GenderIconElement(ResourceLocation bg, ResourceLocation fg, int color) {
            this.bg = bg;
            this.fg = fg;
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
            float finalY = y + ConfigManager.CONFIG.jade.offsetY;

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
        return ResourceLocation.fromNamespaceAndPath("mcidentitymobs", "gender_info");
    }

    @Override
    public int getDefaultPriority() {
        return 1000;
    }
}