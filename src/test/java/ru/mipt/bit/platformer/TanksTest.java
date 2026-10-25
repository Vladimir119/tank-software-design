package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.graphics.GameView;
import ru.mipt.bit.platformer.input.*;
import ru.mipt.bit.platformer.level.*;
import ru.mipt.bit.platformer.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TanksTest {
    @Test void movingTankReservesBothCellsUntilArrival() {
        Tank first = new Tank(new Cell(1, 1), 1);
        Tank second = new Tank(new Cell(0, 1), 1);
        Tank third = new Tank(new Cell(2, 2), 1);
        TankMovement movement = new TankMovement(new Field(4, 4, List.of()), List.of(first, second, third));
        new MoveCommand(first, Direction.RIGHT, movement).execute();
        first.update(0.5f);
        assertFalse(movement.canEnter(new Cell(1, 1)));
        assertFalse(movement.canEnter(new Cell(2, 1)));
        new MoveCommand(second, Direction.RIGHT, movement).execute();
        new MoveCommand(third, Direction.DOWN, movement).execute();
        assertEquals(1, second.progress());
        assertEquals(1, third.progress());
        first.update(0.5f);
        assertTrue(movement.canEnter(new Cell(1, 1)));
        assertFalse(movement.canEnter(new Cell(2, 1)));
        new MoveCommand(second, Direction.RIGHT, movement).execute();
        assertEquals(0, second.progress());
    }

    @Test void onlyFirstTankCanReserveAnEmptyCell() {
        Tank left = new Tank(new Cell(0, 0), 1);
        Tank right = new Tank(new Cell(2, 0), 1);
        TankMovement movement = new TankMovement(new Field(3, 1, List.of()), List.of(left, right));
        new MoveCommand(left, Direction.RIGHT, movement).execute();
        new MoveCommand(right, Direction.LEFT, movement).execute();
        assertEquals(new Cell(1, 0), left.destination());
        assertEquals(new Cell(2, 0), right.destination());
        assertEquals(1, right.progress());
    }

    @Test void tanksCannotSwapPlacesOrPassTreesAndBoundaries() {
        Tank first = new Tank(new Cell(0, 0), 1);
        Tank second = new Tank(new Cell(1, 0), 1);
        TankMovement movement = new TankMovement(
                new Field(2, 2, List.of(new Tree(new Cell(0, 1)))), List.of(first, second));
        for (Direction direction : Direction.values()) new MoveCommand(first, direction, movement).execute();
        new MoveCommand(second, Direction.LEFT, movement).execute();
        assertEquals(1, first.progress());
        assertEquals(1, second.progress());
        for (Cell outside : List.of(new Cell(-1, 0), new Cell(0, -1), new Cell(2, 0), new Cell(0, 2))) {
            assertFalse(movement.canEnter(outside));
        }
        assertTrue(movement.canEnter(new Cell(1, 1)));
    }

    @Test void randomControllerChoosesAgainOnlyAfterArrival() {
        Tank tank = new Tank(new Cell(1, 1), 1);
        int[] calls = {0};
        Random random = new Random() {
            @Override public int nextInt(int bound) { calls[0]++; return Direction.UP.ordinal(); }
        };
        RandomController controller = new RandomController(tank, new Field(4, 4, List.of()), random);
        controller.update();
        tank.update(0.5f);
        controller.update();
        assertEquals(1, calls[0]);
        assertEquals(new Cell(1, 2), tank.destination());
        tank.update(0.5f);
        controller.update();
        assertEquals(2, calls[0]);
        assertEquals(new Cell(1, 3), tank.destination());
    }

    @Test void blockedBotCanChooseAnotherDirection() {
        Tank tank = new Tank(new Cell(0, 0), 1);
        Random random = new Random() {
            private int choice;
            @Override public int nextInt(int bound) {
                return choice++ == 0 ? Direction.LEFT.ordinal() : Direction.UP.ordinal();
            }
        };
        RandomController controller = new RandomController(tank, new Field(2, 2, List.of()), random);
        controller.update();
        assertEquals(1, tank.progress());
        controller.update();
        assertEquals(0, tank.progress());
        assertEquals(new Cell(0, 1), tank.destination());
    }

    @Test void spawnerUsesDifferentFreeCellsAndKeepsPlayerFirst() {
        LevelData level = new LevelData(new Field(5, 4, List.of(new Tree(new Cell(2, 2)))), new Cell(1, 1));
        Set<Cell> positions = new HashSet<>();
        for (int seed = 0; seed < 30; seed++) {
            List<Tank> tanks = new TankSpawner(new Random(seed)).spawn(level, 5, 0.4f);
            assertEquals(6, tanks.size());
            assertEquals(level.spawn(), tanks.get(0).position());
            Set<Cell> occupied = new HashSet<>();
            for (Tank tank : tanks) {
                assertTrue(level.field().canEnter(tank.position()));
                assertTrue(occupied.add(tank.position()));
                assertEquals(tank.position(), tank.destination());
            }
            positions.add(tanks.get(1).position());
        }
        assertTrue(positions.size() > 1);
        List<Tank> first = new TankSpawner(new Random(42)).spawn(level, 5, 0.4f);
        List<Tank> second = new TankSpawner(new Random(42)).spawn(level, 5, 0.4f);
        assertEquals(first.stream().map(Tank::position).toList(), second.stream().map(Tank::position).toList());
        assertThrows(UnsupportedOperationException.class, () -> first.add(first.get(0)));
    }

    @Test void spawnerHandlesCrowdedLevelsAndInvalidCounts() {
        TankSpawner spawner = new TankSpawner(new Random(1));
        LevelData tiny = new LevelData(new Field(1, 1, List.of()), new Cell(0, 0));
        assertEquals(1, spawner.spawn(tiny, 5, 1).size());
        LevelData small = new LevelData(new Field(2, 2, List.of(new Tree(new Cell(1, 1)))), new Cell(0, 0));
        assertEquals(3, spawner.spawn(small, 5, 1).size());
        assertEquals(1, spawner.spawn(small, 0, 1).size());
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(small, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(small, 2, 0));
    }

    @Test void keyboardAndAiUseTheSameCommandsWithoutTakingOverPlayer() {
        Tank player = new Tank(new Cell(0, 0), 1);
        Tank bot = new Tank(new Cell(3, 0), 1);
        Field field = new Field(5, 5, List.of());
        TankMovement movement = new TankMovement(field, List.of(player, bot));
        Set<Integer> pressed = new HashSet<>();
        KeyboardController keyboard = new KeyboardController(pressed::contains);
        keyboard.bind(1, new MoveCommand(player, Direction.UP, movement));
        RandomController ai = new RandomController(bot, movement, new Random() {
            @Override public int nextInt(int bound) { return Direction.UP.ordinal(); }
        });
        List<List<Cell>> frames = new ArrayList<>();
        GameView view = new GameView() {
            public void draw(Field actual, List<Tank> tanks) {
                frames.add(tanks.stream().map(Tank::position).toList());
            }
            public void close() { }
        };
        try (GameSession session = new GameSession(field, List.of(player, bot), List.of(keyboard, ai), view)) {
            session.update(1);
            assertEquals(List.of(new Cell(0, 0), new Cell(3, 1)), frames.get(0));
            pressed.add(1);
            for (float delta : new float[] {-1, Float.NaN, Float.POSITIVE_INFINITY}) {
                assertThrows(IllegalArgumentException.class, () -> session.update(delta));
            }
            assertEquals(1, player.progress());
            session.update(1);
            assertEquals(List.of(new Cell(0, 1), new Cell(3, 2)), frames.get(1));
        }
    }

    @Test void manyAiStepsNeverOverlapReservations() {
        LevelData level = new LevelData(new Field(8, 6, List.of(new Tree(new Cell(2, 2)))), new Cell(0, 0));
        List<Tank> tanks = new TankSpawner(new Random(9)).spawn(level, 20, 0.4f);
        TankMovement movement = new TankMovement(level.field(), tanks);
        List<ControlStrategy> controls = new ArrayList<>();
        for (int i = 1; i < tanks.size(); i++) controls.add(new RandomController(tanks.get(i), movement, new Random(i)));
        for (int frame = 0; frame < 1000; frame++) {
            for (ControlStrategy control : controls) {
                control.update();
                assertNoOverlap(tanks, level.field());
            }
            for (Tank tank : tanks) tank.update(0.07f);
            assertNoOverlap(tanks, level.field());
        }
        assertEquals(level.spawn(), tanks.get(0).position());
    }

    private void assertNoOverlap(List<Tank> tanks, Field field) {
        Set<Cell> occupied = new HashSet<>();
        for (Tank tank : tanks) {
            assertTrue(field.canEnter(tank.position()));
            assertTrue(field.canEnter(tank.destination()));
            assertTrue(occupied.add(tank.position()));
            if (!tank.position().equals(tank.destination())) assertTrue(occupied.add(tank.destination()));
        }
    }
}
