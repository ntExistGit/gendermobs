package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class ColorOption extends OptionEntry<String> {

    private StringWidget labelWidget;
    private static final Pattern HEX_PATTERN =
            Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static CustomEditBox box;
    private ColorPreviewWidget preview;
    private IconButton reset;

    public ColorOption(
            String label,
            String tooltip,
            Supplier<String> getter,
            Consumer<String> setter,
            String def
    ) {
        super(label, tooltip, getter, setter, def);
    }

    public ColorOption(
            String label,
            Supplier<String> getter,
            Consumer<String> setter,
            String def
    ) {
        super(label, null, getter, setter, def);
    }

    private Supplier<Boolean> activeCondition = () -> true;

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

        final CustomEditBox currentBox = new CustomEditBox(
                Minecraft.getInstance().font,
                x + labelWidget.getWidth() + 22, y, width / 2 - 44, 20,
                Component.literal(label)
        );
        this.box = currentBox;
        currentBox.setValue(getter.get());
        currentBox.setMaxLength(7);

        currentBox.setResponder(text -> {
            if (isValid(text)) {
                currentBox.setTextColor(0xFFFFFF);
                setter.accept(text);
                preview.setColor(parseColor(text));
            } else {
                currentBox.setTextColor(0xFF5555);
                preview.setColor(0x000000);
            }
        });

        preview = new ColorPreviewWidget(
                x + labelWidget.getWidth(), y, 20, 20,
                parseColor(getter.get()),
                box
        );
        preview.setTooltip(Tooltip.create(Component.translatable("mcidentitymobs.config.tooltip.preview")));

        reset = new IconButton(
                x + labelWidget.getWidth() + 24 + box.getWidth(), y, 20, 20,
                ResourceLocation.tryBuild("mcidentitymobs", "textures/gui/sprites/icon/reset.png"),
                btn -> {
                    currentBox.setValue(defaultValue);
                    currentBox.setTextColor(0xFFFFFF);
                    preview.setColor(parseColor(defaultValue));
                    setter.accept(defaultValue);
                }
        );

        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            labelWidget.setTooltip(Tooltip.create(Component.translatable(this.tooltip)));
            currentBox.setTooltip(Tooltip.create(Component.translatable(this.tooltip)));
        }

        widgets.add(labelWidget);
        widgets.add(preview);
        widgets.add(currentBox);
        widgets.add(reset);

        return widgets;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    private boolean isValid(String text) {
        return HEX_PATTERN.matcher(text).matches();
    }

    private int parseColor(String hex) {
        try {
            return Integer.parseInt(hex.substring(1), 16);
        } catch (Exception e) {
            return 0x000000;
        }
    }

    public static class ColorPreviewWidget extends Button implements IPresetRenderable {
        private int color;
        private boolean showPresets = false;
        private final CustomEditBox linkedBox;

        private final String[] presets = {
                "#000000", "#0000AA", "#00AA00", "#00AAAA",
                "#AA0000", "#AA00AA", "#FFAA00", "#AAAAAA",
                "#555555", "#5555FF", "#55FF55", "#55FFFF",
                "#FF5555", "#FF55FF", "#FFFF55", "#FFFFFF"
        };

        public ColorPreviewWidget(int x, int y, int w, int h, int color, CustomEditBox linkedBox) {
            super(x, y, w, h, Component.empty(), b -> ((ColorPreviewWidget)b).togglePresets(), DEFAULT_NARRATION);
            this.color = color;
            this.linkedBox = linkedBox;
        }

        public void togglePresets() {
            this.showPresets = !this.showPresets;
        }

        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);

            int padding = 3;
            context.fill(getX() + padding, getY() + padding, getX() + width - padding, getY() + height - padding, 0xFF000000 | color);
        }

        @Override
        public void renderPresets(GuiGraphics context, int mouseX, int mouseY) {
            if (!showPresets) return;

            int cellSize = 15;
            int gap = 2;
            int startX = getX();
            int startY = getY() + height + 2;

            context.pose().pushPose();
            context.pose().translate(0, 0, 500);

            context.fill(startX - 2, startY - 2, startX + (cellSize + gap) * 4 + 2, startY + (cellSize + gap) * 4 + 2, 0xFF101010);

            for (int i = 0; i < presets.length; i++) {
                int row = i / 4;
                int col = i % 4;
                int px = startX + col * (cellSize + gap);
                int py = startY + row * (cellSize + gap);
                int presetColor = Integer.parseInt(presets[i].substring(1), 16);

                if (mouseX >= px && mouseX <= px + cellSize && mouseY >= py && mouseY <= py + cellSize) {
                    context.fill(px - 1, py - 1, px + cellSize + 1, py + cellSize + 1, 0xFFFFFFFF);
                }
                context.fill(px, py, px + cellSize, py + cellSize, 0xFF000000 | presetColor);
            }

            context.pose().popPose();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (showPresets) {
                int cellSize = 15;
                int gap = 2;
                int startX = getX();
                int startY = getY() + height + 2;

                int menuWidth = (cellSize + gap) * 4 + 4;
                int menuHeight = (cellSize + gap) * 4 + 4;

                if (mouseX >= startX - 2 && mouseX <= startX + menuWidth &&
                        mouseY >= startY - 2 && mouseY <= startY + menuHeight) {

                    for (int i = 0; i < presets.length; i++) {
                        int row = i / 4;
                        int col = i % 4;
                        int px = startX + col * (cellSize + gap);
                        int py = startY + row * (cellSize + gap);

                        if (mouseX >= px && mouseX <= px + cellSize && mouseY >= py && mouseY <= py + cellSize) {
                            String hex = presets[i];
                            this.linkedBox.setValue(hex);
                            this.showPresets = false;
                            Minecraft.getInstance().getSoundManager().play(
                                    SimpleSoundInstance.forUI(
                                            SoundEvents.UI_BUTTON_CLICK, 1.0F
                                    )
                            );
                            return true;
                        }
                    }
                    return true;
                }

                this.showPresets = false;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}