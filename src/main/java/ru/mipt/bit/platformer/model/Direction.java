package ru.mipt.bit.platformer.model;

public enum Direction {
    UP(0, 1, 90), LEFT(-1, 0, -180), DOWN(0, -1, -90), RIGHT(1, 0, 0);

    private final int dx;
    private final int dy;
    private final float rotation;

    Direction(int dx, int dy, float rotation) {
        this.dx = dx;
        this.dy = dy;
        this.rotation = rotation;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }
    public float rotation() { return rotation; }
}
