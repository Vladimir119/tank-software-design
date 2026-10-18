package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.List;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class GameRenderer implements AutoCloseable {
    private final SpriteBatch batch = new SpriteBatch();
    private final TiledMap map = new TmxMapLoader().load("level.tmx");
    private final TiledMapTileLayer layer = getSingleLayer(map);
    private final OrthogonalTiledMapRenderer ground =
            (OrthogonalTiledMapRenderer) createSingleLayerMapRenderer(map, batch);
    private final TankRenderer tankRenderer = new TankRenderer(layer);
    private final TreeRenderer treeRenderer = new TreeRenderer(layer);

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
