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
package com.b3dgs.lionengine.game.trait;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.handler.ObjectGame;

/**
 * Default body supporting gravity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class BodyModel
        extends TraitModel
        implements Body
{
    /** Body location. */
    private final Transformable transformable;
    /** Body force. */
    private final Force force;
    /** Maximum gravity value. */
    private final Force gravityMax;
    /** Vector used. */
    private Direction[] vectors;
    /** Body mass. */
    private double mass;
    /** Desired fps. */
    private int desiredFps;

    /**
     * Create a body model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @throws LionEngineException If missing {@link Trait}.
     */
    public BodyModel(ObjectGame owner) throws LionEngineException
    {
        super(owner);
        transformable = owner.getTrait(Transformable.class);
        force = new Force();
        gravityMax = new Force();
        vectors = new Direction[0];
    }

    /*
     * Body
     */

    @Override
    public void update(double extrp)
    {
        final double factor = desiredFps > 0 ? desiredFps * extrp : 1.0;
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
        return mass * Body.GRAVITY_EARTH;
    }
}
