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
package com.b3dgs.lionengine.tutorials.platform.c;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.helper.EntityModelHelper;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements Routine
{
    /** Horizontal speed. */
    static final double SPEED_X = 1.5;
    private static final int GROUND = 54;

    private final Camera camera = services.get(Camera.class);

    private final Transformable transformable;
    private final Mirrorable mirrorable;
    private final Body body;

    private final Force movement = new Force();
    private final Force jump = new Force();

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param transformable The transformable feature.
     * @param mirrorable The mirrorable feature.
     * @param body The body feature.
     */
    public EntityModel(Services services, Setup setup, Transformable transformable, Mirrorable mirrorable, Body body)
    {
        super(services, setup);

        this.transformable = transformable;
        this.mirrorable = mirrorable;
        this.body = body;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        movement.setVelocity(1.0);

        jump.setSensibility(0.1);
        jump.setVelocity(0.2);

        body.setGravity(0.3);
        body.setGravityMax(3);

        respawn();
    }

    @Override
    public void update(double extrp)
    {
        movement.update(extrp);
        jump.update(extrp);

        transformable.moveLocation(extrp, body, movement, jump);

        final double side = input.getHorizontalDirection();
        if (side < 0 && movement.getDirectionHorizontal() < 0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (side > 0 && movement.getDirectionHorizontal() > 0)
        {
            mirrorable.mirror(Mirror.NONE);
        }

        if (transformable.getY() < 0)
        {
            respawn();
        }
    }

    /**
     * Respawn the entity.
     */
    public void respawn()
    {
        transformable.teleport(72, GROUND);
        camera.resetInterval(transformable);
        jump.setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
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
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }
}
