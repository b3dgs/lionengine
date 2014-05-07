/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.cursor;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Text reference. */
    private final TextGame text;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraStrategy camera;
    /** Cursor reference. */
    private final CursorStrategy cursor;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        text = new TextGame(Text.SANS_SERIF, 10, TextStyle.NORMAL);
        map = new Map();
        camera = new CameraStrategy(map);
        cursor = new CursorStrategy(mouse, camera, getConfig().getSource(), map, Core.MEDIA.create("cursor.png"));
        mouse.setConfig(getConfig());
        setSystemCursorVisible(false);
    }

    /**
     * Draw info about the specified tile.
     * 
     * @param g The graphics output.
     * @param tx The tile location x.
     * @param ty The tile location y.
     */
    private void renderTileInfo(Graphic g, int tx, int ty)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = tx * map.getTileWidth();
            final int y = ty * map.getTileHeight();

            text.drawRect(g, ColorRgba.GREEN, x, y, map.getTileWidth(), map.getTileHeight());
            text.setColor(ColorRgba.YELLOW);
            text.draw(g, x + 18, y + 25, "Group: " + tile.getCollision().getGroup());
            text.draw(g, x + 18, y + 16, "Coll: " + tile.getCollision());
            text.draw(g, x + 18, y + 7, "Tile number: " + tile.getNumber());
            text.draw(g, x + 21, y - 2, "tx = " + tx + " | ty = " + ty);
        }
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Core.MEDIA.create("level.png"), Core.MEDIA.create("tiles"), map);
        map.loadCollisions(Core.MEDIA.create("tiles", "collisions.xml"));

        keyboard.setHorizontalControlNegative(Keyboard.LEFT);
        keyboard.setHorizontalControlPositive(Keyboard.RIGHT);
        keyboard.setVerticalControlNegative(Keyboard.DOWN);
        keyboard.setVerticalControlPositive(Keyboard.UP);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setSensibility(30, 30);
        camera.setBorders(map);
    }

    @Override
    protected void update(double extrp)
    {
        mouse.update();
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        map.render(g, camera);
        renderTileInfo(g, cursor.getLocationInTileX(), cursor.getLocationInTileY());
        cursor.render(g);
    }
}
