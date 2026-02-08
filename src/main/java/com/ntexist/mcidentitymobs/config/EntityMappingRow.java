package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EntityMappingRow {

    public static List<AbstractWidget> build(
            int x, int y, int width,
            Map<String, InfectionData> map,
            Consumer<Map<String, InfectionData>> setter,
            String infectedEntityId,
            InfectionData data,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        String[] currentInfectedId = new String[]{infectedEntityId};
        InfectionData[] currentData = new InfectionData[]{data};

        final int ROW_HEIGHT = 20;
        final int GAP = 2;
        final int BUTTON_W = 55;
        final int REMOVE_W = 20;
        final int ICON_W = 20;
        final int REMOVE_H = (ROW_HEIGHT * 2) + GAP;

        int fieldW_row1 = (width - (BUTTON_W + REMOVE_W) - (GAP * 2)) / 2;
        if (fieldW_row1 < 80) fieldW_row1 = 80;

        int availableForRow2 = width - (2 * ICON_W) - (3 * GAP) - BUTTON_W - REMOVE_W;
        int fieldW_row2 = availableForRow2 / 2;
        if (fieldW_row2 < 80) fieldW_row2 = 80;

        int effectIconX = x;
        int effectFieldX = effectIconX + ICON_W + GAP;
        int itemIconX = effectFieldX + fieldW_row2;
        int itemFieldX = itemIconX + ICON_W + GAP;
        int timeFieldX = itemFieldX + fieldW_row2 + GAP;

        int row1Y = y;
        int row2Y = y + ROW_HEIGHT + GAP;

        CustomEditBox infectedField = new CustomEditBox(
                Minecraft.getInstance().font,
                x, row1Y, fieldW_row1, ROW_HEIGHT,
                Component.empty()
        );
        infectedField.setBordered(false);
        infectedField.setMaxLength(32767);
        infectedField.setValue(infectedEntityId);

        CustomEditBox zombieField = new CustomEditBox(
                Minecraft.getInstance().font,
                x + fieldW_row1 + GAP, row1Y, fieldW_row1, ROW_HEIGHT,
                Component.empty()
        );
        zombieField.setBordered(false);
        zombieField.setMaxLength(32767);
        zombieField.setValue(data.zombie);

        Button curableButton = Button.builder(
                Component.translatable(data.curable ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"),
                b -> {
                    data.curable = !data.curable;
                    b.setMessage(Component.translatable(data.curable ? "mcidentitymobs.boolean.true" : "mcidentitymobs.boolean.false"));
                }
        ).bounds(x + (fieldW_row1 * 2) + GAP * 2, row1Y, BUTTON_W, ROW_HEIGHT).build();
        curableButton.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.curable")));

        CustomEditBox effectField = new CustomEditBox(
                Minecraft.getInstance().font,
                effectFieldX, row2Y, fieldW_row2 - GAP, ROW_HEIGHT,
                Component.empty()
        );
        effectField.setBordered(false);
        effectField.setMaxLength(32767);
        effectField.setValue(data.effect);

        IconButton effectIconButton = IconButton.forEffect(
                effectIconX, row2Y, ICON_W, ROW_HEIGHT,
                ResourceLocation.tryParse(effectField.getValue()),
                button -> {
                }
        );

        CustomEditBox itemField = new CustomEditBox(
                Minecraft.getInstance().font,
                itemFieldX, row2Y, fieldW_row2, ROW_HEIGHT,
                Component.empty()
        );
        itemField.setBordered(false);
        itemField.setMaxLength(32767);
        itemField.setValue(data.item);

        IconButton itemIconButton = IconButton.forItem(
                itemIconX, row2Y, ICON_W, ROW_HEIGHT,
                ResourceLocation.tryParse(itemField.getValue()),
                button -> {
                }
        );

        CustomEditBox timeField = new CustomEditBox(
                Minecraft.getInstance().font,
                timeFieldX, row2Y, BUTTON_W, ROW_HEIGHT,
                Component.empty()
        );
        timeField.setBordered(false);
        timeField.setMaxLength(10);
        timeField.setValue(String.valueOf(data.time));

        Button remove = Button.builder(
                Component.literal("-"),
                b -> {
                    Map<String, InfectionData> newMap = new LinkedHashMap<>(map);
                    newMap.remove(currentInfectedId[0]);
                    setter.accept(newMap);
                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - REMOVE_W, row1Y, REMOVE_W, REMOVE_H).build();
        remove.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.remove")));

        Runnable validateAndUpdate = () -> {
            String newInfectedId = infectedField.getValue().trim();
            String newZombieId = zombieField.getValue().trim();
            String newEffect = effectField.getValue().trim();
            String newItem = itemField.getValue().trim();
            int newTime = 0;

            try {
                newTime = Integer.parseInt(timeField.getValue().trim());
                if (newTime < 0) newTime = 0;
                timeField.setTextColor(0xFFFFFF);
            } catch (NumberFormatException e) {
                timeField.setTextColor(0xFF5555);
                timeField.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.time")));
            }

            validateEntityField(infectedField, newInfectedId, "entity");

            validateEntityField(zombieField, newZombieId, "entity");

            validateEffectField(effectField, newEffect);

            validateItemField(itemField, newItem);

            boolean isInfectedValid = isEntityValid(newInfectedId);
            boolean isZombieValid = isEntityValid(newZombieId);
            boolean isEffectValid = newEffect.isEmpty() || isEffectValid(newEffect);
            boolean isItemValid = newItem.isEmpty() || isItemValid(newItem);

            if (isInfectedValid && isZombieValid) {
                boolean infectedChanged = !newInfectedId.equals(currentInfectedId[0]);
                boolean dataChanged = !newZombieId.equals(currentData[0].zombie) ||
                        !data.curable == currentData[0].curable ||
                        !newEffect.equals(currentData[0].effect) ||
                        !newItem.equals(currentData[0].item) ||
                        newTime != currentData[0].time;

                if (infectedChanged || dataChanged) {
                    if (infectedChanged) {
                        map.remove(currentInfectedId[0]);
                    }

                    data.curable = currentData[0].curable;

                    InfectionData newData = new InfectionData(
                            newZombieId, data.curable, newEffect, newItem, newTime
                    );

                    map.put(newInfectedId, newData);
                    currentInfectedId[0] = newInfectedId;
                    currentData[0] = newData;
                    setter.accept(new LinkedHashMap<>(map));
                }
            }
        };

        infectedField.setResponder(s -> validateAndUpdate.run());
        zombieField.setResponder(s -> validateAndUpdate.run());
        effectField.setResponder(s -> validateAndUpdate.run());
        effectField.setResponder(s -> { effectIconButton.updateFromId(s.trim()); });
        itemField.setResponder(s -> validateAndUpdate.run());
        itemField.setResponder(s -> { itemIconButton.updateFromId(s.trim()); });
        timeField.setResponder(s -> validateAndUpdate.run());

        validateAndUpdate.run();

        w.add(infectedField);
        w.add(zombieField);
        w.add(curableButton);
        w.add(effectIconButton);
        w.add(effectField);
        w.add(itemIconButton);
        w.add(itemField);
        w.add(timeField);
        w.add(remove);
        return w;
    }

    private static void validateEntityField(CustomEditBox field, String value, String type) {
        if (value.isBlank()) {
            field.setTextColor(0xAAAAAA);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.entity_id_format")));
            return;
        }

        try {
            ResourceLocation identifier = ResourceLocation.tryParse(value);
            if (identifier == null) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.entity_incorrect_format")));
                return;
            }

            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
            if (entityType == null || entityType == EntityType.PIG) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(
                        Component.translatable("mcidentitymobs.tooltip.entity_not_found", value)
                ));
                return;
            }

            field.setTextColor(0xFFFFFF);
            field.setTooltip(Tooltip.create(
                    Component.translatable("mcidentitymobs.tooltip.entity_found",
                            entityType.getDescription().getString())
            ));
        } catch (Exception e) {
            field.setTextColor(0xFF5555);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.entity_incorrect_format")));
        }
    }

    private static void validateEffectField(CustomEditBox field, String value) {
        if (value.isBlank()) {
            field.setTextColor(0xAAAAAA);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.effect_id_format")));
            return;
        }

        try {
            ResourceLocation identifier = ResourceLocation.tryParse(value);
            if (identifier == null) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.effect_incorrect_format")));
                return;
            }

            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(identifier);
            if (effect == null) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(
                        Component.translatable("mcidentitymobs.tooltip.effect_not_found", value)
                ));
                return;
            }

            field.setTextColor(0xFFFFFF);
            field.setTooltip(Tooltip.create(
                    Component.translatable("mcidentitymobs.tooltip.effect_found",
                            effect.getDisplayName().getString())
            ));
        } catch (Exception e) {
            field.setTextColor(0xFF5555);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.effect_incorrect_format")));
        }
    }

    private static void validateItemField(CustomEditBox field, String value) {
        if (value.isBlank()) {
            field.setTextColor(0xAAAAAA);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.item_id_format")));
            return;
        }

        try {
            ResourceLocation identifier = ResourceLocation.tryParse(value);
            if (identifier == null) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.item_incorrect_format")));
                return;
            }

            Item item = BuiltInRegistries.ITEM.get(identifier);
            if (item == null || item == Items.AIR) {
                field.setTextColor(0xFF5555);
                field.setTooltip(Tooltip.create(
                        Component.translatable("mcidentitymobs.tooltip.item_not_found", value)
                ));
                return;
            }

            field.setTextColor(0xFFFFFF);
            field.setTooltip(Tooltip.create(
                    Component.translatable("mcidentitymobs.tooltip.item_found",
                            item.getDescription().getString())
            ));
        } catch (Exception e) {
            field.setTextColor(0xFF5555);
            field.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.item_incorrect_format")));
        }
    }

    private static boolean isEntityValid(String id) {
        if (id.isBlank()) return false;
        try {
            ResourceLocation identifier = ResourceLocation.tryParse(id);
            if (identifier == null) return false;
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(identifier);
            return entityType != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isEffectValid(String id) {
        if (id.isBlank()) return true;
        try {
            ResourceLocation identifier = ResourceLocation.tryParse(id);
            if (identifier == null) return false;
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(identifier);
            return effect != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isItemValid(String id) {
        if (id.isBlank()) return true;
        try {
            ResourceLocation identifier = ResourceLocation.tryParse(id);
            if (identifier == null) return false;
            Item item = BuiltInRegistries.ITEM.get(identifier);
            return item != null && item != Items.AIR;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<AbstractWidget> buildAddRow(
            int x, int y, int width,
            Map<String, InfectionData> map,
            Consumer<Map<String, InfectionData>> setter,
            Runnable rebuild
    ) {
        List<AbstractWidget> w = new ArrayList<>();

        final int ROW_HEIGHT = 20;
        final int GAP = 2;
        final int BUTTON_W = 55;
        final int REMOVE_W = 20;

        int fieldW = (width - (BUTTON_W + REMOVE_W) - (GAP * 2)) / 2;
        if (fieldW < 100) fieldW = 100;

        int row1Y = y;
        int row2Y = y + ROW_HEIGHT + GAP;

        CustomEditBox placeholder1 = new CustomEditBox(Minecraft.getInstance().font, x, row1Y, fieldW, ROW_HEIGHT, Component.empty());
        placeholder1.setVisible(false);
        placeholder1.active = false;
        w.add(placeholder1);

        CustomEditBox placeholder2 = new CustomEditBox(Minecraft.getInstance().font, x + fieldW + GAP, row1Y, fieldW, ROW_HEIGHT, Component.empty());
        placeholder2.setVisible(false);
        placeholder2.active = false;
        w.add(placeholder2);

        CustomEditBox placeholder3 = new CustomEditBox(Minecraft.getInstance().font, x + (fieldW * 2) + GAP, row1Y, BUTTON_W, ROW_HEIGHT, Component.empty());
        placeholder3.setVisible(false);
        placeholder3.active = false;
        w.add(placeholder3);

        CustomEditBox placeholder4 = new CustomEditBox(Minecraft.getInstance().font, x, row2Y, fieldW, ROW_HEIGHT, Component.empty());
        placeholder4.setVisible(false);
        placeholder4.active = false;
        w.add(placeholder4);

        CustomEditBox placeholder5 = new CustomEditBox(Minecraft.getInstance().font, x + fieldW + GAP, row2Y, fieldW, ROW_HEIGHT, Component.empty());
        placeholder5.setVisible(false);
        placeholder5.active = false;
        w.add(placeholder5);

        CustomEditBox placeholder6 = new CustomEditBox(Minecraft.getInstance().font, x + (fieldW * 2) + GAP, row2Y, BUTTON_W, ROW_HEIGHT, Component.empty());
        placeholder6.setVisible(false);
        placeholder6.active = false;
        w.add(placeholder6);

        Button add = Button.builder(
                Component.literal("+"),
                b -> {
                    String baseKey = "namespace:entity_id";
                    String key = baseKey;
                    int i = 1;

                    while (map.containsKey(key)) {
                        key = baseKey + "_" + i++;
                    }

                    InfectionData defaultData = new InfectionData(
                            "namespace:entity_id", true,
                            "namespace:effect_id", "namespace:item_id", 3600
                    );

                    Map<String, InfectionData> newMap = new LinkedHashMap<>(map);
                    newMap.put(key, defaultData);
                    setter.accept(newMap);

                    if (rebuild != null) rebuild.run();
                }
        ).bounds(x + width - 20, y, 20, 20).build();
        add.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.tooltip.add")));

        w.add(add);
        return w;
    }
}