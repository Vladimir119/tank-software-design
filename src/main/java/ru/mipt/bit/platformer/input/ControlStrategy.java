package ru.mipt.bit.platformer.input;

import java.util.List;

@FunctionalInterface
public interface ControlStrategy {
    List<Command> commands();
}
