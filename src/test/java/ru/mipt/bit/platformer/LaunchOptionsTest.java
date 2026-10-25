package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.level.*;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class LaunchOptionsTest {
    @Test void defaultsToRandomAndAllowsRepeatableSettings() {
        assertInstanceOf(RandomLevelSource.class, LaunchOptions.parse(new String[0]).source());
        LaunchOptions options = LaunchOptions.parse(new String[] {"--random", "--seed=42", "--trees=3"});
        assertEquals(42L, options.seed());
        assertEquals(3, options.source().load().field().trees().size());
    }

    @Test void selectsFileSource() {
        LaunchOptions options = LaunchOptions.parse(new String[] {"--level=some level.txt"});
        assertEquals(Path.of("some level.txt"), options.levelFile());
        assertInstanceOf(FileLevelSource.class, options.source());
    }

    @Test void rejectsTyposDuplicatesAndConflictingOptions() {
        for (String[] args : new String[][] {
                {"--bad"}, {"--level="}, {"--trees=80"}, {"--trees=-1"}, {"--seed=abc"},
                {"--level=a", "--random"}, {"--level=a", "--seed=1"}, {"--level=a", "--trees=0"},
                {"--seed=1", "--seed=2"}, {"--random", "--random"}, {"--level=a", "--level=b"}}) {
            assertThrows(IllegalArgumentException.class, () -> LaunchOptions.parse(args));
        }
    }
}
