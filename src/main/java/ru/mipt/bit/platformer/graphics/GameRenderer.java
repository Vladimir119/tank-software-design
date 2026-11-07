package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ru.mipt.bit.platformer.model.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class GameRenderer implements GameView {
    private final SpriteBatch batch = new SpriteBatch();
    private final TiledMap map;
    private final TiledMapTileLayer layer;
    private final OrthogonalTiledMapRenderer ground;
    private final TankRenderer tankRenderer;
    private final TreeRenderer treeRenderer;
    private final FitViewport viewport;
    private final SceneObjects scene;
    private final Tank player;
    private final Texture bulletTexture;

    public GameRenderer(String mapPath, String tankPath, String treePath, int width, int height, SceneObjects scene, Tank player) {
        this.scene = scene;
        this.player = player;
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(1, 1, 1, 1);
        pixel.fill();
        bulletTexture = new Texture(pixel);
        pixel.dispose();
        map = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer original = getSingleLayer(map);
        layer = new TiledMapTileLayer(width, height, original.getTileWidth(), original.getTileHeight());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                layer.setCell(x, y, original.getCell(x % original.getWidth(), y % original.getHeight()));
            }
        }
        map.getLayers().remove(original);
        map.getLayers().add(layer);
        ground = (OrthogonalTiledMapRenderer) createSingleLayerMapRenderer(map, batch);
        tankRenderer = new TankRenderer(layer, tankPath);
        treeRenderer = new TreeRenderer(layer, treePath);
        viewport = new FitViewport(width * layer.getTileWidth(), height * layer.getTileHeight());
    }

    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewport.apply();
        ground.setView((OrthographicCamera) viewport.getCamera());
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ground.render();
        batch.begin();
        for (GameObject object : scene.objects()) {
            if (object instanceof Tank tank) {
                if (tank == player) batch.setColor(1, 1, 1, 1);
                else batch.setColor(1, 0.55f, 0.55f, 1);
                tankRenderer.draw(batch, tank);
            } else if (object instanceof Tree tree) {
                batch.setColor(1, 1, 1, 1);
                treeRenderer.draw(batch, tree);
            } else if (object instanceof Bullet bullet) {
                batch.setColor(1, 0.85f, 0.1f, 1);
                float x = (bullet.position().x() + 0.5f + bullet.direction().dx() * bullet.progress()) * tileWidth();
                float y = (bullet.position().y() + 0.5f + bullet.direction().dy() * bullet.progress()) * tileHeight();
                batch.draw(bulletTexture, x - 7, y - 7, 14, 14);
            }
        }
        batch.setColor(1, 1, 1, 1);
        batch.end();
    }

    public Camera camera() { return viewport.getCamera(); }
    public int tileWidth() { return layer.getTileWidth(); }
    public int tileHeight() { return layer.getTileHeight(); }
    public int width() { return layer.getWidth(); }
    public int height() { return layer.getHeight(); }

    @Override
    public void close() {
        bulletTexture.dispose();
        treeRenderer.close();
        tankRenderer.close();
        ground.dispose();
        map.dispose();
        batch.dispose();
    }
}
