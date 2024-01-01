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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.helper.StateHelper;

/**
 * Idle state implementation.
 */
public final class StateIdle extends StateHelper<EntityModel>
{
    private final Force movement = model.getMovement();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateIdle(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateWalk.class,
                      () -> isGoHorizontal()
                            && !isFire(DeviceMapping.RUN)
                            && UtilMath.isBetween(movement.getDirectionHorizontal(), -0.1, 0.1));
        addTransition(StateRun.class,
                      () -> isGoHorizontal()
                            && isFire(DeviceMapping.RUN)
                            && UtilMath.isBetween(movement.getDirectionHorizontal(), -0.1, 0.1));
        addTransition(StateCrouch.class, this::isGoDown);
        addTransition(StateJump.class, this::isGoUpOnce);
        addTransition(StateAttack1.class, () -> isFire(DeviceMapping.ATTACK));
        addTransition(StateDefend.class, () -> isFire(DeviceMapping.DEFEND));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
    }
}
