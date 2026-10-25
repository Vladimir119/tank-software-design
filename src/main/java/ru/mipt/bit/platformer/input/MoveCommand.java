package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;
import java.util.Objects;

public final class MoveCommand implements Command {
    private final Tank tank;
    private final Direction direction;
    private final MovementPolicy movement;

    public MoveCommand(Tank tank, Direction direction, MovementPolicy movement) {
        this.tank = Objects.requireNonNull(tank);
        this.direction = Objects.requireNonNull(direction);
        this.movement = Objects.requireNonNull(movement);
    }

    @Override
    public void execute() { tank.move(direction, movement); }
}
