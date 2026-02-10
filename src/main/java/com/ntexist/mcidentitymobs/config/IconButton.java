package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IconButton extends Button {

    public enum IconType {
        TEXTURE, ITEM, EFFECT, ENTITY
    }

    private IconType iconType;
    private ResourceLocation resource;
    private boolean transparent;
    private static final ResourceLocation EMPTY_EGG =
            ResourceLocation.tryBuild("minecraft", "textures/item/spawn_egg.png");

    public IconButton(int x, int y, int w, int h,
                      IconType type,
                      ResourceLocation resource,
                      boolean transparent,
                      Button.OnPress action) {
        super(x, y, w, h, Component.empty(), action, DEFAULT_NARRATION);
        this.iconType = type;
        this.resource = resource;
        this.transparent = transparent;
        updateTooltip();
    }

    public IconButton(int x, int y, int w, int h,
                      ResourceLocation texture,
                      boolean transparent,
                      Button.OnPress action) {
        this(x, y, w, h, IconType.TEXTURE, texture, transparent, action);
    }

    public static IconButton forTexture(int x, int y, int w, int h,
                                        ResourceLocation texture,
                                        boolean transparent,
                                        Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.TEXTURE, texture, transparent, action);
    }

    public static IconButton forItem(int x, int y, int w, int h,
                                     ResourceLocation itemId,
                                     boolean transparent,
                                     Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.ITEM, itemId, transparent, action);
    }

    public static IconButton forEffect(int x, int y, int w, int h,
                                       ResourceLocation effectId,
                                       boolean transparent,
                                       Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.EFFECT, effectId, transparent, action);
    }

    public static IconButton forEntity(int x, int y, int w, int h,
                                       ResourceLocation entityId,
                                       boolean transparent,
                                       Button.OnPress action) {
        return new IconButton(x, y, w, h, IconType.ENTITY, entityId, transparent, action);
    }

    public void updateResource(ResourceLocation newResource) {
        this.resource = newResource;
        updateTooltip();
    }

    public void updateFromId(String id) {
        if (id == null || id.isEmpty()) {
            this.resource = null;
            setTooltip(null);
            return;
        }

        ResourceLocation res = ResourceLocation.tryParse(id);
        if (res != null) {
            this.resource = res;
            updateTooltip();
        }
    }

    public void setIcon(IconType type, String id) {
        this.iconType = type;
        updateFromId(id);
    }

    private void updateTooltip() {
        if (resource == null) {
            setTooltip(null);
            return;
        }

        Component tooltipText = getTooltipForResource();
        if (tooltipText != null) {
            setTooltip(Tooltip.create(tooltipText));
        } else {
            setTooltip(null);
        }
    }

    @Nullable
    private Component getTooltipForResource() {
        if (resource == null) {
            return null;
        }

        switch (iconType) {
            case ITEM:
                Item item = BuiltInRegistries.ITEM.get(resource);
                if (item != null && item != Items.AIR) {
                    return item.getDescription();
                }
                break;

            case EFFECT:
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(resource);
                if (effect != null) {
                    return effect.getDisplayName();
                }
                break;

            case ENTITY:
                Optional<EntityType<?>> entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(resource);
                if (entityTypeOpt.isPresent()) {
                    EntityType<?> entityType = entityTypeOpt.get();
                    return entityType.getDescription();
                }
                break;

            case TEXTURE:
            default:
                // Для текстур не показываем тултип или показываем ID
                return Component.literal(resource.toString());
        }

        return null;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.transparent) {
            int bgColor = 0xbf000000;
            context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
        } else {
            super.renderWidget(context, mouseX, mouseY, delta);
        }

        if (resource == null) return;

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

            case ENTITY:
                var entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(resource);
                if (entityTypeOpt.isPresent()) {
                    EntityType<?> type = entityTypeOpt.get();

                    SpawnEggItem egg = SpawnEggItem.byId(type);

                    if (egg == null) {
                        egg = ForgeSpawnEggItem.fromEntityType(type);
                    }

                    if (egg != null) {
                        context.renderItem(new ItemStack(egg), xPos, yPos);
                    } else {
                        context.blit(EMPTY_EGG, xPos, yPos, 0, 0, 16, 16, 16, 16);
                    }
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