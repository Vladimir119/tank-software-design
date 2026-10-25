package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.graphics.GameView;
import ru.mipt.bit.platformer.input.ControlStrategy;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import java.util.List;
import java.util.Objects;

public final class GameSession implements AutoCloseable {
    private final Field field;
    private final List<Tank> tanks;
    private final List<ControlStrategy> controls;
    private final GameView view;

    public GameSession(Field field, List<Tank> tanks, List<ControlStrategy> controls, GameView view) {
        this.field = Objects.requireNonNull(field);
        this.tanks = List.copyOf(tanks);
        this.controls = List.copyOf(controls);
        this.view = Objects.requireNonNull(view);
    }

    public void update(float delta) {
        if (!Float.isFinite(delta) || delta < 0) {
            throw new IllegalArgumentException("Delta must be finite and nonnegative");
        }
        for (ControlStrategy control : controls) control.update();
        for (Tank tank : tanks) tank.update(delta);
        view.draw(field, tanks);
    }

    @Override
    public void close() { view.close(); }
}
