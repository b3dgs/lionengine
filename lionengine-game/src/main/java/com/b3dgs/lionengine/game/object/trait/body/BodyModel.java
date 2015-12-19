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
package com.b3dgs.lionengine.game.object.trait.body;

import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.TraitModel;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;

/**
 * Default body supporting gravity implementation.
 * <p>
 * The {@link ObjectGame} owner must have the following {@link com.b3dgs.lionengine.game.object.Trait}:
 * </p>
 * <ul>
 * <li>{@link Transformable}</li>
 * </ul>
 */
public class BodyModel extends TraitModel implements Body
{
    /** Body force. */
    private final Force force;
    /** Maximum gravity value. */
    private final Force gravityMax;
    /** Body location. */
    private Transformable transformable;
    /** Gravity used. */
    private double gravity;
    /** Vector used. */
    private Direction[] vectors;
    /** Body mass. */
    private double mass;
    /** Desired fps. */
    private int desiredFps;

    /**
     * Create a body model.
     */
    public BodyModel()
    {
        super();
        force = new Force();
        gravityMax = new Force();
        vectors = new Direction[0];
        gravity = Body.GRAVITY_EARTH;
    }

    /*
     * Body
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);
        transformable = owner.getTrait(Transformable.class);
    }

    @Override
    public void update(double extrp)
    {
        final double factor;
        if (desiredFps > 0)
        {
            factor = desiredFps * extrp;
        }
        else
        {
            factor = 1.0;
        }
        force.addDirection(0.0, -getWeight() / factor);
        transformable.moveLocation(extrp, force, vectors);
    }

    @Override
    public void resetGravity()
    {
        force.setDirection(Direction.ZERO);
    }

    @Override
    public void setVectors(Direction... vectors)
    {
        this.vectors = vectors;
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
        force.setDirectionMaximum(Direction.ZERO);
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
}
