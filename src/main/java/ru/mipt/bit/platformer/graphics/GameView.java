package ru.mipt.bit.platformer.graphics;

public interface GameView extends AutoCloseable {
    void draw();
    @Override
    void close();
}
