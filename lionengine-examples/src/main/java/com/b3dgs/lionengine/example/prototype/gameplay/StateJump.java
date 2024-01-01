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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.helper.StateHelper;

/**
 * State jump implementation.
 */
public final class StateJump extends StateHelper<EntityModel>
{
    private final Force movement;
    private final Force jump;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateJump(EntityModel model, Animation animation)
    {
        super(model, animation);

        movement = model.getMovement();
        jump = model.getJump();

        addTransition(StateFall.class, () -> Double.compare(jump.getDirectionVertical(), 0.0) == 0);
        addTransition(StateDie.class, model.getHealth()::isEmpty);
    }

    @Override
    public void enter()
    {
        super.enter();

        jump.zero();
        jump.setDirection(0.0, 5.0);
        body.resetGravity();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        updateMirrorHorizontal();
        movement.setDestination(device.getHorizontalDirection() * 2, 0.0);
    }
}
