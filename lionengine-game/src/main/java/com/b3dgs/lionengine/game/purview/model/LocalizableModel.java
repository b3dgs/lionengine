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
package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Localizable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LocalizableModel
        implements Localizable
{
    /** Current coordinate. */
    private final Coord current;
    /** Old coordinate. */
    private final Coord old;
    /** Body width. */
    private int width;
    /** Body height. */
    private int height;

    /**
     * Create a localizable, set to <code>(0, 0)</code> by default.
     */
    public LocalizableModel()
    {
        this(0.0, 0.0);
    }

    /**
     * Constructor.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public LocalizableModel(double x, double y)
    {
        current = Geom.createCoord(x, y);
        old = Geom.createCoord(x, y);
    }

    /**
     * Backup the location by storing the current location in <code>old</code> field.
     */
    private void backupLocation()
    {
        old.set(current.getX(), current.getY());
    }

    /*
     * Localizable
     */

    @Override
    public void teleport(double x, double y)
    {
        current.set(x, y);
        old.set(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        current.setX(x);
        old.setX(x);
    }

    @Override
    public void teleportY(double y)
    {
        current.setY(y);
        old.setY(y);
    }

    @Override
    public void moveLocation(double extrp, Direction direction, Direction... directions)
    {
        backupLocation();
        current.translate(direction.getDirectionHorizontal() * extrp, direction.getDirectionVertical() * extrp);
        for (final Direction d : directions)
        {
            current.translate(d.getDirectionHorizontal() * extrp, d.getDirectionVertical() * extrp);
        }
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        backupLocation();
        current.translate(vx * extrp, vy * extrp);
    }

    @Override
    public void setLocation(double x, double y)
    {
        backupLocation();
        current.set(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        old.setX(current.getX());
        current.setX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        old.setY(current.getY());
        current.setY(y);
    }

    @Override
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public double getLocationX()
    {
        return current.getX();
    }

    @Override
    public double getLocationY()
    {
        return current.getY();
    }

    @Override
    public int getLocationIntX()
    {
        return (int) Math.floor(current.getX());
    }

    @Override
    public int getLocationIntY()
    {
        return (int) Math.floor(current.getY());
    }

    @Override
    public double getLocationOldX()
    {
        return old.getX();
    }

    @Override
    public double getLocationOldY()
    {
        return old.getY();
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }
}
