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
package com.b3dgs.lionengine.editor.world.updater;

import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.swt.graphic.KeyboardSwt;

/**
 * Handle the world navigation by using keyboard arrows key as input or mouse drag.
 */
public class WorldNavigation implements WorldMouseMoveListener, WorldKeyboardListener
{
    /** Grid movement sensibility. */
    public static final int GRID_MOVEMENT_SENSIBILITY = 8;

    /**
     * Get the speed.
     * 
     * @param axisPositive The positive axis key.
     * @param axisNegative The negative axis key.
     * @param key The key value.
     * @return The speed (-1 if negative, 1 if positive, 0 if none).
     */
    private static int getSpeed(Integer axisPositive, Integer axisNegative, Integer key)
    {
        final int speed;
        if (axisPositive.equals(key))
        {
            speed = -1;
        }
        else if (axisNegative.equals(key))
        {
            speed = 1;
        }
        else
        {
            speed = 0;
        }
        return speed;
    }

    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** World zoom. */
    private final WorldZoomUpdater zoom;
    /** Palette model. */
    private final PaletteModel palette;

    /**
     * Create the world navigation.
     * 
     * @param services The services reference.
     */
    public WorldNavigation(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        zoom = services.get(WorldZoomUpdater.class);
        palette = services.get(PaletteModel.class);
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        if (click > 0 && palette.isPalette(PaletteType.HAND))
        {
            camera.moveLocation(1.0, oldMx - (double) mx, my - (double) oldMy);
        }
    }

    /*
     * WorldKeyboardListener
     */

    @Override
    public void onKeyPushed(Integer key)
    {
        final double vx = getSpeed(KeyboardSwt.LEFT, KeyboardSwt.RIGHT, key);
        final double vy = getSpeed(KeyboardSwt.DOWN, KeyboardSwt.UP, key);
        final double scale = zoom.getScale();
        final int speedX = map.getTileWidth() * GRID_MOVEMENT_SENSIBILITY;
        final int speedY = map.getTileHeight() * GRID_MOVEMENT_SENSIBILITY;
        camera.moveLocation(1.0, vx * speedX / scale, vy * speedY / scale);
    }
}
