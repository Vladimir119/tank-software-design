package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.graphics.GameView;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.model.GameWorld;
import java.util.*;

public final class GameSession implements AutoCloseable {
    private final GameWorld world;
    private final List<ControlStrategy> controls;
    private final GameView view;

    public GameSession(GameWorld world, List<ControlStrategy> controls, GameView view) {
        this.world = Objects.requireNonNull(world);
        this.controls = List.copyOf(controls);
        this.view = Objects.requireNonNull(view);
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0) throw new IllegalArgumentException("Invalid delta");
        List<Command> commands = new ArrayList<>();
        for (ControlStrategy control : controls) commands.addAll(control.commands());
        for (Command command : commands) command.execute();
        world.live(delta);
        view.draw();
    }

    @Override
    public void close() { view.close(); }
}
