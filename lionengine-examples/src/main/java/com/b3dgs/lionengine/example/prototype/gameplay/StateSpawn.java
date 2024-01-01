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
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.helper.StateHelper;
import com.b3dgs.lionengine.io.DeviceControllerVoid;

/**
 * State spawn implementation.
 */
public final class StateSpawn extends StateHelper<EntityModel>
{
    private final Collidable collidable;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateSpawn(EntityModel model, Animation animation)
    {
        super(model, animation);

        collidable = model.getFeature(Collidable.class);

        addTransition(StateRun.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        collidable.setEnabled(false);
    }

    @Override
    public void exit()
    {
        super.exit();

        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return -0.2;
            }

            @Override
            public double getVerticalDirection()
            {
                return 0.0;
            }
        });
        collidable.setEnabled(true);
    }
}
