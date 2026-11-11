package ru.mipt.bit.platformer.model;

import java.util.Objects;

/** Domain entity: projectile movement and damage. */
public final class Bullet implements GameObject {
    private Cell position;
    private final Direction direction;
    private final Tank owner;
    private final int damage;
    private final float stepDuration;
    private float elapsed;

    public Bullet(Cell position, Direction direction, Tank owner, int damage, float stepDuration) {
        this.position = Objects.requireNonNull(position);
        this.direction = Objects.requireNonNull(direction);
        this.owner = Objects.requireNonNull(owner);
        if (damage <= 0 || !Float.isFinite(stepDuration) || stepDuration <= 0) {
            throw new IllegalArgumentException("Bullet damage and step duration must be positive");
        }
        this.damage = damage;
        this.stepDuration = stepDuration;
    }

    @Override
    public void live(float delta, WorldAccess world) {
        if (!Float.isFinite(delta) || delta < 0) throw new IllegalArgumentException("Invalid delta");
        if (!world.contains(this) || world.collide(this)) return;
        elapsed += delta;
        while (elapsed >= stepDuration && world.contains(this)) {
            elapsed -= stepDuration;
            position = position.next(direction);
            if (world.collide(this)) return;
        }
    }

    public Cell position() { return position; }
    public Direction direction() { return direction; }
    public Tank owner() { return owner; }
    public int damage() { return damage; }
    public float progress() { return elapsed / stepDuration; }
}
