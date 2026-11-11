package ru.mipt.bit.platformer.graphics;

import ru.mipt.bit.platformer.model.*;
import java.util.*;

/** Infrastructure output adapter: maintains the graphical scene from world events. */
public final class SceneObjects implements WorldListener {
    private final List<GameObject> objects = new ArrayList<>();

    @Override
    public void added(GameObject object) {
        if (!objects.contains(object)) objects.add(object);
    }

    @Override
    public void removed(GameObject object) { objects.remove(object); }

    public List<GameObject> objects() { return List.copyOf(objects); }
}
