/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.rts.controlpanel;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Text reference. */
    private final TextGame text;
    /** Map reference. */
    private final Map map;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Camera reference. */
    private final CameraRts camera;
    /** Cursor reference. */
    private final CursorRts cursor;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        map = new Map();
        controlPanel = new ControlPanel();
        factoryEntity = new FactoryEntity(map);
        camera = new CameraRts(map);
        cursor = new CursorRts(mouse, camera, source, map, Media.get("cursor.png"));
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, text);
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Media.get("level.png"), map, Media.get("tiles"));
        map.loadCollisions(Media.get("tiles", "collisions.xml"));

        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 1, 15);
        camera.setKeys(Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(ColorRgba.GREEN);

        handlerEntity.createLayers(map);

        Entity peon = factoryEntity.createEntity(EntityType.PEON);
        peon.setLocation(8, 22);
        peon.setOrientation(Orientation.EAST);
        handlerEntity.add(peon);

        peon = factoryEntity.createEntity(EntityType.PEON);
        peon.setLocation(13, 18);
        peon.setOrientation(Orientation.NORTH_WEST);
        handlerEntity.add(peon);
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
        controlPanel.update(extrp, camera, cursor, keyboard);
        handlerEntity.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        controlPanel.renderCursorSelection(g, camera);
        cursor.render(g);
    }
}
