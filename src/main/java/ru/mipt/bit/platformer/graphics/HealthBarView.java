package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.model.*;

public final class HealthBarView implements GameView {
    private final GameView base;
    private final SceneObjects scene;
    private final HealthDisplay display;
    private final Camera camera;
    private final int tileWidth;
    private final int tileHeight;
    private final ShapeRenderer shapes = new ShapeRenderer();

    public HealthBarView(GameView base, SceneObjects scene, HealthDisplay display,
                         Camera camera, int tileWidth, int tileHeight) {
        this.base = base;
        this.scene = scene;
        this.display = display;
        this.camera = camera;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public void draw() {
        base.draw();
        if (!display.visible()) return;
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (GameObject object : scene.objects()) {
            if (!(object instanceof Tank tank)) continue;
            float progress = Interpolation.smooth.apply(tank.progress());
            float x = (tank.position().x() + (tank.destination().x() - tank.position().x()) * progress + 0.5f) * tileWidth;
            float y = (tank.position().y() + (tank.destination().y() - tank.position().y()) * progress + 0.5f) * tileHeight;
            float width = tileWidth * 0.6f;
            shapes.setColor(0.2f, 0.1f, 0.1f, 1);
            shapes.rect(x - width / 2, y + tileHeight * 0.32f, width, 8);
            shapes.setColor(0.2f, 0.9f, 0.2f, 1);
            shapes.rect(x - width / 2, y + tileHeight * 0.32f, width * tank.health() / tank.maxHealth(), 8);
        }
        shapes.end();
    }

    @Override
    public void close() {
        shapes.dispose();
        base.close();
    }
}
