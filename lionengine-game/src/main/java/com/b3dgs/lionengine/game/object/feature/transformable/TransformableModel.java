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
package com.b3dgs.lionengine.game.object.feature.transformable;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Mover;
import com.b3dgs.lionengine.game.MoverModel;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.object.Configurer;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.SizeConfig;

/**
 * Transformable model implementation.
 */
public class TransformableModel extends FeatureModel implements Transformable
{
    /** Mover model. */
    private final Mover mover = new MoverModel();
    /** Body width. */
    private int width;
    /** Body height. */
    private int height;
    /** Body old width. */
    private int oldWidth;
    /** Body old height. */
    private int oldHeight;

    /**
     * Create a transformable model without configuration.
     */
    public TransformableModel()
    {
        super();
    }

    /**
     * Create a transformable model.
     * <p>
     * The {@link Setup} can provide a valid {@link SizeConfig}.
     * </p>
     * 
     * @param setup The setup reference.
     */
    public TransformableModel(Setup setup)
    {
        super();
        final Configurer configurer = setup.getConfigurer();
        if (configurer.getRoot().hasChild(SizeConfig.NODE_SIZE))
        {
            final SizeConfig sizeData = SizeConfig.imports(configurer);
            width = sizeData.getWidth();
            height = sizeData.getHeight();
        }
        oldWidth = width;
        oldHeight = height;
    }

    /*
     * Transformable
     */

    @Override
    public void moveLocation(double extrp, Direction direction, Direction... directions)
    {
        mover.moveLocation(extrp, direction, directions);
    }

    @Override
    public void moveLocationX(double extrp, double vx)
    {
        mover.moveLocationX(extrp, vx);
    }

    @Override
    public void moveLocationY(double extrp, double vy)
    {
        mover.moveLocationY(extrp, vy);
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
