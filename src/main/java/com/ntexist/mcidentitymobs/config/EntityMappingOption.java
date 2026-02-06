package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class EntityMappingOption extends OptionEntry<Map<String, InfectionData>> {

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
            java.util.function.Supplier<Map<String, InfectionData>> getter,
            java.util.function.Consumer<Map<String, InfectionData>> setter,
            Map<String, InfectionData> def
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
                btn -> onReset()
        );
        reset.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.config.tooltip.reset")));

        widgets.add(toggle);
        widgets.add(labelWidget);
        widgets.add(reset);

        if (!expanded) return widgets;

        int rowY = y + 24;
        Map<String, InfectionData> map = getter.get();

        if (map.isEmpty()) {
            widgets.addAll(EntityMappingRow.buildAddRow(
                    x + 20, rowY, width - 20,
                    map, setter, rebuild
            ));
            return widgets;
        }

        List<Map.Entry<String, InfectionData>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        for (Map.Entry<String, InfectionData> entry : entries) {
            widgets.addAll(EntityMappingRow.build(
                    x + 20, rowY, width - 20,
                    map, setter,
                    entry.getKey(), entry.getValue(), rebuild
            ));
            rowY += 44;
        }

        widgets.addAll(EntityMappingRow.buildAddRow(
                x + 20, rowY, width - 20,
                map, setter, rebuild
        ));
        return widgets;
    }

    @Override
    protected void onReset() {
        Map<String, InfectionData> resetMap = new LinkedHashMap<>();
        for (Map.Entry<String, InfectionData> entry : defaultValue.entrySet()) {
            resetMap.put(entry.getKey(), new InfectionData(entry.getValue()));
        }
        setter.accept(resetMap);
        if (this.rebuild != null) {
            this.rebuild.run();
        }
    }

    @Override
    public int getHeight() {
        if (!expanded) return 24;
        Map<String, InfectionData> map = getter.get();
        int rowCount = Math.max(1, map.size() + 1);
        return 24 + rowCount * 44;
    }
}