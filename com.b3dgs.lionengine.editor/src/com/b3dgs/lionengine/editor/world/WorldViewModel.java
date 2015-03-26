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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.palette.PaletteType;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Factory;

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
    /** Map reference. */
    private MapTile map;
    /** Factory reference. */
    private Factory factory;
    /** Selected object media. */
    private Media selectedObject;
    /** Selected palette. */
    private Enum<?> palette = PaletteType.POINTER;

    /**
     * Private constructor.
     */
    private WorldViewModel()
    {
        camera = new Camera();
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
     * Set the factory reference.
     * 
     * @param factory The factory reference.
     */
    public void setFactory(Factory factory)
    {
        this.factory = factory;
    }

    /**
     * Set the selected object media.
     * 
     * @param selectedObject The selected object media.
     */
    public void setSelectedObject(Media selectedObject)
    {
        this.selectedObject = selectedObject;
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
     * Get the selected object media.
     * 
     * @return The selected object media.
     */
    public Media getSelectedObject()
    {
        return selectedObject;
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
