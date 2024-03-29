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
package com.b3dgs.lionengine.example.prototype.gameplay;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.state.StateLast;
import com.b3dgs.lionengine.helper.StateHelper;

/**
 * State attack1 implementation.
 */
public final class StateAttack1 extends StateHelper<EntityModel>
{
    private final Force movement;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateAttack1(EntityModel model, Animation animation)
    {
        super(model, animation);

        movement = model.getMovement();

        addTransition(StateLast.class, () -> is(AnimState.FINISHED));
        addTransition(StateAttack2.class,
                      () -> animatable.getFrame() == animation.getLast() && isFireOnce(DeviceMapping.FIRE1));
        addTransition(StateDie.class, model.getHealth()::isEmpty);
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
    }
}
