package com.ntexist.gendermobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class EntityMappingOption extends OptionEntry<Map<String, String>> {

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

    public EntityMappingOption(
            String label,
            java.util.function.Supplier<Map<String, String>> getter,
            java.util.function.Consumer<Map<String, String>> setter,
            Map<String, String> def
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
                expanded ? "gendermobs.tooltip.expanded.minimize" : "gendermobs.tooltip.expanded.unwrap"
        )));

        labelWidget = new StringWidget(
                x + 20, y, width - 40, 20,
                Component.translatable(label),
                Minecraft.getInstance().font
        );
        labelWidget.alignCenter();

        reset = new IconButton(
                x + toggle.getWidth() + labelWidget.getWidth(), y, 20, 20,
                ResourceLocation.tryBuild("gendermobs", "textures/gui/sprites/icon/reset.png"),
                btn -> onReset()
        );
        reset.setTooltip(Tooltip.create(Component.translatable("gendermobs.config.tooltip.reset")));

        widgets.add(toggle);
        widgets.add(labelWidget);
        widgets.add(reset);

        if (!expanded) return widgets;

        int rowY = y + 24;
        Map<String, String> map = getter.get();

        if (map.isEmpty()) {
            widgets.addAll(EntityMappingRow.buildAddRow(x + 20, rowY, width - 20, map, rebuild));
            return widgets;
        }

        List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        for (Map.Entry<String, String> entry : entries) {
            widgets.addAll(EntityMappingRow.build(
                    x + 20, rowY, width - 20,
                    map, entry.getKey(), entry.getValue(), rebuild
            ));
            rowY += 22;
        }

        widgets.addAll(EntityMappingRow.buildAddRow(x + 20, rowY, width - 20, map, rebuild));
        return widgets;
    }

    @Override
    protected void onReset() {
        setter.accept(new HashMap<>(defaultValue));
        rebuild.run();
    }

    @Override
    public int getHeight() {
        if (!expanded) return 24;
        Map<String, String> map = getter.get();
        int rowCount = Math.max(1, map.size() + 1);
        return 24 + rowCount * 22;
    }
}