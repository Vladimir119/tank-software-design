package ru.mipt.bit.platformer.model;

import java.util.List;

public final class Field {
    private final int width;
    private final int height;
    private final List<Tree> trees;

    public Field(int width, int height, List<Tree> trees) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.trees = List.copyOf(trees);
        if (trees.stream().anyMatch(tree -> !contains(tree.position()))) {
            throw new IllegalArgumentException("Tree outside field");
        }
    }

    public boolean contains(Cell cell) {
        return cell.x() >= 0 && cell.x() < width && cell.y() >= 0 && cell.y() < height;
    }

    public boolean canEnter(Cell cell) {
        return contains(cell) && trees.stream().noneMatch(tree -> tree.position().equals(cell));
    }

    public int width() { return width; }
    public int height() { return height; }
    public List<Tree> trees() { return trees; }
}
