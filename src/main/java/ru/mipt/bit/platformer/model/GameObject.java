package ru.mipt.bit.platformer.model;

public interface GameObject {
    Cell position();
    void live(float delta, GameWorld world);
}
