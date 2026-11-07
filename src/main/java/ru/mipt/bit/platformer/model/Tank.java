package ru.mipt.bit.platformer.model;

import java.util.Objects;

public final class Tank implements GameObject {
    private final float moveDuration;
    private final int maxHealth = 100;
    private int health = maxHealth;
    private float shotCooldown;
    private Cell position;
    private Cell destination;
    private Direction direction = Direction.RIGHT;
    private float progress = 1;

    public Tank(Cell position, float moveDuration) {
        if (!Float.isFinite(moveDuration) || moveDuration <= 0) {
            throw new IllegalArgumentException("Move duration must be finite and positive");
        }
        this.moveDuration = moveDuration;
        this.position = Objects.requireNonNull(position);
        destination = position;
    }

    public void move(Direction direction, MovementPolicy movement) {
        Objects.requireNonNull(direction);
        if (!alive() || progress < 1) return;
        this.direction = direction;
        Cell next = position.next(direction);
        if (movement.canEnter(next)) {
            destination = next;
            progress = 0;
        }
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        if (!alive()) return;
        shotCooldown = Math.max(0, shotCooldown - delta);
        progress = Math.min(1, progress + delta / moveDuration);
        if (progress == 1) position = destination;
    }

    public void shoot(GameWorld world) {
        if (!alive() || shotCooldown > 0 || !world.contains(this)) return;
        shotCooldown = 0.4f;
        world.add(new Bullet(position.next(direction), direction, this, 25, 0.12f));
    }

    public void damage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Damage must be nonnegative");
        health = Math.max(0, health - amount);
    }

    @Override
    public void live(float delta, GameWorld world) { update(delta); }

    public boolean alive() { return health > 0; }
    public int health() { return health; }
    public int maxHealth() { return maxHealth; }
    public Cell position() { return position; }
    public Cell destination() { return destination; }
    public Direction direction() { return direction; }
    public float progress() { return progress; }
}
