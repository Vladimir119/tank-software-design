package ru.mipt.bit.platformer.model;

/** Domain output port: receives object lifecycle events. */
public interface WorldListener {
    void added(GameObject object);
    void removed(GameObject object);
}
