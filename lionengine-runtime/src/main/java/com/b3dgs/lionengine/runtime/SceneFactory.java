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
package com.b3dgs.lionengine.runtime;

/**
 * Creates the scene of an id of a {@link SceneFlow}, each time the scene is entered (ADR-0036, REQ-SCENE-004).
 *
 * @param <C> The type of the game context.
 * @since 11.0.0
 */
@FunctionalInterface
public interface SceneFactory<C>
{
    /**
     * Creates a new scene instance, never given to a scene manager before.
     *
     * @param context The game context, shared by the scenes of the flow.
     * @return The new scene (never {@code null}).
     */
    Scene create(C context);
}
