package ru.mipt.bit.platformer.model;

@FunctionalInterface
public interface MovementPolicy {
    boolean canEnter(Cell cell);
}
