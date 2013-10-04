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
package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.model.LocalizableModel;

/**
 * Represents an effect. Designed to be used for effect representation, such as explosion, particles...
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EffectGame
        extends ObjectGame
        implements Localizable
{
    /** Localizable. */
    private final Localizable location;

    /**
     * Constructor.
     * 
     * @param configurable The configuration reference.
     */
    public EffectGame(Configurable configurable)
    {
        super(configurable);
        location = new LocalizableModel();
    }

    /**
     * Render the effect.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public abstract void render(Graphic g, CameraGame camera);

    /*
     * Localizable
     */

    @Override
    public void teleport(double x, double y)
    {
        location.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        location.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        location.teleportY(y);
    }

    @Override
    public void moveLocation(double extrp, Force force, Force... forces)
    {
        location.moveLocation(extrp, force, forces);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        location.moveLocation(extrp, vx, vy);
    }

    @Override
    public void setLocation(double x, double y)
    {
        location.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        location.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        location.setLocationY(y);
    }

    @Override
    public void setLocationOffset(double x, double y)
    {
        location.setLocationOffset(x, y);
    }

    @Override
    public void setSize(int width, int height)
    {
        location.setSize(width, height);
    }

    @Override
    public double getLocationX()
    {
        return location.getLocationX();
    }

    @Override
    public double getLocationY()
    {
        return location.getLocationY();
    }

    @Override
    public int getLocationIntX()
    {
        return location.getLocationIntX();
    }

    @Override
    public int getLocationIntY()
    {
        return location.getLocationIntY();
    }

    @Override
    public double getLocationOldX()
    {
        return location.getLocationOldX();
    }

    @Override
    public double getLocationOldY()
    {
        return location.getLocationOldY();
    }

    @Override
    public int getLocationOffsetX()
    {
        return location.getLocationOffsetX();
    }

    @Override
    public int getLocationOffsetY()
    {
        return location.getLocationOffsetY();
    }

    @Override
    public int getWidth()
    {
        return location.getWidth();
    }

    @Override
    public int getHeight()
    {
        return location.getHeight();
    }
}
