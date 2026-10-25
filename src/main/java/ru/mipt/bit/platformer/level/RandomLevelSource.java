package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.*;
import java.util.*;

public final class RandomLevelSource implements LevelSource {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomLevelSource(int width, int height, int treeCount, Random random) {
        if (width <= 0 || height <= 0 || (long) width * height > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid field dimensions");
        }
        if (treeCount < 0 || treeCount >= (long) width * height) {
            throw new IllegalArgumentException("Trees must leave at least one free cell");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public LevelData load() {
        List<Cell> cells = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) cells.add(new Cell(x, y));
        }
        Collections.shuffle(cells, random);
        Cell spawn = cells.get(0);
        List<Tree> trees = new ArrayList<>();
        for (int i = 1; i <= treeCount; i++) trees.add(new Tree(cells.get(i)));
        return new LevelData(new Field(width, height, trees), spawn);
    }
}
