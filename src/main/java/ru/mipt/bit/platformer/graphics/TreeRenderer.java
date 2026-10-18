package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Tree;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class TreeRenderer implements AutoCloseable {
    private final Texture texture = new Texture("images/greenTree.png");
    private final TextureRegion image = new TextureRegion(texture);
    private final Rectangle bounds = createBoundingRectangle(image);
    private final GridPoint2 position = new GridPoint2();
    private final TiledMapTileLayer layer;

    public TreeRenderer(TiledMapTileLayer layer) { this.layer = layer; }

    public void draw(Batch batch, Tree tree) {
        position.set(tree.position().x(), tree.position().y());
        moveRectangleAtTileCenter(layer, bounds, position);
        drawTextureRegionUnscaled(batch, image, bounds, 0);
    }

    @Override
    public void close() { texture.dispose(); }
}
