package ru.mipt.bit.platformer.model;

/** Domain port: access to objects and collisions without depending on GameWorld. */
public interface WorldAccess extends MovementPolicy {
    boolean contains(GameObject object);
    void add(GameObject object);
    void remove(GameObject object);
    boolean collide(Bullet bullet);
}
