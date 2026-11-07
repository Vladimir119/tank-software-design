package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.Cell;
import ru.mipt.bit.platformer.model.Field;
import java.util.Objects;

public record LevelData(Field field, Cell spawn) {
    public LevelData {
        Objects.requireNonNull(field);
        Objects.requireNonNull(spawn);
        if (!field.canEnter(spawn)) throw new IllegalArgumentException("Spawn must be a free cell");
    }
}
