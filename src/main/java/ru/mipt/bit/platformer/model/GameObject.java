package ru.mipt.bit.platformer.model;

/** Domain interface: an object participating in the game simulation. */
public interface GameObject {
    Cell position();
    void live(float delta, WorldAccess world);
}
