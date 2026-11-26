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
 * A transition of a {@link SceneFlow}, checked by its builder (ADR-0036).
 *
 * @param <C> The type of the game context.
 * @param from The id of the source scene, or {@link SceneFlowBuilder#ANY_SCENE}.
 * @param trigger The trigger name.
 * @param condition The condition, always true when none was given.
 * @param kind The kind.
 * @param to The id of the target scene, {@code null} for {@link TransitionKind#EXIT}.
 */
record FlowTransition<C>(String from, String trigger, TransitionCondition<C> condition, TransitionKind kind, String to)
{
    // Value type of the flow.
}
