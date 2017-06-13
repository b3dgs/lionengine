/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * This is where the game loop is running.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    // As we defined our resources class loader with: AppDrawable.class
    // Any call to Medias.get(...) will load from AppDrawable class path

    // Load an image (com/b3dgs/lionengine/example/core/drawable/image.png)
    private final Image image = Drawable.loadImage(Medias.create("image.jpg"));

    // Load a sprite (com/b3dgs/lionengine/example/core/drawable/sprite.png)
    private final Sprite sprite = Drawable.loadSprite(Medias.create("sprite.png"));

    // Load an animated sprite, with 7 horizontal frames only
    private final SpriteAnimated animation = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 7, 1);
    private final SpriteAnimated animationMirror = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 7, 1);

    // Load a tile in 16*16
    private final Sprite tilesheets = Drawable.loadSprite(Medias.create("tilesheet.png"));
    private final SpriteTiled tilesheet = Drawable.loadSpriteTiled(Medias.create("tilesheet.png"), 16, 16);

    // Set animation data (frames between 4-6, at a speed of 0.125, looped)
    private final Animation anim = new Animation(null, 4, 6, 0.125, false, true);

    private double tile;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
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

        // Place images
        image.setLocation(0, 0);
        sprite.setLocation(16, 160);
        animation.setLocation(80, 150);
        animationMirror.setLocation(100, 150);
        tilesheet.setLocation(150, 150);
        tilesheets.setLocation(175, 150);

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
        final int x = 175 + (int) (tile % tilesheet.getTilesHorizontal()) * tilesheet.getTileWidth();
        final int y = 150 + (int) Math.floor(tile / tilesheet.getTilesHorizontal()) * tilesheet.getTileHeight();

        g.setColor(ColorRgba.GREEN);
        g.drawRect(x, y, tilesheet.getTileWidth(), tilesheet.getTileHeight(), false);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
