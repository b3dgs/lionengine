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

import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;
import com.b3dgs.lionengine.game.purview.model.LocalizableModel;

/**
 * Represents an effect.
 */
public abstract class EffectGame
        extends ConfigurableModel
        implements Localizable
{
    /** Id used. */
    private static final Set<Integer> IDS = new HashSet<>(16);
    /** Last id used. */
    private static int lastId = 1;

    /** Localizable. */
    private final Localizable location;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     */
    private static Integer getFreeId()
    {
        while (EffectGame.IDS.contains(Integer.valueOf(EffectGame.lastId)))
        {
            EffectGame.lastId++;
        }
        return Integer.valueOf(EffectGame.lastId);
    }

    /** Effect id. */
    private final Integer id;
    /** Destroyed flag; true will remove it from the handler. */
    private boolean destroy;

    /**
     * Constructor.
     */
    public EffectGame()
    {
        this(null);
    }

    /**
     * Create a new effect from an existing configuration. The configuration will be shared; this will reduce memory
     * usage.
     * 
     * @param configurable The configuration reference.
     */
    public EffectGame(Configurable configurable)
    {
        super(configurable);
        destroy = false;
        id = EffectGame.getFreeId();
        EffectGame.IDS.add(id);
        location = new LocalizableModel();
    }

    /**
     * Update the effect.
     * 
     * @param extrp The extrapolation value.
     */
    public abstract void update(double extrp);

    /**
     * Render the effect.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public abstract void render(Graphic g, CameraGame camera);

    /**
     * Get the entity id (unique).
     * 
     * @return The entity id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Remove entity from handler, and free memory.
     */
    public void destroy()
    {
        destroy = true;
        EffectGame.IDS.remove(getId());
    }

    /**
     * Check if entity is going to be removed.
     * 
     * @return <code>true</code> if going to be removed, <code>false</code> else.
     */
    public boolean isDestroyed()
    {
        return destroy;
    }

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

    @Override
    public Line2D getMovement()
    {
        return location.getMovement();
    }
}
