/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

/**
 * Mover model implementation.
 */
public class MoverModel implements Mover
{
    /** Current x. */
    private double x;
    /** Current y. */
    private double y;
    /** Old x. */
    private double oldX;
    /** Old y. */
    private double oldY;

    /**
     * Create a mover model.
     */
    public MoverModel()
    {
        super();
    }

    /*
     * Mover
     */

    @Override
    public void moveLocation(double extrp, Direction direction, Direction... directions)
    {
        double vx = direction.getDirectionHorizontal();
        double vy = direction.getDirectionVertical();

        for (final Direction current : directions)
        {
            vx += current.getDirectionHorizontal();
            vy += current.getDirectionVertical();
        }
        setLocation(x + vx * extrp, y + vy * extrp);
    }

    @Override
    public void moveLocationX(double extrp, double vx)
    {
        setLocationX(x + vx * extrp);
    }

    @Override
    public void moveLocationY(double extrp, double vy)
    {
        setLocationY(y + vy * extrp);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        setLocation(x + vx * extrp, y + vy * extrp);
    }

    @Override
    public void teleport(double x, double y)
    {
        teleportX(x);
        teleportY(y);
    }

    @Override
    public void teleportX(double x)
    {
        oldX = x;
        this.x = x;
    }

    @Override
    public void teleportY(double y)
    {
        oldY = y;
        this.y = y;
    }

    @Override
    public void setLocation(double x, double y)
    {
        setLocationX(x);
        setLocationY(y);
    }

    @Override
    public void setLocationX(double x)
    {
        oldX = this.x;
        this.x = x;
    }

    @Override
    public void setLocationY(double y)
    {
        oldY = this.y;
        this.y = y;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public double getOldX()
    {
        return oldX;
    }

    @Override
    public double getOldY()
    {
        return oldY;
    }
}
