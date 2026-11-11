package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.input.KeyboardController;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardControllerTest {
    @Test void executesOnlyPressedBindingsInRegistrationOrder() {
        Set<Integer> pressed = new HashSet<>(List.of(1, 3));
        List<String> actions = new ArrayList<>();
        KeyboardController keyboard = new KeyboardController(pressed::contains);
        keyboard.bind(1, () -> actions.add("move"));
        keyboard.bind(2, () -> actions.add("unused"));
        keyboard.bind(3, () -> actions.add("fire"));
        keyboard.commands().forEach(ru.mipt.bit.platformer.input.Command::execute);
        assertEquals(List.of("move", "fire"), actions);
        pressed.clear();
        keyboard.commands().forEach(ru.mipt.bit.platformer.input.Command::execute);
        assertEquals(2, actions.size());
    }

    @Test void bindingCanBeReplaced() {
        List<String> actions = new ArrayList<>();
        KeyboardController keyboard = new KeyboardController(key -> true);
        keyboard.bind(1, () -> actions.add("old"));
        keyboard.bind(1, () -> actions.add("new"));
        keyboard.commands().forEach(ru.mipt.bit.platformer.input.Command::execute);
        assertEquals(List.of("new"), actions);
        assertThrows(NullPointerException.class, () -> keyboard.bind(2, null));
        assertThrows(NullPointerException.class, () -> new KeyboardController(null));
    }
}
