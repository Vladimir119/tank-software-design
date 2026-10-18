package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.input.KeyboardController;
import ru.mipt.bit.platformer.level.*;
import ru.mipt.bit.platformer.model.*;

import java.util.List;
import java.util.Objects;
import static com.badlogic.gdx.Input.Keys.*;

public final class GameDesktopLauncher extends ApplicationAdapter {
    private final LevelSource levels;
    private final float moveDuration;
    private GameSession session;

    public GameDesktopLauncher(LevelSource levels, float moveDuration) {
        this.levels = Objects.requireNonNull(levels);
        this.moveDuration = moveDuration;
    }

    @Override
    public void create() {
        LevelData level = levels.load();
        GameRenderer renderer = new GameRenderer("level.tmx", "images/tank_blue.png", "images/greenTree.png");
        if (level.field().width() != renderer.width() || level.field().height() != renderer.height()) {
            renderer.close();
            throw new IllegalArgumentException("Level dimensions must match the ground map");
        }
        Tank tank = new Tank(level.spawn(), moveDuration);
        KeyboardController keyboard = new KeyboardController(key -> Gdx.input.isKeyPressed(key));
        bindMovement(keyboard, tank, level.field(), Direction.UP, UP, W);
        bindMovement(keyboard, tank, level.field(), Direction.LEFT, LEFT, A);
        bindMovement(keyboard, tank, level.field(), Direction.DOWN, DOWN, S);
        bindMovement(keyboard, tank, level.field(), Direction.RIGHT, RIGHT, D);
        session = new GameSession(level.field(), tank, keyboard, renderer);
    }

    private static void bindMovement(KeyboardController keyboard, Tank tank, MovementPolicy movement,
                                     Direction direction, int... keys) {
        for (int key : keys) keyboard.bind(key, () -> tank.move(direction, movement));
    }

    @Override
    public void render() { session.update(Gdx.graphics.getDeltaTime()); }

    @Override
    public void dispose() { if (session != null) session.close(); }

    public static void main(String[] args) {
        LevelSource source = new FixedLevelSource(new LevelData(
                new Field(10, 8, List.of(new Tree(new Cell(1, 3)))), new Cell(1, 1)));
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(source, 0.4f), config);
    }
}
