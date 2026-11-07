package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.HealthDisplay;
import java.util.Objects;

public final class ToggleHealthCommand implements Command {
    private final HealthDisplay display;

    public ToggleHealthCommand(HealthDisplay display) { this.display = Objects.requireNonNull(display); }

    @Override
    public void execute() { display.toggle(); }
}
