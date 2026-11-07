package ru.mipt.bit.platformer.level;

import java.util.Objects;

public final class FixedLevelSource implements LevelSource {
    private final LevelData level;

    public FixedLevelSource(LevelData level) { this.level = Objects.requireNonNull(level); }

    @Override
    public LevelData load() { return level; }
}
