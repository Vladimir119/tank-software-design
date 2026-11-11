package ru.mipt.bit.platformer.application.port;

import ru.mipt.bit.platformer.level.LevelData;

/** Application output port: loads or generates initial level data. */
@FunctionalInterface
public interface LevelSource {
    LevelData load();
}
