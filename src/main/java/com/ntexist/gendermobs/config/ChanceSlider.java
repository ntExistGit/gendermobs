package com.ntexist.gendermobs.config;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ChanceSlider extends AbstractSliderButton {

    private final Consumer<Float> consumer;

    public ChanceSlider(int x, int y, int width, float value, Consumer<Float> consumer) {
        super(x, y, width, 20, Component.empty(), value);
        this.consumer = consumer;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.literal(String.format("%.2f", value)));
    }

    @Override
    protected void applyValue() {
        float v = Math.round(value * 100f) / 100f;
        consumer.accept(v);
    }
}
