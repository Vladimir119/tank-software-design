package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.graphics.GameView;
import ru.mipt.bit.platformer.level.*;
import ru.mipt.bit.platformer.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CompositionTest {
    @Test void movementRulesAndDurationCanBeReplaced() {
        Tank tank = new Tank(new Cell(1, 1), 2);
        tank.move(Direction.UP, cell -> false);
        assertEquals(1, tank.progress());
        tank.move(Direction.UP, cell -> true);
        tank.update(1);
        assertEquals(0.5f, tank.progress());
        tank.update(1);
        assertEquals(new Cell(1, 2), tank.position());
        for (float duration : new float[] {0, -1, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Tank(new Cell(0, 0), duration));
        }
    }

    @Test void sessionRunsInputThenModelThenViewAndClosesView() {
        Field field = new Field(3, 3, List.of());
        Tank tank = new Tank(new Cell(0, 0), 0.4f);
        List<String> events = new ArrayList<>();
        GameView view = new GameView() {
            public void draw(Field actualField, Tank actualTank) {
                assertSame(field, actualField);
                assertSame(tank, actualTank);
                assertEquals(new Cell(1, 0), tank.position());
                events.add("draw");
            }
            public void close() { events.add("close"); }
        };
        try (GameSession session = new GameSession(field, tank, () -> {
            events.add("input");
            tank.move(Direction.RIGHT, field);
        }, view)) {
            session.update(0.4f);
        }
        assertEquals(List.of("input", "draw", "close"), events);
    }

    @Test void levelValidatesSpawnAndFixedSourceReturnsIt() {
        Field field = new Field(3, 3, List.of(new Tree(new Cell(1, 1))));
        LevelData level = new LevelData(field, new Cell(0, 0));
        LevelSource source = new FixedLevelSource(level);
        assertSame(level, source.load());
        assertThrows(IllegalArgumentException.class, () -> new LevelData(field, new Cell(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new LevelData(field, new Cell(-1, 0)));
        assertThrows(NullPointerException.class, () -> new FixedLevelSource(null));
    }
}
