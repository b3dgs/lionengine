/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Object self collision listener mock.
 */
final class ObjectSelf extends FeaturableModel implements CollidableListener
{
    /**
     * Create self.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    ObjectSelf(Services services, Setup setup)
    {
        super(services, setup);
    }

    /** Called flag. */
    final AtomicReference<Collidable> called = new AtomicReference<>();

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        called.set(collidable);
    }
}
