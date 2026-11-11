package ru.mipt.bit.platformer.model;

import java.util.*;

/** Domain service: implements WorldAccess and publishes object changes. */
public final class GameWorld implements WorldAccess {
    private final Field field;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();

    public GameWorld(Field field, List<Tank> tanks) {
        this.field = Objects.requireNonNull(field);
        for (Tree tree : field.trees()) add(tree);
        for (Tank tank : tanks) add(tank);
    }

    public void subscribe(WorldListener listener) {
        Objects.requireNonNull(listener);
        if (listeners.contains(listener)) return;
        listeners.add(listener);
        for (GameObject object : List.copyOf(objects)) listener.added(object);
    }

    public void unsubscribe(WorldListener listener) { listeners.remove(listener); }

    public void add(GameObject object) {
        Objects.requireNonNull(object);
        if (objects.contains(object)) return;
        if (object instanceof Tank tank && (!tank.alive() || !canEnter(tank.position()))) {
            throw new IllegalArgumentException("Tank must start in a free cell");
        }
        objects.add(object);
        for (WorldListener listener : List.copyOf(listeners)) listener.added(object);
        if (object instanceof Bullet bullet) collide(bullet);
    }

    public void remove(GameObject object) {
        if (!objects.remove(object)) return;
        for (WorldListener listener : List.copyOf(listeners)) listener.removed(object);
    }

    public boolean contains(GameObject object) { return objects.contains(object); }
    public List<GameObject> objects() { return List.copyOf(objects); }

    @Override
    public boolean canEnter(Cell cell) {
        if (!field.contains(cell)) return false;
        return objects.stream().noneMatch(object ->
                object instanceof Tree && object.position().equals(cell)
                || object instanceof Tank tank && tank.alive()
                && (tank.position().equals(cell) || tank.destination().equals(cell)));
    }

    public boolean collide(Bullet bullet) {
        if (!contains(bullet)) return true;
        if (!field.contains(bullet.position())) {
            remove(bullet);
            return true;
        }
        for (GameObject other : List.copyOf(objects)) {
            if (other == bullet || other == bullet.owner()) continue;
            boolean hit = other.position().equals(bullet.position());
            if (other instanceof Tank tank) hit |= tank.destination().equals(bullet.position());
            if (!hit) continue;
            remove(bullet);
            if (other instanceof Tank tank) {
                tank.damage(bullet.damage());
                if (!tank.alive()) remove(tank);
            } else if (other instanceof Bullet) {
                remove(other);
            }
            return true;
        }
        return false;
    }

    public void live(float delta) {
        if (!Float.isFinite(delta) || delta < 0) throw new IllegalArgumentException("Invalid delta");
        float remaining = delta;
        do {
            float step = Math.min(remaining, 0.02f);
            for (GameObject object : List.copyOf(objects)) {
                if (!contains(object)) continue;
                if (object instanceof Tank tank && !tank.alive()) remove(tank);
                else object.live(step, this);
            }
            remaining -= step;
        } while (remaining > 0);
    }
}
