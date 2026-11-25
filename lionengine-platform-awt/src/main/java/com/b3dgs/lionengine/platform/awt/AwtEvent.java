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
package com.b3dgs.lionengine.platform.awt;

import com.b3dgs.lionengine.platform.PlatformEvent;

/**
 * Lifecycle event of the AWT platform, created on the AWT event thread and delivered on the runtime thread. Immutable,
 * so that a thread never changes an event another one reads.
 */
final class AwtEvent implements PlatformEvent
{
    /** Platform that applies the event. */
    private final AwtPlatform platform;
    /** Kind of event. */
    private final Kind kind;
    /** Surface width after the event, in device pixels. */
    private final int surfaceWidth;
    /** Surface height after the event, in device pixels. */
    private final int surfaceHeight;

    /**
     * Creates an event.
     *
     * @param platform The platform that applies it.
     * @param kind The kind of event.
     * @param surfaceWidth The surface width after the event, in device pixels.
     * @param surfaceHeight The surface height after the event, in device pixels.
     */
    AwtEvent(AwtPlatform platform, Kind kind, int surfaceWidth, int surfaceHeight)
    {
        this.platform = platform;
        this.kind = kind;
        this.surfaceWidth = surfaceWidth;
        this.surfaceHeight = surfaceHeight;
    }

    @Override
    public Kind getKind()
    {
        return kind;
    }

    @Override
    public int getSurfaceWidth()
    {
        return surfaceWidth;
    }

    @Override
    public int getSurfaceHeight()
    {
        return surfaceHeight;
    }

    @Override
    public void deliver()
    {
        platform.deliver(this);
    }
}
