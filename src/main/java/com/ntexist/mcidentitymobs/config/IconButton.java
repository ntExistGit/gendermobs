package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IconButton extends Button {

    public enum IconType {
        TEXTURE, ITEM, EFFECT
    }

    private IconType iconType;
    private ResourceLocation resource;

    public IconButton(int x, int y, int w, int h,
                      IconType type,
                      ResourceLocation resource,
                      Button.OnPress action) {
        super(x, y, w, h, Component.empty(), action, DEFAULT_NARRATION);
        this.iconType = type;
        this.resource = resource;
    }

    public IconButton(int x, int y, int w, int h,
                      ResourceLocation texture,
                      Button.OnPress action) {
        this(x, y, w, h, IconType.TEXTURE, texture, action);
    }

    public static IconButton forTexture(int x, int y, int w, int h,
                                        ResourceLocation texture,
                                        Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.TEXTURE, texture, action);
    }

    public static IconButton forItem(int x, int y, int w, int h,
                                     ResourceLocation itemId,
                                     Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.ITEM, itemId, action);
    }

    public static IconButton forEffect(int x, int y, int w, int h,
                                       ResourceLocation effectId,
                                       Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.EFFECT, effectId, action);
    }

    public void updateResource(ResourceLocation newResource) {
        this.resource = newResource;
    }

    public void updateFromId(String id) {
        if (id == null || id.isEmpty()) {
            this.resource = null;
            return;
        }

        ResourceLocation res = ResourceLocation.tryParse(id);
        if (res != null) {
            this.resource = res;
        }
    }

    public void setIcon(IconType type, String id) {
        this.iconType = type;
        updateFromId(id);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (resource == null) return; // Не рисуем если ресурс не задан

        int iconSize = 16;
        int offset = (this.width - iconSize) / 2;
        int xPos = this.getX() + offset;
        int yPos = this.getY() + offset;

        switch (iconType) {
            case TEXTURE:
                context.blit(resource, xPos, yPos, 0, 0, iconSize, iconSize, iconSize, iconSize);
                break;

            case ITEM:
                Item item = BuiltInRegistries.ITEM.get(resource);
                if (item != null && item != Items.AIR) {
                    ItemStack stack = new ItemStack(item);
                    context.renderItem(stack, xPos, yPos);
                }
                break;

            case EFFECT:
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(resource);
                if (effect != null) {
                    renderEffectIcon(context, effect, xPos, yPos, iconSize);
                }
                break;
        }
    }

    private void renderEffectIcon(GuiGraphics context, MobEffect effect, int x, int y, int size) {
        Minecraft minecraft = Minecraft.getInstance();
        MobEffectTextureManager textureManager = minecraft.getMobEffectTextures();

        TextureAtlasSprite sprite = textureManager.get(effect);
        if (sprite != null) {
            context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.blit(x + (size - 18) / 2, y + (size - 18) / 2, 0, 18, 18, sprite);
        }
    }
}