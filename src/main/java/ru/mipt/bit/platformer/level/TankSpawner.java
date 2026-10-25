package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.*;
import java.util.*;

public final class TankSpawner {
    private final Random random;

    public TankSpawner(Random random) { this.random = Objects.requireNonNull(random); }

    public List<Tank> spawn(LevelData level, int botCount, float moveDuration) {
        if (botCount < 0) throw new IllegalArgumentException("Bot count must be nonnegative");
        List<Tank> tanks = new ArrayList<>();
        tanks.add(new Tank(level.spawn(), moveDuration));
        List<Cell> free = new ArrayList<>();
        for (int y = 0; y < level.field().height(); y++) {
            for (int x = 0; x < level.field().width(); x++) {
                Cell cell = new Cell(x, y);
                if (level.field().canEnter(cell) && !cell.equals(level.spawn())) free.add(cell);
            }
        }
        Collections.shuffle(free, random);
        for (int i = 0; i < Math.min(botCount, free.size()); i++) {
            tanks.add(new Tank(free.get(i), moveDuration));
        }
        return List.copyOf(tanks);
    }
}
