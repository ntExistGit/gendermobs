package com.ntexist.mcidentitymobs.config;

import com.ntexist.mcidentitymobs.pipeline.SpawnPipeline;
import com.ntexist.mcidentitymobs.service.ColorService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;

import java.util.*;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private final List<OptionEntry<?>> options = new ArrayList<>();
    private final List<AbstractWidget> listWidgets = new ArrayList<>();

    private int scrollOffset = 0;
    private int totalContentHeight = 0;

    private int listTop;
    private int listBottom;

    private int currentTab = 0;
    private final List<Tab> tabs = new ArrayList<>();
    private static final int TAB_HEIGHT = 25;
    private int tabWidth;

    private boolean vanillaHumanoidOptionExpanded = true;
    private boolean customHumanoidOptionExpanded = true;
    private boolean vanillaNonHumanoidOptionExpanded = true;
    private boolean customNonHumanoidOptionExpanded = true;
    private boolean zombiesOptionExpanded = true;
    private boolean canBeInfectedOptionExpanded = true;

    private boolean scrolling = false;

    private class Tab {
        final String translationKey;
        final Runnable contentBuilder;

        Tab(String translationKey, Runnable contentBuilder) {
            this.translationKey = translationKey;
            this.contentBuilder = contentBuilder;
        }

        Component getTitle() {
            return Component.translatable(translationKey);
        }
    }

    public ConfigScreen(Screen parent) {
        super(Component.translatable("mcidentitymobs.config.title"));
        this.parent = parent;
        ConfigManager.createWorkingCopy();
        createTabs();
    }

    private void createTabs() {
        tabs.clear();

        tabs.add(new Tab("mcidentitymobs.config.tab.general", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            options.add(new BoolOption(
                    "mcidentitymobs.config.show_names",
                    "mcidentitymobs.config.tooltip.show_names",
                    () -> ConfigManager.WORKING_COPY.general.showNames,
                    v -> ConfigManager.WORKING_COPY.general.showNames = v,
                    true));

            options.add(new BoolOption(
                    "mcidentitymobs.config.colored_names",
                    "mcidentitymobs.config.tooltip.colored_names",
                    () -> ConfigManager.WORKING_COPY.general.showColors,
                    v -> ConfigManager.WORKING_COPY.general.showColors = v,
                    false));

            if (ModList.get().isLoaded("jade")) {
                options.add(new SpacerOption(12));

                options.add(new BoolOption(
                        "mcidentitymobs.config.jade_icons",
                        "mcidentitymobs.config.tooltip.jade_icons",
                        () -> ConfigManager.WORKING_COPY.jade.jadeIcons,
                        v -> ConfigManager.WORKING_COPY.jade.jadeIcons = v,
                        false));

                options.add(new SliderOption(
                        "mcidentitymobs.config.iconOffsetY",
                        "mcidentitymobs.config.tooltip.iconOffsetY",
                        -10.0f, 10.0f, 0.1f,
                        () -> ConfigManager.WORKING_COPY.jade.offsetY,
                        v -> ConfigManager.WORKING_COPY.jade.offsetY = v,
                        -3.5f
                ));

                options.add(new BoolOption(
                        "mcidentitymobs.config.jade.zombieConversion",
                        "mcidentitymobs.config.tooltip.jade.zombieConversion",
                        () -> ConfigManager.WORKING_COPY.jade.conversionTime,
                        v -> ConfigManager.WORKING_COPY.jade.conversionTime = v,
                        true));

                options.add(new SpacerOption(12));
            }

            options.add(new ColorOption(
                    "mcidentitymobs.config.male_color",
                    "mcidentitymobs.config.tooltip.male_color",
                    () -> ConfigManager.WORKING_COPY.colors.male,
                    v -> ConfigManager.WORKING_COPY.colors.male = v,
                    "#5555FF"));

            options.add(new ColorOption(
                    "mcidentitymobs.config.female_color",
                    "mcidentitymobs.config.tooltip.female_color",
                    () -> ConfigManager.WORKING_COPY.colors.female,
                    v -> ConfigManager.WORKING_COPY.colors.female = v,
                    "#FF55FF"));

            options.add(new SpacerOption(12));

            options.add(new BoolOption(
                    "mcidentitymobs.config.use_default_names",
                    "mcidentitymobs.config.tooltip.use_default_names",
                    () -> ConfigManager.WORKING_COPY.general.useDefaultNames,
                    v -> ConfigManager.WORKING_COPY.general.useDefaultNames = v,
                    true));

            options.add(new ButtonOption(
                    "mcidentitymobs.config.restore_default_names",
                    "mcidentitymobs.config.tooltip.restore_default_names",
                    "mcidentitymobs.config.button.action",
                    b -> ConfigManager.restoreDefaultNames()));

            options.add(new SpacerOption(12));
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.vanilla_humanoid", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            MapOption vanillaHumanoidOption = new MapOption(
                    "mcidentitymobs.config.vanilla_humanoid",
                    () -> ConfigManager.WORKING_COPY.vanillaHumanoid,
                    v -> ConfigManager.WORKING_COPY.vanillaHumanoid = v,
                    ModConfig.defaultConfig().vanillaHumanoid) {
                @Override
                protected void onReset() {
                    setter.accept(new LinkedHashMap<>(ModConfig.defaultConfig().vanillaHumanoid));
                    if (this.rebuild != null) {
                        this.rebuild.run();
                    }
                }
            };
            vanillaHumanoidOption.setExpanded(vanillaHumanoidOptionExpanded);
            vanillaHumanoidOption.attachRebuild(() -> {
                vanillaHumanoidOptionExpanded = vanillaHumanoidOption.isExpanded();
                rebuild();
            });
            options.add(vanillaHumanoidOption);

            options.add(new SpacerOption(12));
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.custom_humanoid", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            MapOption customHumanoidOption = new MapOption(
                    "mcidentitymobs.config.custom_humanoid",
                    () -> ConfigManager.WORKING_COPY.customHumanoid,
                    v -> ConfigManager.WORKING_COPY.customHumanoid = v,
                    ModConfig.defaultConfig().customHumanoid) {
                @Override
                protected void onReset() {
                    setter.accept(new LinkedHashMap<>(ModConfig.defaultConfig().customHumanoid));
                    if (this.rebuild != null) {
                        this.rebuild.run();
                    }
                }
            };
            customHumanoidOption.setExpanded(customHumanoidOptionExpanded);
            customHumanoidOption.attachRebuild(() -> {
                customHumanoidOptionExpanded = customHumanoidOption.isExpanded();
                rebuild();
            });
            options.add(customHumanoidOption);

            options.add(new SpacerOption(12));
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.vanilla_non_humanoid", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            MapOption vanillaNonHumanoidOption = new MapOption(
                    "mcidentitymobs.config.vanilla_non_humanoid",
                    () -> ConfigManager.WORKING_COPY.vanillaNonHumanoid,
                    v -> ConfigManager.WORKING_COPY.vanillaNonHumanoid = v,
                    ModConfig.defaultConfig().vanillaNonHumanoid) {
                @Override
                protected void onReset() {
                    setter.accept(new LinkedHashMap<>(ModConfig.defaultConfig().vanillaNonHumanoid));
                    if (this.rebuild != null) {
                        this.rebuild.run();
                    }
                }
            };
            vanillaNonHumanoidOption.setExpanded(vanillaNonHumanoidOptionExpanded);
            vanillaNonHumanoidOption.attachRebuild(() -> {
                vanillaNonHumanoidOptionExpanded = vanillaNonHumanoidOption.isExpanded();
                rebuild();
            });
            options.add(vanillaNonHumanoidOption);

            options.add(new SpacerOption(12));
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.custom_non_humanoid", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            MapOption customNonHumanoidOption = new MapOption(
                    "mcidentitymobs.config.custom_non_humanoid",
                    () -> ConfigManager.WORKING_COPY.customNonHumanoid,
                    v -> ConfigManager.WORKING_COPY.customNonHumanoid = v,
                    ModConfig.defaultConfig().customNonHumanoid) {
                @Override
                protected void onReset() {
                    setter.accept(new LinkedHashMap<>(ModConfig.defaultConfig().customNonHumanoid));
                    if (this.rebuild != null) {
                        this.rebuild.run();
                    }
                }
            };
            customNonHumanoidOption.setExpanded(customNonHumanoidOptionExpanded);
            customNonHumanoidOption.attachRebuild(() -> {
                customNonHumanoidOptionExpanded = customNonHumanoidOption.isExpanded();
                rebuild();
            });
            options.add(customNonHumanoidOption);

            options.add(new SpacerOption(12));
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.can_be_infected", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            EntityMappingOption canBeInfectedOption = new EntityMappingOption(
                    "mcidentitymobs.config.can_be_infected",
                    () -> ConfigManager.WORKING_COPY.canBeInfected,
                    v -> ConfigManager.WORKING_COPY.canBeInfected = v,
                    new LinkedHashMap<>()
            ) {
                @Override
                protected void onReset() {
                    Map<String, InfectionData> resetMap = new LinkedHashMap<>();
                    for (var entry : ModConfig.defaultConfig().canBeInfected.entrySet()) {
                        resetMap.put(entry.getKey(), new InfectionData(entry.getValue()));
                    }
                    setter.accept(resetMap);
                    if (this.rebuild != null) {
                        this.rebuild.run();
                    }
                }
            };
            canBeInfectedOption.setExpanded(canBeInfectedOptionExpanded);
            canBeInfectedOption.attachRebuild(() -> {
                canBeInfectedOptionExpanded = canBeInfectedOption.isExpanded();
                rebuild();
            });
            options.add(canBeInfectedOption);
        }));

        tabs.add(new Tab("mcidentitymobs.config.tab.zombies", () -> {
            options.clear();
            options.add(new SpacerOption(12));

            ListOption zombiesOption = new ListOption(
                    "mcidentitymobs.config.zombies",
                    () -> ConfigManager.WORKING_COPY.zombies,
                    v -> ConfigManager.WORKING_COPY.zombies = v,
                    ModConfig.defaultConfig().zombies
            );
            zombiesOption.setExpanded(zombiesOptionExpanded);
            zombiesOption.attachRebuild(() -> {
                zombiesOptionExpanded = zombiesOption.isExpanded();
                rebuild();
            });
            options.add(zombiesOption);

            options.add(new SpacerOption(12));
        }));
    }

    @Override
    protected void init() {
        clearWidgets();
        options.clear();
        listWidgets.clear();

        tabWidth = Math.min(120, width / Math.max(1, tabs.size()));

        int totalTabsWidth = tabs.size() * tabWidth;
        int tabStartX = (width - totalTabsWidth) / 2;

        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            Tab tab = tabs.get(i);

            Button tabButton = Button.builder(
                            tab.getTitle(),
                            b -> {
                                currentTab = tabIndex;
                                rebuild();
                            })
                    .bounds(tabStartX + i * tabWidth, 15, tabWidth, TAB_HEIGHT)
                    .build();

            if (i == currentTab) {
                tabButton.active = false;
            }

            addRenderableWidget(tabButton);
        }

        listTop = 15 + TAB_HEIGHT + 10;
        listBottom = height - 40;

        if (currentTab < tabs.size()) {
            tabs.get(currentTab).contentBuilder.run();
        }

        int buttonWidth = (width - 100) / 3;

        int calculatedHeight = 0;
        for (OptionEntry<?> option : options) {
            calculatedHeight += option.getHeight();
        }
        totalContentHeight = calculatedHeight;

        int visibleHeight = listBottom - listTop;
        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);
        if (scrollOffset > maxScroll)
            scrollOffset = maxScroll;

        int y = listTop - scrollOffset;
        for (OptionEntry<?> option : options) {
            for (var w : option.build(40, y, width - 80)) {
                addWidget(w);
                addRenderableWidget(w);
                listWidgets.add(w);
            }
            y += option.getHeight();
        }

        addRenderableWidget(Button.builder(
                        Component.translatable("mcidentitymobs.gui.back"),
                        b -> Minecraft.getInstance().setScreen(parent)).bounds(40, height - 28, buttonWidth, 20)
                .build());

        addRenderableWidget(Button.builder(
                Component.translatable("mcidentitymobs.gui.reset_all"),
                b -> {
                    ConfigManager.resetAllConfig();
                    rebuild();
                }).bounds(40 + buttonWidth + 10, height - 28, buttonWidth, 20).build());

        addRenderableWidget(Button.builder(
                Component.translatable("mcidentitymobs.gui.done"),
                b -> {
                    ConfigManager.applyWorkingCopy();
                    Minecraft.getInstance().setScreen(parent);
                }).bounds(40 + buttonWidth * 2 + 20, height - 28, buttonWidth, 20).build());
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseY < listTop || mouseY > listBottom)
            return false;

        int visibleHeight = listBottom - listTop;
        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);

        scrollOffset -= (int) (amount * 20);

        if (scrollOffset < 0)
            scrollOffset = 0;
        if (scrollOffset > maxScroll)
            scrollOffset = maxScroll;

        init();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollbarX = width - 10;
        int scrollbarWidth = 6;

        if (button == 0 && mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth) {
            if (mouseY >= listTop && mouseY <= listBottom) {
                this.scrolling = true;
                return true;
            }
        }

        if (mouseY >= listTop && mouseY <= listBottom) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        for (var child : children()) {
            if (child instanceof AbstractWidget widget) {
                if (!listWidgets.contains(widget) && widget.isMouseOver(mouseX, mouseY)) {
                    return widget.mouseClicked(mouseX, mouseY, button);
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int visibleHeight = listBottom - listTop;
            int maxScroll = Math.max(0, totalContentHeight - visibleHeight);

            double progress = (mouseY - listTop) / (double) visibleHeight;
            scrollOffset = (int) (progress * totalContentHeight - visibleHeight / 2.0);

            if (scrollOffset < 0)
                scrollOffset = 0;
            if (scrollOffset > maxScroll)
                scrollOffset = maxScroll;

            rebuild();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.enableScissor(0, listTop, width, listBottom);
        context.fill(0, listTop, width, listBottom, 0x77000000);

        for (var w : listWidgets) {
            w.render(context, mouseX, mouseY, delta);
        }

        if (totalContentHeight > (listBottom - listTop)) {
            int scrollbarWidth = 6;
            int scrollbarX = width - 10;
            int visibleHeight = listBottom - listTop;
            int thumbHeight = Math.max(10, (int) ((float) visibleHeight * visibleHeight / totalContentHeight));
            int thumbY = listTop + (int) ((float) scrollOffset / (totalContentHeight - visibleHeight)
                    * (visibleHeight - thumbHeight));

            context.fill(scrollbarX, listTop, scrollbarX + scrollbarWidth, listBottom, 0xFF000000);
            context.fill(scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, 0xFF808080);
            context.fill(scrollbarX, thumbY, scrollbarX + scrollbarWidth - 1, thumbY + 1, 0xFFC0C0C0);
            context.fill(scrollbarX, thumbY, scrollbarX + 1, thumbY + thumbHeight - 1, 0xFFC0C0C0);
        }
        context.disableScissor();

        context.fillGradient(0, listTop, width, listTop + 4, 0xFF000000, 0x00000000);
        context.fillGradient(0, listBottom - 4, width, listBottom, 0x00000000, 0xFF000000);

        for (var drawable : renderables) {
            if (!listWidgets.contains(drawable)) {
                drawable.render(context, mouseX, mouseY, delta);
            }
        }

        for (var w : listWidgets) {
            if (w instanceof IPresetRenderable presetWidget) {
                presetWidget.renderPresets(context, mouseX, mouseY);
            }
        }
    }

    public void rebuild() {
        clearWidgets();
        init();
    }
}