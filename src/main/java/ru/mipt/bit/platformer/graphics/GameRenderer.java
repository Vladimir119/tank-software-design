package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
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

    public GameRenderer(String mapPath, String tankPath, String treePath) {
        map = new TmxMapLoader().load(mapPath);
        layer = getSingleLayer(map);
        ground = (OrthogonalTiledMapRenderer) createSingleLayerMapRenderer(map, batch);
        tankRenderer = new TankRenderer(layer, tankPath);
        treeRenderer = new TreeRenderer(layer, treePath);
    }

    public void draw(Field field, Tank tank) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
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
