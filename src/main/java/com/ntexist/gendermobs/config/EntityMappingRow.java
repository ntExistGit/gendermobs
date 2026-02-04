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

public class EntityMappingRow {

    public static List<AbstractWidget> build(
            int x, int y, int width,
            Map<String, String> map,
            String infectedEntityId,
            String zombieVariantId,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        String[] currentInfectedId = new String[]{infectedEntityId};
        String[] currentZombieId = new String[]{zombieVariantId};

        int fieldWidth = (width - 44) / 2;

        EditBox infectedField = new EditBox(
                Minecraft.getInstance().font,
                x, y, fieldWidth, 20,
                Component.empty()
        );
        infectedField.setMaxLength(32767);
        infectedField.setValue(infectedEntityId);

        EditBox zombieField = new EditBox(
                Minecraft.getInstance().font,
                x + fieldWidth + 2, y, fieldWidth, 20,
                Component.empty()
        );
        zombieField.setMaxLength(32767);
        zombieField.setValue(zombieVariantId);

        Runnable validateAndUpdate = (Runnable) () -> {
            String newInfectedId = infectedField.getValue().trim();
            String newZombieId = zombieField.getValue().trim();

            boolean isInfectedValid = validateEntityField(infectedField, newInfectedId,
                    "gendermobs.tooltip.infected_entity");

            boolean isZombieValid = validateEntityField(zombieField, newZombieId,
                    "gendermobs.tooltip.zombie_variant");

            if (isInfectedValid && isZombieValid) {
                boolean infectedChanged = !newInfectedId.equals(currentInfectedId[0]);
                boolean zombieChanged = !newZombieId.equals(currentZombieId[0]);

                if (infectedChanged || zombieChanged) {
                    if (infectedChanged) {
                        map.remove(currentInfectedId[0]);
                    }

                    map.put(newInfectedId, newZombieId);
                    currentInfectedId[0] = newInfectedId;
                    currentZombieId[0] = newZombieId;
                }
            }
        };

        infectedField.setResponder(newId -> validateAndUpdate.run());
        zombieField.setResponder(newId -> validateAndUpdate.run());

        validateAndUpdate.run();

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    map.remove(currentInfectedId[0]);
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        remove.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.remove")));

        w.add(infectedField);
        w.add(zombieField);
        w.add(remove);
        return w;
    }

    private static boolean validateEntityField(EditBox field, String entityId, String tooltipKey) {
        boolean isValidEntity = false;
        ResourceLocation identifier = null;
        String tooltipText = "";

        if (entityId.isBlank()) {
            field.setTextColor(0xAAAAAA);
            tooltipText = Component.translatable("gendermobs.tooltip.entity_id_format").getString();
        } else {
            try {
                identifier = ResourceLocation.tryParse(entityId);
                if (identifier != null) {
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
                    if (entityType != null && entityType != EntityType.PIG) {
                        ResourceLocation registeredId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                        if (registeredId != null && registeredId.equals(identifier)) {
                            isValidEntity = true;
                            tooltipText = String.format(
                                    Component.translatable("gendermobs.tooltip.entity_found").getString(),
                                    entityType.getDescription().getString()
                            );
                        } else {
                            field.setTextColor(0xFF5555);
                            tooltipText = String.format(
                                    Component.translatable("gendermobs.tooltip.entity_not_found").getString(),
                                    entityId
                            );
                        }
                    } else {
                        field.setTextColor(0xFF5555);
                        tooltipText = String.format(
                                Component.translatable("gendermobs.tooltip.entity_not_found").getString(),
                                entityId
                        );
                    }
                } else {
                    field.setTextColor(0xFF5555);
                    tooltipText = Component.translatable("gendermobs.tooltip.incorrect_format").getString();
                }
            } catch (Exception e) {
                field.setTextColor(0xFF5555);
                tooltipText = Component.translatable("gendermobs.tooltip.incorrect_format").getString();
            }
        }

        if (isValidEntity) {
            field.setTextColor(0xFFFFFF);
        }

        // Добавляем основной тултип
        String mainTooltip = Component.translatable(tooltipKey).getString();
        if (!tooltipText.isEmpty()) {
            mainTooltip += "\n" + tooltipText;
        }
        field.setTooltip(Tooltip.create(Component.literal(mainTooltip)));

        return isValidEntity;
    }

    public static List<AbstractWidget> buildAddRow(
            int x, int y, int width,
            Map<String, String> map,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        int fieldWidth = (width - 44) / 2;

        EditBox placeholder1 = new EditBox(Minecraft.getInstance().font, x, y, fieldWidth, 20, Component.empty());
        placeholder1.setVisible(false);
        placeholder1.active = false;

        EditBox placeholder2 = new EditBox(Minecraft.getInstance().font, x + fieldWidth + 2, y, fieldWidth, 20, Component.empty());
        placeholder2.setVisible(false);
        placeholder2.active = false;

        Button add = Button.builder(
                Component.literal("+"),
                b -> {
                    String baseKey = "minecraft:villager";
                    String key = baseKey;
                    String value = "minecraft:zombie_villager";
                    int i = 1;

                    while (map.containsKey(key)) {
                        key = baseKey + "_" + i++;
                    }

                    map.put(key, value);
                    rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("gendermobs.tooltip.add_mapping")));

        w.add(placeholder1);
        w.add(placeholder2);
        w.add(add);
        return w;
    }
}