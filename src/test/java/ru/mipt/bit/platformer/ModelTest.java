package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    @Test void directionsDefineStepsAndRotation() {
        Cell origin = new Cell(2, 3);
        assertEquals(new Cell(2, 4), origin.next(Direction.UP));
        assertEquals(new Cell(1, 3), origin.next(Direction.LEFT));
        assertEquals(new Cell(2, 2), origin.next(Direction.DOWN));
        assertEquals(new Cell(3, 3), origin.next(Direction.RIGHT));
        assertEquals(90, Direction.UP.rotation());
        assertEquals(-180, Direction.LEFT.rotation());
        assertEquals(-90, Direction.DOWN.rotation());
        assertEquals(0, Direction.RIGHT.rotation());
        assertEquals(new Cell(2, 3), origin);
    }

    @Test void fieldRejectsObstaclesAndEveryBoundary() {
        Tree tree = new Tree(new Cell(1, 2));
        Field field = new Field(3, 3, List.of(tree));
        assertEquals(new Cell(1, 2), tree.position());
        assertFalse(field.canEnter(tree.position()));
        for (Cell outside : List.of(new Cell(-1, 0), new Cell(0, -1), new Cell(3, 0), new Cell(0, 3))) {
            assertFalse(field.canEnter(outside));
        }
        assertTrue(field.canEnter(new Cell(0, 0)));
        assertEquals(3, field.width());
        assertEquals(3, field.height());
    }

    @Test void fieldOwnsAnImmutableObstacleList() {
        List<Tree> trees = new ArrayList<>();
        Field field = new Field(2, 2, trees);
        trees.add(new Tree(new Cell(0, 0)));
        assertTrue(field.trees().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> field.trees().add(trees.get(0)));
    }

    @Test void invalidModelsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Field(0, 1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Field(2, 2, List.of(new Tree(new Cell(2, 0)))));
        assertThrows(NullPointerException.class, () -> new Tree(null));
        assertThrows(NullPointerException.class, () -> new Tank(null, 0.4f));
    }

    @Test void tankFinishesOneMoveBeforeAcceptingAnother() {
        Tank tank = new Tank(new Cell(1, 1), 0.4f);
        Field field = new Field(4, 4, List.of());
        tank.move(Direction.UP, field);
        tank.update(0.2f);
        assertEquals(0.5f, tank.progress(), 0.0001f);
        assertEquals(new Cell(1, 1), tank.position());
        tank.move(Direction.RIGHT, field);
        assertEquals(Direction.UP, tank.direction());
        assertEquals(new Cell(1, 2), tank.destination());
        tank.update(1);
        assertEquals(new Cell(1, 2), tank.position());
        assertEquals(1, tank.progress());
        tank.move(Direction.RIGHT, field);
        tank.update(0.4f);
        assertEquals(new Cell(2, 2), tank.position());
    }

    @Test void blockedTankTurnsWithoutMoving() {
        Tank tank = new Tank(new Cell(0, 0), 0.4f);
        Field field = new Field(2, 2, List.of(new Tree(new Cell(0, 1))));
        tank.move(Direction.UP, field);
        assertEquals(Direction.UP, tank.direction());
        assertEquals(1, tank.progress());
        tank.move(Direction.LEFT, field);
        tank.update(1);
        assertEquals(new Cell(0, 0), tank.position());
        assertEquals(tank.position(), tank.destination());
    }

    @Test void invalidTimeDoesNotCorruptMovement() {
        Tank tank = new Tank(new Cell(0, 0), 0.4f);
        for (float delta : new float[] {-1, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> tank.update(delta));
        }
        assertEquals(1, tank.progress());
    }
}
