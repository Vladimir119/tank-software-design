package ru.mipt.bit.platformer.model;

import java.util.Objects;

public final class Tank {
    private Cell position;
    private Cell destination;
    private Direction direction = Direction.RIGHT;
    private float progress = 1;

    public Tank(Cell position) {
        this.position = Objects.requireNonNull(position);
        destination = position;
    }

    public void move(Direction direction, Field field) {
        Objects.requireNonNull(direction);
        if (progress < 1) return;
        this.direction = direction;
        Cell next = position.next(direction);
        if (field.canEnter(next)) {
            destination = next;
            progress = 0;
        }
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        progress = Math.min(1, progress + delta / 0.4f);
        if (progress == 1) position = destination;
    }

    public Cell position() { return position; }
    public Cell destination() { return destination; }
    public Direction direction() { return direction; }
    public float progress() { return progress; }
}
