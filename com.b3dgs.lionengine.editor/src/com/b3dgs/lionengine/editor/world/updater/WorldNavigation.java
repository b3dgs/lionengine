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
package com.b3dgs.lionengine.editor.world.updater;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.swt.Keyboard;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.WorldKeyboardListener;
import com.b3dgs.lionengine.editor.world.WorldMouseClickListener;
import com.b3dgs.lionengine.editor.world.WorldMouseMoveListener;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Handle the world navigation by using keyboard arrows key as input or mouse drag.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldNavigation implements WorldMouseClickListener, WorldMouseMoveListener, WorldKeyboardListener
{
    /** Grid movement sensibility. */
    private static final int GRID_MOVEMENT_SENSIBILITY = 8;

    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** World zoom. */
    private final WorldZoom zoom;

    /**
     * Create the world navigation.
     * 
     * @param services The services reference.
     */
    public WorldNavigation(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        zoom = services.get(WorldZoom.class);
    }

    /**
     * Update the keyboard.
     * 
     * @param vx The keyboard horizontal movement.
     * @param vy The keyboard vertical movement.
     * @param step The movement sensibility.
     */
    private void updateCamera(double vx, double vy, int step)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        if (step > 0)
        {
            camera.moveLocation(1.0, UtilMath.getRounded(vx * tw * step, tw), UtilMath.getRounded(vy * th * step, th));
        }
        else
        {
            camera.moveLocation(1.0, vx, vy);
        }
    }

    /*
     * WorldMouseClickListener
     */

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        // Nothing to do
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.HAND)
        {
            final int tw = map.getTileWidth();
            final int th = map.getTileHeight();
            final int x = UtilMath.getRounded(camera.getX() + tw / 2.0, tw);
            final int y = UtilMath.getRounded(camera.getY() - th / 2.0, th);
            camera.teleport(x, y);
        }
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.HAND && click > 0)
        {
            updateCamera(oldMx - mx, my - oldMy, 0);
        }
    }

    /*
     * WorldKeyboardListener
     */

    @Override
    public void onKeyPushed(Integer key)
    {
        final int vx;
        if (Keyboard.LEFT.equals(key))
        {
            vx = -1;
        }
        else if (Keyboard.RIGHT.equals(key))
        {
            vx = 1;
        }
        else
        {
            vx = 0;
        }

        final int vy;
        if (Keyboard.DOWN.equals(key))
        {
            vy = -1;
        }
        else if (Keyboard.UP.equals(key))
        {
            vy = 1;
        }
        else
        {
            vy = 0;
        }

        final double scale = zoom.getScale();
        updateCamera(vx / scale, vy / scale, GRID_MOVEMENT_SENSIBILITY);
    }
}
