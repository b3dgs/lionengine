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
package com.b3dgs.lionengine.example.game.platform.background;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Game loop designed to handle our world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera. */
    private final CameraGame camera;
    /** Background. */
    private final BackgroundPlatform background;
    /** Foreground. */
    private final Foreground foreground;
    /** Camera y. */
    private double y;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        camera = new CameraGame();
        background = new Swamp(getConfig().getSource(), 1.0, 1.0);
        foreground = new Foreground(getConfig().getSource(), 1.0, 1.0);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        camera.setView(0, 0, getWidth(), getHeight());
        y = 230;
    }

    @Override
    protected void update(double extrp)
    {
        y = UtilityMath.wrapDouble(y + 1, 0.0, 360.0);
        camera.moveLocation(extrp, 1.0, 0.0);
        camera.teleportY(UtilityMath.sin(y) * 100 + 100);
        background.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
        foreground.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());

        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        background.render(g);
        foreground.render(g);
    }
}
