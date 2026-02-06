package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapRow {

    public static List<AbstractWidget> build(
            int x, int y, int width,
            Map<String, EntryData> map,
            String key,
            EntryData data,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        String[] currentKey = new String[]{key};

        final int GAP = 2;
        final int REMOVE_W = 20;
        final int FORCE_W = 55;
        final int SLIDER_MIN_W = 90;

        int sliderW = Math.max(SLIDER_MIN_W, width / 4);
        int idW = width - sliderW - FORCE_W - REMOVE_W - GAP;

        if (idW < 100) {
            idW = 100;
            sliderW = width - idW - FORCE_W - REMOVE_W - GAP;
            if (sliderW < 60) sliderW = 60;
        }

        CustomEditBox id = new CustomEditBox(
                Minecraft.getInstance().font,
                x, y, idW, 20,
                Component.empty()
        );
        id.setMaxLength(32767);
        id.setValue(key);

        Runnable validateAndUpdate = () -> {
            String newKey = id.getValue().trim();

            boolean isValidEntity = false;
            ResourceLocation identifier = null;

            if (!newKey.isBlank()) {
                try {
                    identifier = ResourceLocation.tryParse(newKey);
                    if (identifier != null) {
                        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
                        if (entityType != null) {
                            ResourceLocation registeredId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                            if (registeredId != null && registeredId.equals(identifier)) {
                                isValidEntity = true;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    isValidEntity = false;
                }
            }

            String tooltipText;
            if (newKey.isBlank()) {
                id.setTextColor(0xAAAAAA);
                tooltipText = Component.translatable("mcidentitymobs.tooltip.entity_id_format").getString();
            } else if (!isValidEntity) {
                id.setTextColor(0xFF5555);
                if (identifier == null) {
                    tooltipText = Component.translatable("mcidentitymobs.tooltip.entity_incorrect_format").getString();
                } else {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_not_found").getString(),
                            newKey
                    );
                }
            } else {
                id.setTextColor(0xFFFFFF);
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
                if (entityType != null) {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_found").getString(),
                            entityType.getDescription().getString()
                    );
                } else {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_found").getString(),
                            newKey
                    );
                }
            }
            id.setTooltip(Tooltip.create(Component.literal(tooltipText)));

            if (isValidEntity && !newKey.equals(currentKey[0]) && !map.containsKey(newKey)) {
                map.remove(currentKey[0]);
                map.put(newKey, data);
                currentKey[0] = newKey;
            }
        };

        id.setResponder(newKey -> {
            validateAndUpdate.run();
        });

        validateAndUpdate.run();

        ChanceSlider slider = new ChanceSlider(
                x + idW + GAP, y, sliderW,
                data.chance,
                v -> data.chance = v
        );
        slider.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.change_slider")));

        Button force = Button.builder(
                Component.translatable(data.force ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"),
                b -> {
                    data.force = !data.force;
                    b.setMessage(Component.translatable(data.force ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"));
                }
        ).bounds(x + idW + GAP + sliderW, y, FORCE_W, 20).build();
        force.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.force_name")));

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    map.remove(currentKey[0]);
                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - REMOVE_W, y, REMOVE_W, 20).build();
        remove.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.remove")));

        w.add(id);
        w.add(slider);
        w.add(force);
        w.add(remove);
        return w;
    }

    public static List<AbstractWidget> buildAddRow(
            int x, int y, int width,
            Map<String, EntryData> map,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        Button add = Button.builder(
                Component.literal("+"),
                b -> {
                    String baseKey = "namespace:entity_id";
                    String key = baseKey;
                    int i = 1;

                    while (map.containsKey(key)) {
                        key = baseKey + "_" + i++;
                    }

                    map.put(key, new EntryData(0.5f, true));
                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.add")));

        w.add(add);
        return w;
    }
}