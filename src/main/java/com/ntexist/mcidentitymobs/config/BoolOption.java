package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BoolOption extends OptionEntry<Boolean> {

    private StringWidget labelWidget;
    private Button toggle;
    private IconButton reset;

    private Supplier<Boolean> activeCondition = () -> true;

    public void setActiveCondition(Supplier<Boolean> condition) {
        this.activeCondition = condition;
    }

    public boolean isActive() {
        return activeCondition.get();
    }

    public BoolOption(String label, String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean def) {
        super(label, tooltip, getter, setter, def);
    }

    public BoolOption(String label, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean def) {
        super(label, null, getter, setter, def);
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

        toggle = Button.builder(
                Component.translatable(getter.get() ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"),
                btn -> {
                    boolean newVal = !getter.get();
                    setter.accept(newVal);
                    btn.setMessage(Component.translatable(newVal ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"));
                }
        ).bounds(x + labelWidget.getWidth(), y, width / 2 - 20, 20).build();

        reset = new IconButton(
                x + labelWidget.getWidth() + toggle.getWidth(), y, 20, 20,
                ResourceLocation.tryBuild("mcidentitymobs", "textures/gui/sprites/icon/reset.png"),
                btn -> onReset()
        );
        reset.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.config.tooltip.reset")));

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            Tooltip tooltipObj = Tooltip.create(Component.translatable(this.tooltip));
            labelWidget.setTooltip(tooltipObj);
            toggle.setTooltip(tooltipObj);
        }

        widgets.add(labelWidget);
        widgets.add(toggle);
        widgets.add(reset);
        return widgets;
    }

    @Override
    protected void onReset() {
        setter.accept(defaultValue);
        toggle.setMessage(Component.translatable(defaultValue ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"));
    }

    @Override
    public int getHeight() {
        return 24;
    }
}