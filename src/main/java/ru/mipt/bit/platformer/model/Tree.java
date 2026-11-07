package ru.mipt.bit.platformer.model;

import java.util.Objects;

public record Tree(Cell position) implements GameObject {
    public Tree {
        Objects.requireNonNull(position);
    }
    @Override
    public void live(float delta, GameWorld world) { }
}
