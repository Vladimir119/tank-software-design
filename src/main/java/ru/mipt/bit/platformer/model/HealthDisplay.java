package ru.mipt.bit.platformer.model;

public final class HealthDisplay {
    private boolean visible;

    public boolean visible() { return visible; }
    public void toggle() { visible = !visible; }
}
