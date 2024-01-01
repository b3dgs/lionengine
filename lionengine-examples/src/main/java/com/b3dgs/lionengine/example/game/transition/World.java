/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.transition;

import java.util.Arrays;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.CircuitsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuit;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.helper.WorldHelper;

/**
 * World representation.
 */
public final class World extends WorldHelper
{
    private static final int TILE_GROUND = 0;
    private static final int TILE_TREE = 70;
    private static final int TILE_WATER = 61;
    private static final int TILE_ROAD = 30;

    private final TextGame text = new TextGame(Constant.FONT_DIALOG, 9, TextStyle.NORMAL);
    private final Cursor cursor = services.create(Cursor.class);
    private final MapTileTransition mapTransition = map.getFeature(MapTileTransition.class);
    private final MapTileCircuit mapCurcuit = map.getFeature(MapTileCircuit.class);
    private final Keyboard keyboard = getInputDevice(Keyboard.class);
    private final Mouse mouse = getInputDevice(Mouse.class);

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    public World(Services services)
    {
        super(services);

        TransitionsConfig.exports(Medias.create("transitions.xml"),
                                  Arrays.asList(Medias.create("transitions.png")),
                                  Medias.create("sheets.xml"),
                                  Medias.create("groups.xml"));
        CircuitsConfig.exports(Medias.create("circuits.xml"),
                               Arrays.asList(Medias.create("transitions.png")),
                               Medias.create("sheets.xml"),
                               Medias.create("groups.xml"));
        map.loadSheets(Medias.create("sheets.xml"));
        map.create(16, 16, 32, 32);

        for (int tx = 0; tx < 32; tx++)
        {
            for (int ty = 0; ty < 32; ty++)
            {
                map.setTile(tx, ty, TILE_GROUND);
            }
        }

        cursor.addImage(0, Medias.create("cursor.png"));
        cursor.load();
        cursor.setGrid(map.getTileWidth(), map.getTileHeight());
        cursor.setSync(mouse);
        cursor.setViewer(camera);

        camera.setView(source, 0, 0, Origin.TOP_LEFT);
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

        if (cursor.isPushed())
        {
            final int tx = map.getInTileX(cursor);
            final int ty = map.getInTileY(cursor);
            if (UtilMath.isBetween(tx, 0, map.getInTileWidth() - 1)
                && UtilMath.isBetween(ty, 0, map.getInTileHeight() - 1))
            {
                if (cursor.getPushed().intValue() == 1)
                {
                    map.setTile(tx, ty, TILE_TREE);
                    mapTransition.resolve(map.getTile(tx, ty));
                }
                else if (cursor.getPushed().intValue() == 2)
                {
                    map.setTile(tx, ty, TILE_GROUND);
                    mapTransition.resolve(map.getTile(tx, ty));
                }
                else if (cursor.getPushed().intValue() == 3)
                {
                    map.setTile(tx, ty, TILE_WATER);
                    mapTransition.resolve(map.getTile(tx, ty));
                }
                else if (cursor.getPushed().intValue() == 4)
                {
                    map.setTile(tx, ty, TILE_ROAD);
                    mapCurcuit.resolve(map.getTile(tx, ty));
                }
            }
        }

        cursor.update(extrp);
        text.update(camera);

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        cursor.render(g);
    }
}
