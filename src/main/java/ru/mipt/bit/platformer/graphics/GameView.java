package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

public interface GameView extends AutoCloseable {
    void draw(Field field, Tank tank);
    @Override
    void close();
}
