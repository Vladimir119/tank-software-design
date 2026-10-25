package ru.mipt.bit.platformer.graphics;

import java.util.List;

import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

public interface GameView extends AutoCloseable {
    void draw(Field field, List<Tank> tanks);
    @Override
    void close();
}
