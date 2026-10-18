package ru.mipt.bit.platformer.input;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class KeyboardController implements InputHandler {
    private final IntPredicate pressed;
    private final Map<Integer, Runnable> bindings = new LinkedHashMap<>();

    public KeyboardController(IntPredicate pressed) {
        this.pressed = Objects.requireNonNull(pressed);
    }

    public void bind(int key, Runnable action) {
        bindings.put(key, Objects.requireNonNull(action));
    }

    public void update() {
        bindings.forEach((key, action) -> {
            if (pressed.test(key)) action.run();
        });
    }
}
