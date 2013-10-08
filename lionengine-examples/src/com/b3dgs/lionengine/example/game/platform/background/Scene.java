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
package com.b3dgs.lionengine.example.game.platform.background;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Game loop designed to handle our world.
 * 
 * @see com.b3dgs.lionengine.example.minimal
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

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
        camera = new CameraGame();
        background = new Swamp(source, 1.0, 1.0);
        foreground = new Foreground(source, 1.0, 1.0);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        camera.setView(0, 0, width, height);
    }

    @Override
    protected void update(double extrp)
    {
        y += 1.0;
        camera.moveLocation(extrp, 1.0, 0.0);
        camera.teleportY(UtilityMath.sin(y) * 100 + 100);
        background.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
        foreground.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
    }

    @Override
    protected void render(Graphic g)
    {
        background.render(g);
        foreground.render(g);
    }
}
