package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class TankRenderer implements AutoCloseable {
    private final Texture texture;
    private final TextureRegion image;
    private final Rectangle bounds;
    private final GridPoint2 from = new GridPoint2();
    private final GridPoint2 to = new GridPoint2();
    private final TileMovement movement;

    public TankRenderer(TiledMapTileLayer layer, String texturePath) {
        texture = new Texture(texturePath);
        image = new TextureRegion(texture);
        bounds = createBoundingRectangle(image);
        movement = new TileMovement(layer, Interpolation.smooth);
    }

    public void draw(Batch batch, Tank tank) {
        from.set(tank.position().x(), tank.position().y());
        to.set(tank.destination().x(), tank.destination().y());
        movement.moveRectangleBetweenTileCenters(bounds, from, to, tank.progress());
        drawTextureRegionUnscaled(batch, image, bounds, tank.direction().rotation());
    }

    @Override
    public void close() { texture.dispose(); }
}
