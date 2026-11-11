package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;
import java.util.Objects;

/** Application use case: requests a shot through the world port. */
public final class ShootCommand implements Command {
    private final Tank tank;
    private final WorldAccess world;

    public ShootCommand(Tank tank, WorldAccess world) {
        this.tank = Objects.requireNonNull(tank);
        this.world = Objects.requireNonNull(world);
    }

    @Override
    public void execute() { tank.shoot(world); }
}
