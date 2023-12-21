/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.state.StateChecker;
import com.b3dgs.lionengine.helper.StateHelper;

/**
 * Turn state implementation.
 */
public final class StateTurn extends StateHelper<EntityModel>
{
    private final Force movement = model.getMovement();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateTurn(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, new StateChecker()
        {
            @Override
            public boolean getAsBoolean()
            {
                return device.getHorizontalDirection() == 0
                       && movement.getDirectionHorizontal() == 0
                       && device.getVerticalDirection() == 0;
            }

            @Override
            public void exit()
            {
                mirrorHorizontal();
            }
        });
        addTransition(StateIdle.class,
                      () -> isCollideX() || device.getHorizontalDirection() == 0 && device.getVerticalDirection() == 0);
        addTransition(StateWalk.class,
                      () -> (device.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0
                             || device.getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                            && device.getVerticalDirection() == 0);
        addTransition(StateJump.class, () -> device.getVerticalDirection() > 0);
        addTransition(StateFall.class, () -> !isCollideY());
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        movement.setDestination(device.getHorizontalDirection() * EntityModel.SPEED_X, 0.0);
    }
}
