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
package com.b3dgs.lionengine.editor.world;

import com.b3dgs.lionengine.editor.PaletteType;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ComponentRenderer;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;

/**
 * Contains the objects of the world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum WorldViewModel
{
    /** Instance. */
    INSTANCE;

    /** Camera reference. */
    private final Camera camera;
    /** Factory reference. */
    private final Factory factory;
    /** Map reference. */
    private MapTile map;
    /** Selected palette. */
    private Enum<?> palette = PaletteType.POINTER;

    /**
     * Private constructor.
     */
    private WorldViewModel()
    {
        factory = new Factory();
        camera = new Camera();
        map = new MapTileGame(16, 16, camera);

        factory.addService(camera);
        factory.addService(map);

        final Handler handlerObject = new Handler();
        handlerObject.addRenderable(new ComponentRenderer());
        handlerObject.addUpdatable(new ComponentUpdater());
        factory.addService(handlerObject);

        final Selection selection = new Selection();
        factory.addService(selection);

        final ObjectControl objectControl = new ObjectControl(factory);
        factory.addService(objectControl);
    }

    /**
     * Set the map reference.
     * 
     * @param map The map reference.
     */
    public void setMap(MapTile map)
    {
        this.map = map;
    }

    /**
     * Set the selected palette.
     * 
     * @param palette The selected palette.
     */
    public void setSelectedPalette(Enum<?> palette)
    {
        this.palette = palette;
    }

    /**
     * Get the camera reference.
     * 
     * @return The camera reference.
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Get the map reference.
     * 
     * @return The map reference.
     */
    public MapTile getMap()
    {
        return map;
    }

    /**
     * Get the factory object reference.
     * 
     * @return The factory object reference.
     */
    public Factory getFactory()
    {
        return factory;
    }

    /**
     * Get the selected palette.
     * 
     * @return The selected palette.
     */
    public Enum<?> getSelectedPalette()
    {
        return palette;
    }
}
