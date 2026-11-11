package ru.mipt.bit.platformer.application.port;

import java.util.List;
import ru.mipt.bit.platformer.input.Command;

/** Application input port: supplies commands for the next frame. */
@FunctionalInterface
public interface ControlStrategy {
    List<Command> commands();
}
