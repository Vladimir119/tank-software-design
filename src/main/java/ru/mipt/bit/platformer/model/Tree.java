package ru.mipt.bit.platformer.model;

import java.util.Objects;

public record Tree(Cell position) {
    public Tree {
        Objects.requireNonNull(position);
    }
}
