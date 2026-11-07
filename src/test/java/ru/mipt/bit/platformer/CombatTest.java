package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.graphics.*;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CombatTest {
    private Tank tank(int x, int y) { return new Tank(new Cell(x, y), 0.4f); }
    private GameWorld world(Tank... tanks) { return new GameWorld(new Field(10, 4, List.of()), List.of(tanks)); }
    private List<Bullet> bullets(GameWorld world) {
        return world.objects().stream().filter(Bullet.class::isInstance).map(Bullet.class::cast).toList();
    }

    @Test void shotCreatesBulletInFrontWithCurrentDirection() {
        Tank player = tank(2, 1);
        GameWorld world = world(player);
        player.move(Direction.UP, cell -> false);
        new ShootCommand(player, world).execute();
        Bullet bullet = bullets(world).get(0);
        assertEquals(new Cell(2, 2), bullet.position());
        assertEquals(Direction.UP, bullet.direction());
        assertSame(player, bullet.owner());
        assertEquals(25, bullet.damage());
        world.live(0.13f);
        assertEquals(new Cell(2, 3), bullet.position());
    }

    @Test void adjacentTankTakesDamageImmediatelyAndDiesAtZero() {
        Tank player = tank(1, 1);
        Tank target = tank(2, 1);
        GameWorld world = world(player, target);
        for (int i = 1; i <= 4; i++) {
            new ShootCommand(player, world).execute();
            assertEquals(100 - i * 25, target.health());
            assertTrue(bullets(world).isEmpty());
            world.live(0.5f);
        }
        assertFalse(target.alive());
        assertFalse(world.contains(target));
        assertTrue(world.canEnter(new Cell(2, 1)));
        new ShootCommand(target, world).execute();
        target.move(Direction.UP, world);
        assertTrue(bullets(world).isEmpty());
        assertEquals(new Cell(2, 1), target.destination());
        assertEquals(100, player.health());
    }

    @Test void heldFireRespectsCooldown() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        ShootCommand shoot = new ShootCommand(player, world);
        shoot.execute();
        shoot.execute();
        assertEquals(1, bullets(world).size());
        world.live(0.2f);
        shoot.execute();
        assertEquals(1, bullets(world).size());
        world.live(0.21f);
        shoot.execute();
        assertEquals(2, bullets(world).size());
    }

    @Test void bulletStopsAtTreeWithoutRemovingIt() {
        Tank player = tank(0, 0);
        Tree tree = new Tree(new Cell(3, 0));
        GameWorld world = new GameWorld(new Field(10, 4, List.of(tree)), List.of(player));
        player.shoot(world);
        world.live(1);
        assertTrue(bullets(world).isEmpty());
        assertTrue(world.contains(tree));
        assertFalse(world.canEnter(tree.position()));
    }

    @Test void shotIntoAdjacentTreeOrOutOfBoundsDisappears() {
        for (Direction direction : Direction.values()) {
            Tank player = tank(0, 0);
            player.move(direction, cell -> false);
            GameWorld world = new GameWorld(new Field(1, 1, List.of()), List.of(player));
            player.shoot(world);
            assertTrue(bullets(world).isEmpty());
        }
        Tank player = tank(0, 0);
        GameWorld world = new GameWorld(new Field(2, 1, List.of(new Tree(new Cell(1, 0)))), List.of(player));
        player.shoot(world);
        assertTrue(bullets(world).isEmpty());
    }

    @Test void oppositeBulletsDestroyEachOther() {
        Tank left = tank(0, 0);
        Tank right = tank(5, 0);
        right.move(Direction.LEFT, cell -> false);
        GameWorld world = world(left, right);
        left.shoot(world);
        right.shoot(world);
        world.live(0.5f);
        assertTrue(bullets(world).isEmpty());
        assertEquals(100, left.health());
        assertEquals(100, right.health());
    }

    @Test void bulletsCreatedInSameCellCollideImmediately() {
        Tank left = tank(0, 0);
        Tank right = tank(2, 0);
        right.move(Direction.LEFT, cell -> false);
        GameWorld world = world(left, right);
        left.shoot(world);
        right.shoot(world);
        assertTrue(bullets(world).isEmpty());
        assertEquals(100, left.health());
        assertEquals(100, right.health());
    }

    @Test void largeTickDoesNotSkipTargetsAndUsesBulletDamage() {
        Tank player = tank(0, 0);
        Tank target = tank(4, 0);
        GameWorld world = world(player, target);
        world.add(new Bullet(new Cell(1, 0), Direction.RIGHT, player, 37, 0.01f));
        world.live(2);
        assertEquals(63, target.health());
        assertTrue(bullets(world).isEmpty());
    }

    @Test void bothCellsOfMovingTankCanBeHit() {
        for (int x : new int[] {2, 3}) {
            Tank player = tank(0, 0);
            Tank target = tank(2, 1);
            GameWorld world = world(player, target);
            target.move(Direction.RIGHT, world);
            target.update(0.2f);
            world.add(new Bullet(new Cell(x, 1), Direction.UP, player, 25, 0.12f));
            assertEquals(75, target.health());
            assertTrue(bullets(world).isEmpty());
        }
    }

    @Test void movingShooterDoesNotHitItself() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        player.move(Direction.RIGHT, world);
        player.shoot(world);
        world.live(0.5f);
        assertEquals(100, player.health());
        assertEquals(new Cell(1, 0), player.position());
    }

    @Test void deadOrRemovedTankCannotShootAndDamageIsClamped() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        world.remove(player);
        player.shoot(world);
        assertTrue(bullets(world).isEmpty());
        player.damage(Integer.MAX_VALUE);
        assertEquals(0, player.health());
        assertThrows(IllegalArgumentException.class, () -> player.damage(-1));
        assertThrows(IllegalArgumentException.class, () -> world.add(player));
    }

    @Test void observerReceivesInitialObjectsAndRuntimeChanges() {
        Tank player = tank(0, 0);
        Tree tree = new Tree(new Cell(0, 1));
        GameWorld world = new GameWorld(new Field(5, 3, List.of(tree)), List.of(player));
        SceneObjects scene = new SceneObjects();
        world.subscribe(scene);
        world.subscribe(scene);
        assertEquals(world.objects(), scene.objects());
        player.shoot(world);
        assertEquals(3, scene.objects().size());
        assertEquals(world.objects(), scene.objects());
        world.live(2);
        assertEquals(List.of(tree, player), scene.objects());
        player.damage(100);
        world.live(0);
        assertEquals(List.of(tree), scene.objects());
        world.unsubscribe(scene);
        world.remove(tree);
        assertEquals(List.of(tree), scene.objects());
        assertThrows(UnsupportedOperationException.class, () -> scene.objects().clear());
    }

    @Test void observerGetsOneRemovalPerObject() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        List<GameObject> removed = new ArrayList<>();
        world.subscribe(new WorldListener() {
            public void added(GameObject object) { }
            public void removed(GameObject object) { removed.add(object); }
        });
        world.remove(player);
        world.remove(player);
        assertEquals(List.of(player), removed);
    }

    @Test void healthToggleRunsOncePerKeyPress() {
        HealthDisplay health = new HealthDisplay();
        Set<Integer> pressed = new HashSet<>();
        KeyboardController keyboard = new KeyboardController(pressed::contains);
        keyboard.bindOnPress(1, new ToggleHealthCommand(health));
        assertFalse(health.visible());
        pressed.add(1);
        List<Command> pending = keyboard.commands();
        assertFalse(health.visible());
        pending.forEach(Command::execute);
        assertTrue(health.visible());
        for (int i = 0; i < 10; i++) keyboard.commands().forEach(Command::execute);
        assertTrue(health.visible());
        pressed.clear();
        keyboard.commands();
        pressed.add(1);
        keyboard.commands().forEach(Command::execute);
        assertFalse(health.visible());
    }

    @Test void aiCanChooseShootingAndDoesNotControlDeadTanks() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        Random random = new Random() {
            @Override public int nextInt(int bound) { return bound - 1; }
        };
        RandomController ai = new RandomController(player, world, random, new ShootCommand(player, world));
        List<Command> pending = ai.commands();
        assertTrue(bullets(world).isEmpty());
        pending.forEach(Command::execute);
        assertEquals(1, bullets(world).size());
        player.damage(100);
        assertTrue(ai.commands().isEmpty());
    }

    @Test void gameLoopCollectsAllCommandsBeforeExecutingAndDrawing() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        List<String> events = new ArrayList<>();
        ControlStrategy first = () -> {
            events.add("collect1");
            return List.of(() -> { events.add("execute1"); player.move(Direction.RIGHT, world); });
        };
        ControlStrategy second = () -> {
            events.add("collect2");
            assertEquals(1, player.progress());
            return List.of(() -> events.add("execute2"));
        };
        GameView view = new GameView() {
            public void draw() {
                assertEquals(new Cell(1, 0), player.position());
                events.add("draw");
            }
            public void close() { events.add("close"); }
        };
        try (GameSession session = new GameSession(world, List.of(first, second), view)) {
            session.update(0.5f);
        }
        assertEquals(List.of("collect1", "collect2", "execute1", "execute2", "draw", "close"), events);
    }

    @Test void invalidBulletSettingsAndTimeAreRejected() {
        Tank player = tank(0, 0);
        GameWorld world = world(player);
        assertThrows(IllegalArgumentException.class, () -> new Bullet(new Cell(1, 0), Direction.RIGHT, player, 0, 1));
        for (float value : new float[] {0, -1, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Bullet(new Cell(1, 0), Direction.RIGHT, player, 25, value));
        }
        Bullet bullet = new Bullet(new Cell(1, 0), Direction.RIGHT, player, 25, 1);
        for (float delta : new float[] {-1, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> world.live(delta));
            assertThrows(IllegalArgumentException.class, () -> bullet.live(delta, world));
        }
    }
}
