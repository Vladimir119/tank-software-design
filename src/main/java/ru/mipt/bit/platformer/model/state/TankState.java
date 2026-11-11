package ru.mipt.bit.platformer.model.state;

/** Domain: behavior of a tank in its current state. */
public interface TankState {
    default float movementSpeed() { return 1; }
    default float reloadSpeed() { return 1; }
    default boolean canShoot() { return true; }

    default float advance(float progress, float delta, float moveDuration) {
        return Math.min(1, progress + delta * movementSpeed() / moveDuration);
    }
}
