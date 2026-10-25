package ru.mipt.bit.platformer.model;

import java.util.List;
import java.util.Objects;

public final class TankMovement implements MovementPolicy {
    private final Field field;
    private final List<Tank> tanks;

    public TankMovement(Field field, List<Tank> tanks) {
        this.field = Objects.requireNonNull(field);
        this.tanks = List.copyOf(tanks);
    }

    @Override
    public boolean canEnter(Cell cell) {
        return field.canEnter(cell) && tanks.stream().noneMatch(tank ->
                tank.position().equals(cell) || tank.destination().equals(cell));
    }
}
