package ru.mipt.bit.platformer.input;

@FunctionalInterface
public interface Command {
    void execute();
}
