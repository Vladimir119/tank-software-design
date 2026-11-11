package ru.mipt.bit.platformer.input;

/** Application input port: a single game action. */
@FunctionalInterface
public interface Command {
    void execute();
}
