package ru.mipt.bit.platformer.input;

import java.util.*;
import java.util.function.IntPredicate;

public final class KeyboardController implements ControlStrategy {
    private final IntPredicate pressed;
    private final Map<Integer, Command> bindings = new LinkedHashMap<>();
    private final Set<Integer> onPress = new HashSet<>();
    private final Set<Integer> held = new HashSet<>();

    public KeyboardController(IntPredicate pressed) {
        this.pressed = Objects.requireNonNull(pressed);
    }

    public void bind(int key, Command action) {
        bindings.put(key, Objects.requireNonNull(action));
        onPress.remove(key);
    }

    public void bindOnPress(int key, Command action) {
        bind(key, action);
        onPress.add(key);
    }

    @Override
    public List<Command> commands() {
        List<Command> result = new ArrayList<>();
        bindings.forEach((key, action) -> {
            if (pressed.test(key)) {
                if (!onPress.contains(key) || !held.contains(key)) result.add(action);
                held.add(key);
            } else held.remove(key);
        });
        return result;
    }
}
