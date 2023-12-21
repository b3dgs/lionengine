/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.example.core.drawable;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * This is where the game loop is running.
 */
public final class Scene extends Sequence
{
    /** Native resolution. */
    static final Resolution NATIVE = new Resolution(320, 240, 60);

    // As we defined our resources class loader with: AppDrawable.class
    // Any call to Medias.get(...) will load from AppDrawable class path

    // Load an image (com/b3dgs/lionengine/example/core/drawable/image.png)
    private final Image image = Drawable.loadImage(Medias.create("image.png"));

    // Load a sprite (com/b3dgs/lionengine/example/core/drawable/sprite.png)
    private final Sprite sprite = Drawable.loadSprite(Medias.create("sprite.png"));

    // Load an animated sprite, with 7 horizontal frames only
    private final SpriteAnimated animation = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 4, 1);
    private final SpriteAnimated animationMirror = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 4, 1);

    // Load a tile in 16*16
    private final Sprite tilesheets = Drawable.loadSprite(Medias.create("tilesheet.png"));
    private final SpriteTiled tilesheet = Drawable.loadSpriteTiled(Medias.create("tilesheet.png"), 16, 16);

    // Load font 8*8
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create("font.png"), Medias.create("font.xml"), 8, 8);

    // Set animation data (frames between 4-6, at a speed of 0.125, looped)
    private final Animation anim = new Animation(Animation.DEFAULT_NAME, 1, 4, 0.125, false, true);

    private double tile;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        getInputDevice(Keyboard.class).addActionPressed(KeyboardAwt.ESCAPE, this::end);
    }

    @Override
    public void load()
    {
        // Prepare surfaces without alpha (need to be called only one time)
        // If this function is not called, there won't have any surface to display
        image.load();
        image.prepare();

        sprite.load();
        sprite.prepare();

        animation.load();
        animation.prepare();

        animationMirror.load();
        animationMirror.prepare();

        tilesheets.load();
        tilesheets.prepare();

        tilesheet.load();
        tilesheet.prepare();

        font.load();
        font.prepare();

        // Place images
        image.setLocation(getWidth() - image.getWidth() - 64, 16);
        sprite.setLocation(64, image.getHeight() / 2);
        sprite.setAngleAnchor(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        animation.setLocation(48 - animation.getTileWidth() / 2, image.getHeight() + 64);
        animationMirror.setLocation(48 + animation.getTileWidth() / 2, image.getHeight() + 64);
        tilesheet.setLocation(176, 160);
        tilesheets.setLocation(208, 160);

        sprite.setOrigin(Origin.MIDDLE);
        animationMirror.setMirror(Mirror.HORIZONTAL);

        // Set animation to play
        animation.play(anim);
        animationMirror.play(anim);
    }

    @Override
    public void update(double extrp)
    {
        // Update animation
        animation.update(extrp);
        animationMirror.update(extrp);

        // Change the tile number to display
        tile += 0.05 * extrp;

        // Ensure value is lower than the total number of tile
        tile %= tilesheet.getTilesHorizontal() * tilesheet.getTilesVertical();
        tilesheet.setTile((int) tile);

        // Rotate sprite
        sprite.rotate((int) (tile
                             * (Constant.MAX_DEGREE
                                / (tilesheet.getTilesHorizontal() * tilesheet.getTilesVertical()))));
    }

    @Override
    public void render(Graphic g)
    {
        // Clean screen (as we don't have any background)
        g.clear(0, 0, getWidth(), getHeight());

        // Render all resources at specified location
        image.render(g);
        sprite.render(g);
        animation.render(g);
        animationMirror.render(g);
        tilesheets.render(g);
        tilesheet.render(g);

        font.draw(g,
                  (int) image.getX() + image.getWidth() / 2,
                  (int) image.getY() + image.getHeight() + 2,
                  Align.CENTER,
                  "IMAGE");
        font.draw(g, (int) sprite.getX(), (int) sprite.getY() + sprite.getHeight() / 2 + 2, Align.CENTER, "SPRITE");
        font.draw(g,
                  (int) animation.getX() + animation.getTileWidth(),
                  (int) animation.getY() + animation.getHeight() + 2,
                  Align.CENTER,
                  "ANIMATION");
        font.draw(g,
                  (int) tilesheets.getX() + tilesheets.getWidth() / 2,
                  (int) tilesheets.getY() + tilesheets.getHeight() + 2,
                  Align.CENTER,
                  "TILE");

        // Box the current tile
        final int x = (int) tilesheets.getX()
                      + (int) (tile % tilesheet.getTilesHorizontal()) * tilesheet.getTileWidth();
        final int y = (int) tilesheets.getY()
                      + (int) Math.floor(tile / tilesheet.getTilesHorizontal()) * tilesheet.getTileHeight();

        g.setColor(ColorRgba.GREEN);
        g.drawRect((int) tilesheet.getX(),
                   (int) tilesheet.getY(),
                   tilesheet.getTileWidth(),
                   tilesheet.getTileHeight(),
                   false);
        g.drawRect(x, y, tilesheet.getTileWidth(), tilesheet.getTileHeight(), false);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
