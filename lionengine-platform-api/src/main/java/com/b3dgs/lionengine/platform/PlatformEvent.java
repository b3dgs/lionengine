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
package com.b3dgs.lionengine.platform;

import com.b3dgs.lionengine.InboxEvent;

/**
 * Lifecycle event of the platform, posted from a platform thread to the inbox (ADR-0018, ADR-0031, ADR-0040,
 * REQ-PLATFORM-002).
 *
 * <p>
 * The platform implements the effect in {@link #deliver()}, on the runtime thread: going to the background pauses the
 * loop and coming to the foreground resumes it; a close request stops the loop unless a {@link CloseListener} vetoes
 * it; a resize never changes the logical frame, and the scaling and the presentation geometry are recomputed at the
 * next frame boundary; context loss is recovered by the render backend. Implementations reuse their instances.
 * </p>
 *
 * @since 11.0.0
 */
public interface PlatformEvent extends InboxEvent
{
    /**
     * Returns the kind of event.
     *
     * @return The kind of event (never {@code null}).
     */
    Kind getKind();

    /**
     * Returns the width of the output surface after the event.
     *
     * @return The surface width in device pixels (not negative).
     */
    int getSurfaceWidth();

    /**
     * Returns the height of the output surface after the event.
     *
     * @return The surface height in device pixels (not negative).
     */
    int getSurfaceHeight();

    /**
     * Kind of platform event.
     */
    enum Kind
    {
        /** The output surface changed size; the logical frame does not. */
        SURFACE_RESIZED,
        /** The application gained the input focus. */
        FOCUS_GAINED,
        /** The application lost the input focus; held inputs are released. */
        FOCUS_LOST,
        /** The application went to the background; the loop is paused. */
        BACKGROUND,
        /** The application came back to the foreground; the loop is resumed. */
        FOREGROUND,
        /** The user or the system asks to close the application. */
        CLOSE_REQUESTED,
        /** The rendering context was lost. */
        CONTEXT_LOST,
        /** The rendering context was restored; informational, the backend recovered its resources. */
        CONTEXT_RESTORED
    }
}
