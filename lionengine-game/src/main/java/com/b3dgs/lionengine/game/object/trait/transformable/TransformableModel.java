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
package com.b3dgs.lionengine.game.object.trait.transformable;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Mover;
import com.b3dgs.lionengine.game.MoverModel;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.TraitModel;

/**
 * Transformable model implementation.
 * <p>
 * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
 * with {@link SizeConfig}.
 * </p>
 */
public class TransformableModel extends TraitModel implements Transformable
{
    /** Mover model. */
    private final Mover mover;
    /** Body width. */
    private int width;
    /** Body height. */
    private int height;
    /** Body old width. */
    private int oldWidth;
    /** Body old height. */
    private int oldHeight;

    /**
     * Create a transformable model.
     */
    public TransformableModel()
    {
        mover = new MoverModel();
    }

    /*
     * Transformable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        final SizeConfig sizeData = SizeConfig.create(owner.getConfigurer());
        width = sizeData.getWidth();
        height = sizeData.getHeight();
        oldWidth = width;
        oldHeight = height;
    }

    @Override
    public void moveLocation(double extrp, Direction direction, Direction... directions)
    {
        mover.moveLocation(extrp, direction, directions);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        mover.moveLocation(extrp, vx, vy);
    }

    @Override
    public void teleport(double x, double y)
    {
        mover.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        mover.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        mover.teleportY(y);
    }

    @Override
    public void setLocation(double x, double y)
    {
        mover.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        mover.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        mover.setLocationY(y);
    }

    @Override
    public void setSize(int width, int height)
    {
        oldWidth = this.width;
        oldHeight = this.height;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getX()
    {
        return mover.getX();
    }

    @Override
    public double getY()
    {
        return mover.getY();
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

    @Override
    public double getOldX()
    {
        return mover.getOldX();
    }

    @Override
    public double getOldY()
    {
        return mover.getOldY();
    }

    @Override
    public int getOldWidth()
    {
        return oldWidth;
    }

    @Override
    public int getOldHeight()
    {
        return oldHeight;
    }
}
