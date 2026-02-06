package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class SpacerOption extends OptionEntry<Void> {
    private final int height;

    public SpacerOption(int height) {
        super("", () -> null, v -> {}, null);
        this.height = height;
    }

    @Override
    public List<AbstractWidget> build(int x, int y, int width) {
        widgets.clear();
        return widgets;
    }

    @Override
    public int getHeight() {
        return height;
    }
}