package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.*;
import java.io.*;
import java.util.*;

public final class TextLevelParser {
    public LevelData parse(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> rows = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) rows.add(line);
        if (rows.isEmpty()) throw new IllegalArgumentException("Level is empty");
        if (rows.get(0).startsWith("\uFEFF")) rows.set(0, rows.get(0).substring(1));
        int width = rows.get(0).length();
        if (width == 0) throw new IllegalArgumentException("Level has an empty row");
        List<Tree> trees = new ArrayList<>();
        Cell spawn = null;
        for (int row = 0; row < rows.size(); row++) {
            String text = rows.get(row);
            if (text.length() != width) {
                throw new IllegalArgumentException("Unequal row width at line " + (row + 1));
            }
            for (int x = 0; x < width; x++) {
                Cell cell = new Cell(x, rows.size() - row - 1);
                switch (text.charAt(x)) {
                    case 'T' -> trees.add(new Tree(cell));
                    case 'X' -> {
                        if (spawn != null) throw new IllegalArgumentException("Level has multiple spawns");
                        spawn = cell;
                    }
                    case '_', ' ' -> { }
                    default -> throw new IllegalArgumentException(
                            "Unknown symbol at line " + (row + 1) + ", column " + (x + 1));
                }
            }
        }
        if (spawn == null) throw new IllegalArgumentException("Level must contain one X");
        return new LevelData(new Field(width, rows.size(), trees), spawn);
    }
}
