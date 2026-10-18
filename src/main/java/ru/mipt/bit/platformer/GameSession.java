package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.graphics.GameView;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

public final class GameSession implements AutoCloseable {
    private final Field field;
    private final Tank tank;
    private final InputHandler input;
    private final GameView view;

    public GameSession(Field field, Tank tank, InputHandler input, GameView view) {
        this.field = Objects.requireNonNull(field);
        this.tank = Objects.requireNonNull(tank);
        this.input = Objects.requireNonNull(input);
        this.view = Objects.requireNonNull(view);
    }

    public void update(float delta) {
        input.update();
        tank.update(delta);
        view.draw(field, tank);
    }

    @Override
    public void close() { view.close(); }
}
