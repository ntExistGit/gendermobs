package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {

    private final ResourceLocation icon;

    public IconButton(int x, int y, int w, int h, ResourceLocation icon, Button.OnPress action) {
        super(x, y, w, h, Component.empty(), action, DEFAULT_NARRATION);
        this.icon = icon;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        int iconSize = 16;
        int offset = (this.width - iconSize) / 2;

        context.blit(
                icon,
                this.getX() + offset,
                this.getY() + offset,
                0, 0,
                iconSize, iconSize,
                iconSize, iconSize
        );
    }
}