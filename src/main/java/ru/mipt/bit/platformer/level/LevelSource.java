package ru.mipt.bit.platformer.level;

@FunctionalInterface
public interface LevelSource {
    LevelData load();
}
