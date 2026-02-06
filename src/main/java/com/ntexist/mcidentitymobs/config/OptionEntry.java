package com.ntexist.mcidentitymobs.config;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class OptionEntry<T> {

    protected final String label;
    protected final String tooltip;
    protected final Supplier<T> getter;
    protected final Consumer<T> setter;
    protected final T defaultValue;

    protected final List<AbstractWidget> widgets = new ArrayList<>();

    protected OptionEntry(
            String label,
            String tooltip,
            Supplier<T> getter,
            Consumer<T> setter,
            T defaultValue
    ) {
        this.label = label;
        this.tooltip = tooltip;
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = defaultValue;
    }

    protected OptionEntry(
            String label,
            Supplier<T> getter,
            Consumer<T> setter,
            T defaultValue
    ) {
        this(label, null, getter, setter, defaultValue);
    }

    public abstract List<AbstractWidget> build(int x, int y, int width);

    public abstract int getHeight();

    protected void onReset() {
        setter.accept(defaultValue);
    }
}
