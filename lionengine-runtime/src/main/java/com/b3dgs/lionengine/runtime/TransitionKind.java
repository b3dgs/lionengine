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
 * Kind of a transition of a {@link SceneFlow}: what happens to the scene stack when it applies (ADR-0036,
 * REQ-SCENE-005, REQ-SCENE-006).
 *
 * @since 11.0.0
 */
public enum TransitionKind
{
    /** Replaces the whole stack by the target scene: every scene is left from the top, then the target is entered. */
    SWITCH,
    /** Leaves every scene of the stack from the top, then requests the stop of the loop; no target. */
    EXIT
}
