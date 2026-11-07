package ru.mipt.bit.platformer.model;

public interface WorldListener {
    void added(GameObject object);
    void removed(GameObject object);
}
