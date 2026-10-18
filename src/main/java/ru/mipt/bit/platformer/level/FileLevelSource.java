package ru.mipt.bit.platformer.level;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;

public final class FileLevelSource implements LevelSource {
    private final Path path;
    private final TextLevelParser parser;

    public FileLevelSource(Path path, TextLevelParser parser) {
        this.path = Objects.requireNonNull(path);
        this.parser = Objects.requireNonNull(parser);
    }

    @Override
    public LevelData load() {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parser.parse(reader);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read level: " + path, e);
        }
    }
}
