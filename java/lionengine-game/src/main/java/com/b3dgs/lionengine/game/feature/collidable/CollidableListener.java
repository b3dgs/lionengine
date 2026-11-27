/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Notify collision events between two {@link FeatureProvider}.
 */
public interface CollidableListener
{
    /**
     * Notify when a collision occurred with another {@link FeatureProvider}.
     * 
     * @param collidable The collidable reference.
     * @param with The collision collided with (source).
     * @param by The collision collided by (other).
     */
    void notifyCollided(FeatureProvider collidable, Collision with, Collision by);
}
