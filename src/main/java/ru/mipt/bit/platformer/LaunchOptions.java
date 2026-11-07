package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.level.*;
import java.nio.file.Path;
import java.util.Random;

public record LaunchOptions(Path levelFile, Long seed, int trees) {
    public static LaunchOptions parse(String[] args) {
        Path file = null;
        Long seed = null;
        Integer trees = null;
        boolean random = false;
        for (String arg : args) {
            if (arg.startsWith("--level=") && file == null && arg.length() > 8) {
                file = Path.of(arg.substring(8));
            } else if (arg.startsWith("--seed=") && seed == null) {
                seed = Long.parseLong(arg.substring(7));
            } else if (arg.startsWith("--trees=") && trees == null) {
                trees = Integer.parseInt(arg.substring(8));
            } else if (arg.equals("--random") && !random) {
                random = true;
            } else {
                throw new IllegalArgumentException("Unknown or duplicate argument: " + arg);
            }
        }
        if (file != null && (random || seed != null || trees != null)) {
            throw new IllegalArgumentException("--level cannot be combined with random level options");
        }
        if (trees != null && (trees < 0 || trees >= 80)) {
            throw new IllegalArgumentException("--trees must be between 0 and 79");
        }
        return new LaunchOptions(file, seed, trees == null ? 18 : trees);
    }

    public LevelSource source() {
        if (levelFile != null) return new FileLevelSource(levelFile, new TextLevelParser());
        return new RandomLevelSource(10, 8, trees, seed == null ? new Random() : new Random(seed));
    }
}
