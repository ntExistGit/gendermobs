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
import java.util.Set;

public class ListRow {

    public static List<AbstractWidget> build(
            int x, int y, int width,
            Set<String> set,
            String entityId,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        String[] currentId = new String[]{entityId};

        EditBox idField = new EditBox(
                Minecraft.getInstance().font,
                x, y, width - 22, 20,
                Component.empty()
        );
        idField.setValue(entityId);

        Runnable validateAndUpdate = () -> {
            String newId = idField.getValue().trim();

            boolean isValidEntity = false;
            ResourceLocation identifier = null;

            if (!newId.isBlank()) {
                try {
                    identifier = ResourceLocation.tryParse(newId);
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
            if (newId.isBlank()) {
                idField.setTextColor(0xAAAAAA);
                tooltipText = Component.translatable("gendermobs.tooltip.entity_id_format").getString();
            } else if (!isValidEntity) {
                idField.setTextColor(0xFF5555);
                if (identifier == null) {
                    tooltipText = Component.translatable("gendermobs.tooltip.incorrect_format").getString();
                } else {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_not_found").getString(),
                            newId
                    );
                }
            } else {
                idField.setTextColor(0xFFFFFF);
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
                if (entityType != null) {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_found").getString(),
                            entityType.getDescription().getString()
                    );
                } else {
                    tooltipText = String.format(
                            Component.translatable("gendermobs.tooltip.entity_found").getString(),
                            newId
                    );
                }
            }
            idField.setTooltip(Tooltip.create(Component.literal(tooltipText)));

            if (isValidEntity && !newId.equals(currentId[0]) && !set.contains(newId)) {
                set.remove(currentId[0]);
                set.add(newId);
                currentId[0] = newId;
            }
        };

        idField.setResponder(newId -> {
            validateAndUpdate.run();
        });

        validateAndUpdate.run();

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    set.remove(currentId[0]);
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        remove.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.remove")));

        w.add(idField);
        w.add(remove);
        return w;
    }

    public static List<AbstractWidget> buildAddRow(
            int x, int y, int width,
            Set<String> set,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        Button add = Button.builder(
                Component.literal("+"),
                b -> {
                    String baseKey = "namespace:entity_id";
                    String key = baseKey;
                    int i = 1;

                    while (set.contains(key)) {
                        key = baseKey + "_" + i++;
                    }

                    set.add(key);
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.add")));

        w.add(add);
        return w;
    }
}