package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.input.KeyboardController;
import ru.mipt.bit.platformer.model.*;

import java.util.List;

import static com.badlogic.gdx.Input.Keys.*;

public final class GameDesktopLauncher extends ApplicationAdapter {
    private GameRenderer renderer;
    private KeyboardController keyboard;
    private Field field;
    private Tank tank;

    @Override
    public void create() {
        renderer = new GameRenderer();
        field = new Field(renderer.width(), renderer.height(), List.of(new Tree(new Cell(1, 3))));
        tank = new Tank(new Cell(1, 1));
        keyboard = new KeyboardController(key -> Gdx.input.isKeyPressed(key));
        bindMovement(Direction.UP, UP, W);
        bindMovement(Direction.LEFT, LEFT, A);
        bindMovement(Direction.DOWN, DOWN, S);
        bindMovement(Direction.RIGHT, RIGHT, D);
    }

    private void bindMovement(Direction direction, int... keys) {
        for (int key : keys) keyboard.bind(key, () -> tank.move(direction, field));
    }

    @Override
    public void render() {
        keyboard.update();
        tank.update(Gdx.graphics.getDeltaTime());
        renderer.draw(field, tank);
    }

    @Override
    public void dispose() { renderer.close(); }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
