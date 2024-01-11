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
package com.b3dgs.lionengine.example.prototype.dungeon;

import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.helper.EntityModelHelper;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements RoutineUpdate, Recyclable
{
    private final Force movement = new Force();
    private final Alterable health = new Alterable(1);

    private final Transformable transformable;

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param transformable The transformable feature.
     */
    public EntityModel(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;

        movement.setVelocity(1.0);
        movement.setSensibility(1.0);
    }

    /**
     * Get the movement.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the health.
     * 
     * @return The health reference.
     */
    public Alterable getHealth()
    {
        return health;
    }

    @Override
    public void update(double extrp)
    {
        movement.update(extrp);

        transformable.moveLocation(extrp, movement);
    }

    @Override
    public void recycle()
    {
        transformable.teleport(0.0, 0.0);
        movement.zero();
        health.fill();
    }
}
