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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.helper.StateHelper;

/**
 * Attack run state implementation.
 */
public final class StateAttackRun extends StateHelper<EntityModel>
{
    private final Force movement = model.getMovement();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateAttackRun(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> is(AnimState.FINISHED) && (isGoNone() || !isFire(DeviceMapping.RUN)));
        addTransition(StateRun.class, () -> isGoHorizontal() && isFire(DeviceMapping.RUN) && is(AnimState.FINISHED));
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        movement.setDestination(device.getHorizontalDirection() * EntityModel.SPEED_X_RUN, 0.0);
        animatable.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / 20.0);
    }
}
