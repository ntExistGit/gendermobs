package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class CustomEditBox extends EditBox {

    private final int padding = 6;
    private final int verticalShift = 6;

    public CustomEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.setBordered(false);
    }

    @Override
    public int getInnerWidth() {
        return this.width - this.padding;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {
            int originalX = this.getX();
            int originalY = this.getY();
            int originalWidth = this.getWidth();

            int bgColor = 0xbf000000;
            guiGraphics.fill(originalX, originalY, originalX + originalWidth, originalY + this.getHeight(), bgColor);

            this.setX(originalX + this.padding);
            this.setY(originalY + this.verticalShift);
            this.setWidth(originalWidth - this.padding);

            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            this.setX(originalX);
            this.setY(originalY);
            this.setWidth(originalWidth);
        }
    }
}