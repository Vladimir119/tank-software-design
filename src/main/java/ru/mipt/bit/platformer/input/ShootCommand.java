package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;
import java.util.Objects;

public final class ShootCommand implements Command {
    private final Tank tank;
    private final GameWorld world;

    public ShootCommand(Tank tank, GameWorld world) {
        this.tank = Objects.requireNonNull(tank);
        this.world = Objects.requireNonNull(world);
    }

    @Override
    public void execute() { tank.shoot(world); }
}
