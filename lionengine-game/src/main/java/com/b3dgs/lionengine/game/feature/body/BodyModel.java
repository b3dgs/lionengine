/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Body model implementation.
 */
public class BodyModel extends FeatureModel implements Body, Recyclable
{
    /** Body force. */
    private final Force force = new Force();
    /** Maximum gravity value. */
    private final Force gravityMax = new Force();
    /** Update priority. */
    private final int priorityUpdate;

    /** Gravity used. */
    private double gravity = Constant.GRAVITY_EARTH;
    /** Body mass. */
    private double mass;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BodyModel(Services services, Setup setup)
    {
        this(services, setup, XmlReader.EMPTY);
    }

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param config The feature configuration node (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BodyModel(Services services, Setup setup, XmlReader config)
    {
        super(services, setup);

        Check.notNull(config);

        priorityUpdate = config.getInteger(RoutineUpdate.BODY, FeaturableConfig.ATT_PRIORITY_UPDATE);
        readConfig();
    }

    /**
     * Read configuration.
     */
    private void readConfig()
    {
        final BodyConfig config = BodyConfig.imports(setup);
        gravity = config.getGravity();
        gravityMax.setDirection(0.0, -config.getGravityMax());
        force.setDirectionMaximum(DirectionNone.INSTANCE);
        force.setDirectionMinimum(gravityMax);
    }

    @Override
    public void update(double extrp)
    {
        force.addDirection(extrp, 0.0, -gravity);
    }

    @Override
    public void resetGravity()
    {
        force.setDirection(DirectionNone.INSTANCE);
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
    public double getGravity()
    {
        return gravity;
    }

    @Override
    public double getGravityMax()
    {
        return -gravityMax.getDirectionVertical();
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

    @Override
    public int getPriotityUpdate()
    {
        return priorityUpdate;
    }

    @Override
    public void recycle()
    {
        resetGravity();
        readConfig();
    }
}
