package com.b3dgs.lionengine.example.b_loaddraw;

import java.awt.Color;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This is where the game loop is running.
 */
final class Scene
        extends Sequence
{
    /** Image reference. */
    private final Image image;
    /** Sprite reference. */
    private final Sprite sprite;
    /** Animation reference. */
    private final SpriteAnimated animation;
    /** Tile reference. */
    private final SpriteTiled tilesheet;
    /** Animation to play. */
    private final Animation anim;
    /** Displayed tile number. */
    private double tile;

    /**
     * Create the scene and its vars.
     * 
     * @param loader The loader reference.
     */
    Scene(final Loader loader)
    {
        super(loader);
        // As we defined our resources directory as this: Media.get("resources", "loaddraw")
        // Any call to Media.get(...) will load from ./resources/loaddraw/

        // Load an image (./resources/loaddraw/image.png)
        image = Drawable.loadImage(Media.get("image.png"));
        // Load a sprite (./resources/loaddraw/sprite.png)
        sprite = Drawable.loadSprite(Media.get("sprite.png"));
        // Load an animated sprite, with 7 horizontal frames only
        animation = Drawable.loadSpriteAnimated(Media.get("animation.png"), 7, 1);
        // Load a tile in 16*16
        tilesheet = Drawable.loadSpriteTiled(Media.get("tilesheet.png"), 16, 16);
        // Set animation data (frames between 4-6, at a speed of 0.125, looped)
        anim = Anim.createAnimation(4, 6, 0.125, false, true);
    }

    @Override
    protected void load()
    {
        // Prepare surfaces without alpha (need to be called only one time)
        // If this function is not called, there won't have any surface to display
        sprite.load(false);
        animation.load(false);
        tilesheet.load(false);
        tile = 0.0;

        // Set animation to play
        animation.play(anim);
    }

    @Override
    protected void update(double extrp)
    {
        // Update animation
        animation.updateAnimation(extrp);

        // Change the tile number to display
        tile += 0.1;

        // Ensure value is lower than the total number of tile
        tile %= tilesheet.getTilesNumber();
    }

    @Override
    protected void render(Graphic g)
    {
        // Clean screen (as we don't have any background)
        clearScreen(g);

        // Render all resources at specified location
        image.render(g, 0, 0);
        sprite.render(g, 64, 280);
        animation.setMirror(false);
        animation.render(g, 160, 300);
        animation.setMirror(true);
        animation.render(g, 200, 300);
        tilesheet.render(g, (int) tile, 300, 300);
        tilesheet.render(g, 350, 300);

        // Box the current tile
        final int x = 350 + (int) (tile % tilesheet.getTilesHorizontal()) * tilesheet.getTileWidth();
        final int y = 300 + (int) Math.floor(tile / tilesheet.getTilesHorizontal()) * tilesheet.getTileHeight();

        g.setColor(Color.GREEN);
        g.drawRect(x, y, tilesheet.getTileWidth(), tilesheet.getTileHeight(), false);
    }
}
