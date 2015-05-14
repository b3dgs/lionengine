/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.core.drawable;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.EventAction;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * This is where the game loop is running.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(640, 480, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    /** Image reference. */
    private final Image image;
    /** Sprite reference. */
    private final Sprite sprite;
    /** Animation reference. */
    private final SpriteAnimated animation;
    /** Animation mirror reference. */
    private final SpriteAnimated animationMirror;
    /** Tile reference. */
    private final Sprite tilesheets;
    /** Tile reference. */
    private final SpriteTiled tilesheet;
    /** Animation to play. */
    private final Animation anim;
    /** Displayed tile number. */
    private double tile;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, NATIVE);
        // As we defined our resources directory as this: Medias.get("resources", "drawable")
        // Any call to Medias.get(...) will load from ./resources/drawable/

        // Load an image (./resources/drawable/image.png)
        image = Drawable.loadImage(Medias.create("image.png"));

        // Load a sprite (./resources/drawable/sprite.png)
        sprite = Drawable.loadSprite(Medias.create("sprite.png"));

        // Load an animated sprite, with 7 horizontal frames only
        animation = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 7, 1);
        animationMirror = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 7, 1);

        // Load a tile in 16*16
        tilesheets = Drawable.loadSprite(Medias.create("tilesheet.png"));
        tilesheet = Drawable.loadSpriteTiled(Medias.create("tilesheet.png"), 16, 16);

        // Set animation data (frames between 4-6, at a speed of 0.125, looped)
        anim = Anim.createAnimation(null, 4, 6, 0.125, false, true);

        // Exit
        keyboard.addActionPressed(Keyboard.ESCAPE, new EventAction()
        {
            @Override
            public void action()
            {
                end();
            }
        });
    }

    @Override
    protected void load()
    {
        // Prepare surfaces without alpha (need to be called only one time)
        // If this function is not called, there won't have any surface to display
        image.load(false);
        sprite.load(false);
        animation.load(false);
        animationMirror.load(false);
        tilesheets.load(false);
        tilesheet.load(false);

        tile = 0.0;

        // Place images
        image.setLocation(0, 0);
        sprite.setLocation(64, 280);
        animation.setLocation(160, 300);
        animationMirror.setLocation(200, 300);
        tilesheet.setLocation(300, 300);
        tilesheets.setLocation(350, 300);

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
        tile += 0.1 * extrp;

        // Ensure value is lower than the total number of tile
        tile %= tilesheet.getTilesHorizontal() * tilesheet.getTilesVertical();
        tilesheet.setTile((int) tile);

        // Rotate sprite
        sprite.rotate((int) tile * (360 / (tilesheet.getTilesHorizontal() * tilesheet.getTilesVertical())));
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

        // Box the current tile
        final int x = 350 + (int) (tile % tilesheet.getTilesHorizontal()) * tilesheet.getTileWidth();
        final int y = 300 + (int) Math.floor(tile / tilesheet.getTilesHorizontal()) * tilesheet.getTileHeight();

        g.setColor(ColorRgba.GREEN);
        g.drawRect(x, y, tilesheet.getTileWidth(), tilesheet.getTileHeight(), false);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
