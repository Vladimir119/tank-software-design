package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.application.port.LevelSource;
import java.util.Objects;

/** Infrastructure output adapter: supplies an existing level. */
public final class FixedLevelSource implements LevelSource {
    private final LevelData level;

    public FixedLevelSource(LevelData level) { this.level = Objects.requireNonNull(level); }

    @Override
    public LevelData load() { return level; }
}
