package ru.mipt.bit.platformer.model;

public record Cell(int x, int y) {
    public Cell next(Direction direction) {
        return new Cell(x + direction.dx(), y + direction.dy());
    }
}
