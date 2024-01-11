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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.helper.EntityModelHelper;

/**
 * Player model implementation.
 */
@FeatureInterface
public final class PlayerModel extends EntityModelHelper implements RoutineUpdate, Recyclable
{
    private static final double SPEED = 1.0;
    private static final double JUMP = 8.0;
    private static final double GRAVITY = 6.0;

    private final Body body;
    private final Transformable transformable;

    private final Force movement = new Force();
    private final Force jump = new Force();

    /**
     * Create model.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param transformable The transformable feature.
     * @param body The body feature.
     * @param tileCollidable The tile collidable feature.
     */
    public PlayerModel(Services services,
                       Setup setup,
                       Transformable transformable,
                       Body body,
                       TileCollidable tileCollidable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.body = body;

        jump.setVelocity(0.1);
        jump.setSensibility(0.1);

        movement.setVelocity(1.0);
        movement.setSensibility(1.0);

        body.setGravity(GRAVITY);
        body.setGravityMax(GRAVITY);

        tileCollidable.addListener((result, category) ->
        {
            if (Axis.Y == category.getAxis() && transformable.getY() < transformable.getOldY())
            {
                body.resetGravity();
                jump.setDirection(DirectionNone.INSTANCE);
                tileCollidable.apply(result);
            }
        });
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return THe jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(input.getHorizontalDirection() * SPEED, 0.0);
        if (input.getVerticalDirection() > 0)
        {
            jump.setDirection(0.0, JUMP);
        }

        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);

        if (transformable.getY() < 0)
        {
            jump.setDirection(DirectionNone.INSTANCE);
            transformable.teleportY(0.0);
            body.resetGravity();
        }

        transformable.moveLocation(extrp, body, movement, jump);
    }

    @Override
    public void recycle()
    {
        transformable.teleport(0.0, 0.0);
        movement.zero();
        jump.zero();
    }
}
