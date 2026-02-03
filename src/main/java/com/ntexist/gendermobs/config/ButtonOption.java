package com.ntexist.gendermobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ButtonOption extends OptionEntry<Void> {

    private StringWidget labelWidget;
    private Button actionButton;

    private final String buttonTextKey;
    private final Consumer<Button> onPress;

    private Supplier<Boolean> activeCondition = () -> true;

    public ButtonOption(String label, String tooltip, String buttonTextKey, Consumer<Button> onPress) {
        super(label, tooltip, () -> null, v -> {}, null);
        this.buttonTextKey = buttonTextKey;
        this.onPress = onPress;
    }

    public ButtonOption(String label, String buttonTextKey, Consumer<Button> onPress) {
        super(label, null, () -> null, v -> {}, null);
        this.buttonTextKey = buttonTextKey;
        this.onPress = onPress;
    }

    public void setActiveCondition(Supplier<Boolean> condition) {
        this.activeCondition = condition;
    }

    public boolean isActive() {
        return activeCondition.get();
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
        labelWidget.setTooltip(Tooltip.create(Component.translatable(tooltip)));

        actionButton = Button.builder(
                Component.translatable(buttonTextKey),
                btn -> {
                    if (isActive()) {
                        onPress.accept(btn);
                    }
                }
        ).bounds(x + labelWidget.getWidth(), y, width / 2, 20).build();

        actionButton.active = isActive();

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            labelWidget.setTooltip(Tooltip.create(Component.translatable(this.tooltip)));
            actionButton.setTooltip(Tooltip.create(Component.translatable(this.tooltip)));
        }

        widgets.add(labelWidget);
        widgets.add(actionButton);
        return widgets;
    }

    @Override
    public int getHeight() {
        return 24;
    }
}
