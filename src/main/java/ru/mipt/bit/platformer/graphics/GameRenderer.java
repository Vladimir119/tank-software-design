package ru.mipt.bit.platformer.graphics;

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

    public GameRenderer(String mapPath, String tankPath, String treePath, int width, int height) {
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

    public void draw(Field field, Tank tank) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        viewport.apply();
        ground.setView((OrthographicCamera) viewport.getCamera());
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ground.render();
        batch.begin();
        tankRenderer.draw(batch, tank);
        for (Tree tree : field.trees()) treeRenderer.draw(batch, tree);
        batch.end();
    }

    public int width() { return layer.getWidth(); }
    public int height() { return layer.getHeight(); }

    @Override
    public void close() {
        treeRenderer.close();
        tankRenderer.close();
        ground.dispose();
        map.dispose();
        batch.dispose();
    }
}
