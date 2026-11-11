package ru.mipt.bit.platformer.model.state;

/** Domain: selects the damage state from the remaining health. */
public final class TankStates {
    private TankStates() { }

    public static TankState forHealth(int health, int maxHealth) {
        if (maxHealth <= 0 || health < 0 || health > maxHealth) {
            throw new IllegalArgumentException("Invalid health");
        }
        if ((long) health * 100 > (long) maxHealth * 70) return LightTankState.INSTANCE;
        if ((long) health * 100 >= (long) maxHealth * 15) return MediumTankState.INSTANCE;
        return HeavyTankState.INSTANCE;
    }
}
