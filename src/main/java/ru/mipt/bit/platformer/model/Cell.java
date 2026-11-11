package ru.mipt.bit.platformer.model;

/** Domain value: grid coordinates. */
public record Cell(int x, int y) {
    public Cell next(Direction direction) {
        return new Cell(x + direction.dx(), y + direction.dy());
    }
}
