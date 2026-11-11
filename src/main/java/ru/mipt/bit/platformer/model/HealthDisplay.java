package ru.mipt.bit.platformer.model;

/** Application state: visibility of the health overlay. */
public final class HealthDisplay {
    private boolean visible;

    public boolean visible() { return visible; }
    public void toggle() { visible = !visible; }
}
