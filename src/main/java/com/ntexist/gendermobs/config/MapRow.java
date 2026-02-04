package com.ntexist.gendermobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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

        EditBox id = new EditBox(
                Minecraft.getInstance().font,
                x, y, width / 2 - 2, 20,
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
                        if (entityType != null && entityType != EntityType.PIG) {
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
                tooltipText = Component.translatable("gendermobs.tooltip.entity_id_format").getString();
            } else if (!isValidEntity) {
                id.setTextColor(0xFF5555);
                if (identifier == null) {
                    tooltipText = Component.translatable("gendermobs.tooltip.incorrect_format").getString();
                } else {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_not_found").getString(),
                            newKey
                    );
                }
            } else {
                id.setTextColor(0xFFFFFF);
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
                if (entityType != null) {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_found").getString(),
                            entityType.getDescription().getString()
                    );
                } else {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_found").getString(),
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
                x + id.getWidth() + 2, y, width / 4,
                data.chance,
                v -> data.chance = v
        );
        slider.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.change_slider")));

        Button force = Button.builder(
                Component.translatable(data.force ? "gendermobs.boolean.true" : "gendermobs.boolean.false"),
                b -> {
                    data.force = !data.force;
                    b.setMessage(Component.translatable(data.force ? "gendermobs.boolean.true" : "gendermobs.boolean.false"));
                }
        ).bounds(x + id.getWidth() + slider.getWidth() + 2, y, width / 4 - 20, 20).build();
        force.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.force_name")));

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    map.remove(currentKey[0]);
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        remove.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.remove")));

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
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.add")));

        w.add(add);
        return w;
    }
}