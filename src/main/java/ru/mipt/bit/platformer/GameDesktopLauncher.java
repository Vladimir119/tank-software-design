package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.application.port.LevelSource;
import ru.mipt.bit.platformer.application.port.ControlStrategy;
import ru.mipt.bit.platformer.application.port.GameView;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.graphics.*;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.level.*;
import ru.mipt.bit.platformer.model.*;

import java.util.*;
import static com.badlogic.gdx.Input.Keys.*;

/** Infrastructure: composition root and LibGDX lifecycle adapter. */
public final class GameDesktopLauncher extends ApplicationAdapter {
    private final LevelSource levels;
    private final float moveDuration;
    private GameSession session;
    private GameWorld world;
    private SceneObjects scene;

    public GameDesktopLauncher(LevelSource levels, float moveDuration) {
        this.levels = Objects.requireNonNull(levels);
        this.moveDuration = moveDuration;
    }

    @Override
    public void create() {
        LevelData level = levels.load();
        Random random = new Random();
        List<Tank> tanks = new TankSpawner(random).spawn(level, 5, moveDuration);
        world = new GameWorld(level.field(), tanks);
        scene = new SceneObjects();
        world.subscribe(scene);
        Tank player = tanks.get(0);
        GameRenderer renderer = new GameRenderer("level.tmx", "images/tank_blue.png", "images/greenTree.png",
                level.field().width(), level.field().height(), scene, player);
        HealthDisplay health = new HealthDisplay();
        GameView view = new HealthBarView(renderer, scene, health, renderer.camera(), renderer.tileWidth(), renderer.tileHeight());
        KeyboardController keyboard = new KeyboardController(key -> Gdx.input.isKeyPressed(key));
        bindMovement(keyboard, player, world, Direction.UP, UP, W);
        bindMovement(keyboard, player, world, Direction.LEFT, LEFT, A);
        bindMovement(keyboard, player, world, Direction.DOWN, DOWN, S);
        bindMovement(keyboard, player, world, Direction.RIGHT, RIGHT, D);
        keyboard.bind(SPACE, new ShootCommand(player, world));
        keyboard.bindOnPress(L, new ToggleHealthCommand(health));
        List<ControlStrategy> controls = new ArrayList<>();
        controls.add(keyboard);
        for (int i = 1; i < tanks.size(); i++) {
            controls.add(new RandomController(tanks.get(i), world, random, new ShootCommand(tanks.get(i), world)));
        }
        session = new GameSession(world, controls, view);
    }

    private static void bindMovement(KeyboardController keyboard, Tank tank, MovementPolicy movement,
                                     Direction direction, int... keys) {
        Command command = new MoveCommand(tank, direction, movement);
        for (int key : keys) keyboard.bind(key, command);
    }

    @Override
    public void render() { session.update(Gdx.graphics.getDeltaTime()); }

    @Override
    public void dispose() {
        if (world != null && scene != null) world.unsubscribe(scene);
        if (session != null) session.close();
    }

    public static void main(String[] args) {
        LevelSource source = LaunchOptions.parse(args).source();
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(source, 0.4f), config);
    }
}
