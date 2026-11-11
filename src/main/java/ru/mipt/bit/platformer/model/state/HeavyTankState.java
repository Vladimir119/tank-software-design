package ru.mipt.bit.platformer.model.state;

/** Domain: heavy tank state. */
public final class HeavyTankState implements TankState {
    public static final HeavyTankState INSTANCE = new HeavyTankState();

    private HeavyTankState() { }

    @Override
    public float movementSpeed() { return 1f / 3; }

    @Override
    public boolean canShoot() { return false; }
}
