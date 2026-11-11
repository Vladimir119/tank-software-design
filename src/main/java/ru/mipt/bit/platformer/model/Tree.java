package ru.mipt.bit.platformer.model;

import java.util.Objects;

/** Domain entity: a stationary obstacle. */
public record Tree(Cell position) implements GameObject {
    public Tree {
        Objects.requireNonNull(position);
    }
    @Override
    public void live(float delta, WorldAccess world) { }
}
