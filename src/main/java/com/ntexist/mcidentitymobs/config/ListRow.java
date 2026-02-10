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
import java.util.Optional;
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

        CustomEditBox idField = new CustomEditBox(
                Minecraft.getInstance().font,
                x + 22, y, width - 44, 20,
                Component.empty()
        );
        idField.setMaxLength(32767);
        idField.setValue(entityId);

        IconButton idFieldIconButton = IconButton.forEntity(
                x, y, 20, 20,
                ResourceLocation.tryParse(idField.getValue()),
                true,
                button -> {
                }
        );

        Runnable validateAndUpdate = () -> {
            String newId = idField.getValue().trim();

            boolean isValidEntity = false;
            ResourceLocation identifier = null;
            EntityType<?> entityType = null;

            if (!newId.isBlank()) {
                try {
                    identifier = ResourceLocation.tryParse(newId);
                    if (identifier != null) {
                        Optional<EntityType<?>> entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(identifier);

                        if (entityTypeOpt.isPresent()) {
                            entityType = entityTypeOpt.get();
                            isValidEntity = true;
                        }
                    }
                } catch (Exception ignored) {
                    isValidEntity = false;
                }
            }

            String tooltipText;
            if (newId.isBlank()) {
                idField.setTextColor(0xAAAAAA);
                tooltipText = Component.translatable("mcidentitymobs.tooltip.entity_id_format").getString();
            } else if (!isValidEntity) {
                idField.setTextColor(0xFF5555);
                if (identifier == null) {
                    tooltipText = Component.translatable("mcidentitymobs.tooltip.entity_incorrect_format").getString();
                } else {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_not_found").getString(),
                            newId
                    );
                }
            } else {
                idField.setTextColor(0xFFFFFF);
                if (entityType != null) {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_found").getString(),
                            entityType.getDescription().getString()
                    );
                } else {
                    tooltipText = String.format(
                            Component.translatable("mcidentitymobs.tooltip.entity_found").getString(),
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

        idField.setResponder(s -> {
            idFieldIconButton.updateFromId(s.trim());
            validateAndUpdate.run();
        });

        validateAndUpdate.run();

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    set.remove(currentId[0]);
                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        remove.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.remove")));

        w.add(idFieldIconButton);
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
                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.add")));

        w.add(add);
        return w;
    }
}