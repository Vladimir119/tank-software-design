package ru.mipt.bit.platformer.model.state;

/** Domain: medium tank state. */
public final class MediumTankState implements TankState {
    public static final MediumTankState INSTANCE = new MediumTankState();

    private MediumTankState() { }

    @Override
    public float movementSpeed() { return 0.5f; }
}
