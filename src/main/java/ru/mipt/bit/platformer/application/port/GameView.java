package ru.mipt.bit.platformer.application.port;

/** Application output port: renders a frame without exposing a graphics library. */
public interface GameView extends AutoCloseable {
    void draw();
    @Override
    void close();
}
