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
package com.b3dgs.lionengine.editor.utility;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Point;

/**
 * Series of tool functions around the editor world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilWorld
{
    /**
     * Get the location over the mouse.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The location found.
     */
    public static Point getPoint(MapTile map, Camera camera, int mx, int my)
    {
        final int x = (int) camera.getX() + mx;
        final int y = (int) camera.getY() - my + camera.getHeight();
        return Geom.createPoint(x, y);
    }

    /**
     * Get the tile location over the mouse.
     * 
     * @param map The map reference.
     * @param camera The camera reference.
     * @param mx The mouse X.
     * @param my The mouse Y.
     * @return The tile found, <code>null</code> if none.
     */
    public static Tile getTile(MapTile map, Camera camera, int mx, int my)
    {
        final int x = (int) camera.getX() + mx;
        final int y = (int) camera.getY() - my + camera.getHeight();
        return map.getTileAt(x, y);
    }

    /**
     * Private constructor.
     */
    private UtilWorld()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
