package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
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

public class GameDesktopLauncher extends ApplicationAdapter {
    private SpriteBatch batch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer ground;
    private TiledMapTileLayer layer;
    private Texture tankTexture;
    private Texture treeTexture;
    private TextureRegion tankImage;
    private TextureRegion treeImage;
    private TileMovement movement;
    private Field field;
    private Tank tank;

    @Override
    public void create() {
        batch = new SpriteBatch();
        map = new TmxMapLoader().load("level.tmx");
        layer = getSingleLayer(map);
        ground = (OrthogonalTiledMapRenderer) createSingleLayerMapRenderer(map, batch);
        movement = new TileMovement(layer, Interpolation.smooth);
        tankTexture = new Texture("images/tank_blue.png");
        treeTexture = new Texture("images/greenTree.png");
        tankImage = new TextureRegion(tankTexture);
        treeImage = new TextureRegion(treeTexture);
        field = new Field(layer.getWidth(), layer.getHeight(), List.of(new Tree(new Cell(1, 3))));
        tank = new Tank(new Cell(1, 1));
    }

    @Override
    public void render() {
        handleMovement(UP, W, Direction.UP);
        handleMovement(LEFT, A, Direction.LEFT);
        handleMovement(DOWN, S, Direction.DOWN);
        handleMovement(RIGHT, D, Direction.RIGHT);
        tank.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        ground.render();
        batch.begin();
        Rectangle bounds = createBoundingRectangle(tankImage);
        movement.moveRectangleBetweenTileCenters(bounds, point(tank.position()), point(tank.destination()), tank.progress());
        drawTextureRegionUnscaled(batch, tankImage, bounds, tank.direction().rotation());
        for (Tree tree : field.trees()) {
            Rectangle treeBounds = createBoundingRectangle(treeImage);
            moveRectangleAtTileCenter(layer, treeBounds, point(tree.position()));
            drawTextureRegionUnscaled(batch, treeImage, treeBounds, 0);
        }
        batch.end();
    }

    private void handleMovement(int arrow, int letter, Direction direction) {
        if (Gdx.input.isKeyPressed(arrow) || Gdx.input.isKeyPressed(letter)) tank.move(direction, field);
    }

    private static GridPoint2 point(Cell cell) { return new GridPoint2(cell.x(), cell.y()); }

    @Override
    public void dispose() {
        treeTexture.dispose();
        tankTexture.dispose();
        ground.dispose();
        map.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
