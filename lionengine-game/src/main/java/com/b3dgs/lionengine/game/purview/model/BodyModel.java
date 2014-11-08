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

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.purview.Body;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default body implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class BodyModel
        implements Body
{
    /** Body location. */
    private final Localizable localizable;
    /** Body force. */
    private final Force force;
    /** Maximum gravity value. */
    private final Force gravityMax;
    /** Body mass. */
    private double mass;
    /** Invert axis. */
    private int invertY;

    /**
     * Constructor.
     * 
     * @param localizable The localizable reference.
     */
    public BodyModel(Localizable localizable)
    {
        this.localizable = localizable;
        force = new Force();
        gravityMax = new Force();
        invertY = -1;
    }

    /*
     * Body
     */

    @Override
    public void updateGravity(double extrp, int desiredFps, Force... forces)
    {
        force.addForce(0.0, getWeight() * invertY / desiredFps * extrp);
        localizable.moveLocation(extrp, force, forces);
    }

    @Override
    public void resetGravity()
    {
        force.setForce(Force.ZERO);
    }

    @Override
    public void invertAxisY(boolean state)
    {
        invertY = state ? -1 : 1;
    }

    @Override
    public void setGravityMax(double max)
    {
        gravityMax.setForce(0.0, -max);
        force.setForceMaximum(Force.ZERO);
        force.setForceMinimum(gravityMax);
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
