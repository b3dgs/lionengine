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
package com.b3dgs.lionengine.example.game.cursor;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTransitionExtractor;
import com.b3dgs.lionengine.game.feature.tile.map.transition.Transition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.Circuit;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapCircuitExtractor;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private final TextGame text = new TextGame(Constant.FONT_SERIF, 9, TextStyle.NORMAL);
    private final Cursor cursor = services.create(Cursor.class);
    private final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    private final Mouse mouse = getInputDevice(Mouse.class);
    private final MapTransitionExtractor transitionExtractor = new MapTransitionExtractor(map);
    private final MapCircuitExtractor circuitExtractor = new MapCircuitExtractor(map);

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        map.create(Medias.create("level.png"));

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setSync(mouse);
        cursor.setViewer(camera);

        camera.setView(source, 0, 0, Origin.TOP_LEFT);
        camera.setLimits(map);
    }

    /**
     * Draw info about the specified tile.
     * 
     * @param g The graphics output.
     */
    private void renderTileInfo(Graphic g)
    {
        final int tx = map.getInTileX(cursor);
        final int ty = map.getInTileY(cursor);
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = tx * map.getTileWidth();
            final int y = ty * map.getTileHeight();

            text.drawRect(g, ColorRgba.GREEN, x, y, map.getTileWidth(), map.getTileHeight());
            text.setColor(ColorRgba.YELLOW);
            text.draw(g, x - 20, y + 35, Align.LEFT, "Tile number: " + tile.getNumber());
            text.draw(g, x - 20, y + 25, Align.LEFT, "X = " + tx + " | Y = " + ty);
            text.draw(g, x + 20, y + 15, "RX = " + (int) cursor.getX() + " | RY = " + (int) cursor.getY());
            text.draw(g, x + 20, y + 5, "Group: " + mapGroup.getGroup(tile));

            final Transition transition = transitionExtractor.getTransition(tile);
            text.draw(g, x - 20, y - 5, Align.LEFT, "Transition: " + transition);

            final Circuit circuit = circuitExtractor.getCircuit(tile);
            text.draw(g, x - 20, y - 15, Align.LEFT, "Circuit: " + circuit);
        }
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);

        if (keyboard.isPushedOnce(KeyboardAwt.UP))
        {
            camera.moveLocation(extrp, 0, 64);
        }
        if (keyboard.isPushedOnce(KeyboardAwt.DOWN))
        {
            camera.moveLocation(extrp, 0, -64);
        }
        if (keyboard.isPushedOnce(KeyboardAwt.LEFT))
        {
            camera.moveLocation(extrp, -64, 0);
        }
        if (keyboard.isPushedOnce(KeyboardAwt.RIGHT))
        {
            camera.moveLocation(extrp, 64, 0);
        }
        cursor.update(extrp);
        text.update(camera);

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        renderTileInfo(g);
        cursor.render(g);
    }
}
