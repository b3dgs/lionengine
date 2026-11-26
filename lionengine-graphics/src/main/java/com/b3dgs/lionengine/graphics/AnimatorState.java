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
package com.b3dgs.lionengine.graphics;

/**
 * State of an {@link Animator} (ADR-0058, REQ-ANIM-002).
 *
 * @since 11.0.0
 */
public enum AnimatorState
{
    /** Not playing, on the first frame; the initial state. */
    STOPPED,
    /** Playing: each update advances the frames. */
    PLAYING,
    /** Not playing, on the frame and time kept by the pause, until resumed. */
    PAUSED,
    /**
     * A {@link AnimationLoop#ONCE} animation whose last frame was shown for its full duration; it stays on that frame.
     */
    FINISHED
}
