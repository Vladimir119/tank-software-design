package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;
import java.util.Objects;
import java.util.Random;
import java.util.List;

public final class RandomController implements ControlStrategy {
    private final Tank tank;
    private final Random random;
    private final Command[] commands;

    public RandomController(Tank tank, MovementPolicy movement, Random random, Command... extraActions) {
        this.tank = Objects.requireNonNull(tank);
        this.random = Objects.requireNonNull(random);
        Direction[] directions = Direction.values();
        commands = new Command[directions.length + extraActions.length];
        for (int i = 0; i < directions.length; i++) {
            commands[i] = new MoveCommand(tank, directions[i], movement);
        }
        for (int i = 0; i < extraActions.length; i++) {
            commands[directions.length + i] = Objects.requireNonNull(extraActions[i]);
        }
    }

    @Override
    public List<Command> commands() {
        if (!tank.alive() || tank.progress() < 1) return List.of();
        return List.of(commands[random.nextInt(commands.length)]);
    }
}
