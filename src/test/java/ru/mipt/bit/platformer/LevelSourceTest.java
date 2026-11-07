package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.mipt.bit.platformer.level.*;
import ru.mipt.bit.platformer.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LevelSourceTest {
    @TempDir Path directory;

    @Test void readsCoordinatesFromTopToBottomAndAcceptsWindowsNewlines() throws IOException {
        LevelData level = new TextLevelParser().parse(new StringReader("\uFEFFT_ \r\n_XT\r\n"));
        assertEquals(3, level.field().width());
        assertEquals(2, level.field().height());
        assertEquals(new Cell(1, 0), level.spawn());
        assertEquals(List.of(new Tree(new Cell(0, 1)), new Tree(new Cell(2, 0))), level.field().trees());
    }

    @Test void rejectsMalformedLevels() {
        for (String text : List.of("", "\n", "___", "XX", "X_\n_", "X?", "X_\n\n")) {
            assertThrows(IllegalArgumentException.class,
                    () -> new TextLevelParser().parse(new StringReader(text)), text);
        }
    }

    @Test void readsActualFileAndReportsMissingFile() throws IOException {
        Path file = directory.resolve("level.txt");
        Files.writeString(file, "T_\n_X\n");
        LevelData level = new FileLevelSource(file, new TextLevelParser()).load();
        assertEquals(new Cell(1, 0), level.spawn());
        assertThrows(UncheckedIOException.class,
                () -> new FileLevelSource(directory.resolve("missing.txt"), new TextLevelParser()).load());
    }

    @Test void randomLevelsHaveExactCountUniqueTreesAndFreeSpawn() {
        Set<Cell> spawns = new HashSet<>();
        for (int seed = 0; seed < 100; seed++) {
            LevelData level = new RandomLevelSource(10, 8, 18, new Random(seed)).load();
            Set<Cell> trees = new HashSet<>();
            for (Tree tree : level.field().trees()) {
                assertTrue(level.field().contains(tree.position()));
                assertTrue(trees.add(tree.position()));
            }
            assertEquals(18, trees.size());
            assertTrue(level.field().canEnter(level.spawn()));
            spawns.add(level.spawn());
        }
        assertTrue(spawns.size() > 1);
    }

    @Test void seededGenerationIsReproducible() {
        LevelData a = new RandomLevelSource(5, 4, 7, new Random(123)).load();
        LevelData b = new RandomLevelSource(5, 4, 7, new Random(123)).load();
        assertEquals(a.spawn(), b.spawn());
        assertEquals(a.field().trees(), b.field().trees());
    }

    @Test void handlesSmallAndAlmostFullFieldsAndRejectsInvalidCounts() {
        LevelData single = new RandomLevelSource(1, 1, 0, new Random(0)).load();
        assertEquals(new Cell(0, 0), single.spawn());
        assertTrue(single.field().trees().isEmpty());
        LevelData full = new RandomLevelSource(2, 2, 3, new Random(0)).load();
        assertEquals(3, full.field().trees().size());
        assertTrue(full.field().canEnter(full.spawn()));
        for (int count : new int[] {-1, 4, 5}) {
            assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, count, new Random()));
        }
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(0, 1, 0, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(Integer.MAX_VALUE, 2, 0, new Random()));
    }
}
