package ru.mipt.bit.platformer.model;

/** Domain port: checks whether a cell can be entered. */
@FunctionalInterface
public interface MovementPolicy {
    boolean canEnter(Cell cell);
}
