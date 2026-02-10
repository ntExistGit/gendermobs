package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderOption extends OptionEntry<Float> {

    private final float min;
    private final float max;
    private final float step;

    private StringWidget labelWidget;
    private Slider slider;
    private IconButton reset;

    public SliderOption(
            String label,
            String tooltip,
            float min,
            float max,
            float step,
            Supplier<Float> getter,
            Consumer<Float> setter,
            float def
    ) {
        super(label, tooltip, getter, setter, def);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public List<AbstractWidget> build(int x, int y, int width) {
        widgets.clear();

        labelWidget = new StringWidget(
                x, y, width / 2, 20,
                Component.translatable(label),
                Minecraft.getInstance().font
        );
        labelWidget.alignLeft();

        slider = new Slider(
                x + labelWidget.getWidth(),
                y,
                width / 2 - 20,
                20,
                getter.get()
        );

        reset = new IconButton(
                x + labelWidget.getWidth() + slider.getWidth(),
                y, 20, 20,
                ResourceLocation.tryBuild("mcidentitymobs", "textures/gui/sprites/icon/reset.png"),
                false,
                btn -> onReset()
        );

        reset.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.config.tooltip.reset")));

        if (tooltip != null && !tooltip.isEmpty()) {
            Tooltip tooltipObj = Tooltip.create(Component.translatable(tooltip));
            labelWidget.setTooltip(tooltipObj);
            slider.setTooltip(tooltipObj);
        }

        widgets.add(labelWidget);
        widgets.add(slider);
        widgets.add(reset);
        return widgets;
    }

    @Override
    protected void onReset() {
        setter.accept(defaultValue);
        slider.setRealValue(defaultValue);
    }

    @Override
    public int getHeight() {
        return 24;
    }

    private float denormalize(double value) {
        float v = (float) (min + (max - min) * value);
        return snapToStep(v);
    }

    private double normalize(float value) {
        return (value - min) / (max - min);
    }

    private float snapToStep(float value) {
        float snapped = Math.round(value / step) * step;
        return Math.min(max, Math.max(min, snapped));
    }

    private DecimalFormat getFormat() {
        int decimals = Math.max(0, countDecimals(step));
        StringBuilder pattern = new StringBuilder("0");
        if (decimals > 0) {
            pattern.append(".");
            pattern.append("0".repeat(decimals));
        }
        return new DecimalFormat(pattern.toString());
    }

    private int countDecimals(float value) {
        String text = Float.toString(value);
        int index = text.indexOf('.');
        if (index < 0) return 0;
        return text.length() - index - 1;
    }

    private class Slider extends AbstractSliderButton {

        private final DecimalFormat format = getFormat();

        public Slider(int x, int y, int w, int h, float current) {
            super(x, y, w, h, Component.empty(), normalize(current));
            updateMessage();
        }

        public void setRealValue(float real) {
            this.value = normalize(real);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            float realValue = denormalize(this.value);
            this.setMessage(Component.literal(format.format(realValue)));
        }

        @Override
        protected void applyValue() {
            float realValue = denormalize(this.value);
            setter.accept(realValue);
        }
    }
}