package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ListOption extends OptionEntry<Set<String>> {

    private boolean expanded = true;
    private StringWidget labelWidget;
    private Button toggle;
    private IconButton reset;
    protected Runnable rebuild;

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ListOption(
            String label,
            java.util.function.Supplier<Set<String>> getter,
            java.util.function.Consumer<Set<String>> setter,
            Set<String> def
    ) {
        super(label, getter, setter, def);
    }

    public void attachRebuild(Runnable rebuild) {
        this.rebuild = rebuild;
    }

    @Override
    public List<AbstractWidget> build(int x, int y, int width) {
        widgets.clear();

        toggle = Button.builder(
                Component.literal(expanded ? "▼" : "▶"),
                b -> {
                    expanded = !expanded;
                    rebuild.run();
                }
        ).bounds(x, y, 20, 20).build();
        toggle.setTooltip(Tooltip.create(Component.translatable(
                expanded ? "mcidentitymobs.tooltip.expanded.minimize" : "mcidentitymobs.tooltip.expanded.unwrap"
        )));

        labelWidget = new StringWidget(
                x + 20, y, width - 40, 20,
                Component.translatable(label),
                Minecraft.getInstance().font
        );
        labelWidget.alignCenter();

        reset = new IconButton(
                x + toggle.getWidth() + labelWidget.getWidth(), y, 20, 20,
                ResourceLocation.tryBuild("mcidentitymobs", "textures/gui/sprites/icon/reset.png"),
                false,
                btn -> onReset()
        );
        reset.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.config.tooltip.reset")));

        widgets.add(toggle);
        widgets.add(labelWidget);
        widgets.add(reset);

        if (!expanded) return widgets;

        int rowY = y + 24;
        Set<String> set = getter.get();

        if (set.isEmpty()) {
            widgets.addAll(ListRow.buildAddRow(x + 20, rowY, width - 20, set, rebuild));
            return widgets;
        }

        for (String entityId : new ArrayList<>(set)) {
            widgets.addAll(ListRow.build(x + 20, rowY, width - 20, set, entityId, rebuild));
            rowY += 22;
        }

        widgets.addAll(ListRow.buildAddRow(x + 20, rowY, width - 20, set, rebuild));
        return widgets;
    }

    @Override
    protected void onReset() {
        setter.accept(new LinkedHashSet<>(defaultValue));
        if (this.rebuild != null) {
            this.rebuild.run();
        }
    }

    @Override
    public int getHeight() {
        if (!expanded) return 24;
        return 24 + Math.max(1, getter.get().size() + 1) * 22;
    }
}
