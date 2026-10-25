package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;
import java.util.Objects;
import java.util.Random;

public final class RandomController implements ControlStrategy {
    private final Tank tank;
    private final Random random;
    private final Command[] commands;

    public RandomController(Tank tank, MovementPolicy movement, Random random) {
        this.tank = Objects.requireNonNull(tank);
        this.random = Objects.requireNonNull(random);
        Direction[] directions = Direction.values();
        commands = new Command[directions.length];
        for (int i = 0; i < directions.length; i++) {
            commands[i] = new MoveCommand(tank, directions[i], movement);
        }
    }

    @Override
    public void update() {
        if (tank.progress() == 1) commands[random.nextInt(commands.length)].execute();
    }
}
