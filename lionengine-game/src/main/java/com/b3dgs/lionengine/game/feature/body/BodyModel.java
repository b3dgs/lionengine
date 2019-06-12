/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.body;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Default body supporting gravity implementation.
 */
public class BodyModel extends FeatureModel implements Body
{
    /** Default FPS. */
    private static final int DEFAULT_FPS = 60;
    /** Body force. */
    private final Force force = new Force();
    /** Maximum gravity value. */
    private final Force gravityMax = new Force();
    /** Gravity used. */
    private double gravity = Constant.GRAVITY_EARTH;
    /** Body mass. */
    private double mass;
    /** Desired FPS. */
    private int desiredFps = DEFAULT_FPS;

    /**
     * Create a body model.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     */
    public BodyModel()
    {
        super();
    }

    /*
     * Body
     */

    @Override
    public void update(double extrp)
    {
        force.addDirection(extrp, 0.0, -gravity * (desiredFps / (double) Constant.ONE_SECOND_IN_MILLI) * extrp);
    }

    @Override
    public void resetGravity()
    {
        force.setDirection(DirectionNone.INSTANCE);
    }

    @Override
    public void setDesiredFps(int desiredFps)
    {
        this.desiredFps = desiredFps;
    }

    @Override
    public void setGravity(double gravity)
    {
        this.gravity = gravity;
    }

    @Override
    public void setGravityMax(double max)
    {
        gravityMax.setDirection(0.0, -max);
        force.setDirectionMaximum(DirectionNone.INSTANCE);
        force.setDirectionMinimum(gravityMax);
    }

    @Override
    public void setMass(double mass)
    {
        this.mass = mass;
    }

    @Override
    public double getMass()
    {
        return mass;
    }

    @Override
    public double getWeight()
    {
        return mass * gravity;
    }

    @Override
    public double getDirectionHorizontal()
    {
        return force.getDirectionHorizontal();
    }

    @Override
    public double getDirectionVertical()
    {
        return force.getDirectionVertical();
    }
}
