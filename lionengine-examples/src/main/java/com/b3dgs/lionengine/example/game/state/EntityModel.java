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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.helper.EntityModelHelper;
import com.b3dgs.lionengine.io.DeviceController;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements Routine
{
    /** Horizontal speed. */
    static final double SPEED_X_WALK = 2.0;
    /** Horizontal speed. */
    static final double SPEED_X_RUN = 4.0;

    private final Transformable transformable;
    private final Mirrorable mirrorable;
    private final Body body;
    private final Rasterable rasterable;

    private final Force movement = new Force();
    private final Force jump = new Force();

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param mirrorable The mirrorable feature.
     * @param transformable The transformable feature.
     * @param body The body feature.
     * @param rasterable The rasterable feature.
     */
    public EntityModel(Services services,
                       Setup setup,
                       Mirrorable mirrorable,
                       Transformable transformable,
                       Body body,
                       Rasterable rasterable)
    {
        super(services, setup);

        this.mirrorable = mirrorable;
        this.transformable = transformable;
        this.body = body;
        this.rasterable = rasterable;

        movement.setVelocity(1.0);

        jump.setSensibility(0.1);
        jump.setVelocity(0.5);

        body.setGravity(0.5);
        body.setGravityMax(5);

        setInput(services.get(DeviceController.class));
    }

    @Override
    public void update(double extrp)
    {
        movement.update(extrp);
        jump.update(extrp);

        transformable.moveLocation(extrp, body, movement, jump);

        if (transformable.getY() < 32)
        {
            transformable.teleportY(32);
            body.resetGravity();
        }

        final double side = input.getHorizontalDirection();
        if (side < 0 && movement.getDirectionHorizontal() < 0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
            rasterable.setFrameOffsets(28, 0);

        }
        else if (side > 0 && movement.getDirectionHorizontal() > 0)
        {
            mirrorable.mirror(Mirror.NONE);
            rasterable.setFrameOffsets(0, 0);
        }
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
