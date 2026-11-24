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
package com.b3dgs.lionengine.testsupport;

import com.b3dgs.lionengine.platform.PlatformEvent;

/**
 * Platform event of the headless platform, one reused instance per kind (ADR-0031, REQ-PLATFORM-002).
 */
final class HeadlessEvent implements PlatformEvent
{
    /** Platform that applies the event. */
    private final HeadlessPlatform platform;
    /** Kind of event. */
    private final Kind kind;
    /** Surface width after the event, in device pixels. */
    private int surfaceWidth;
    /** Surface height after the event, in device pixels. */
    private int surfaceHeight;

    /**
     * Creates an event.
     *
     * @param platform The platform that applies the event.
     * @param kind The kind of event.
     */
    HeadlessEvent(HeadlessPlatform platform, Kind kind)
    {
        this.platform = platform;
        this.kind = kind;
    }

    /**
     * Sets the surface size carried by the event.
     *
     * @param width The surface width in device pixels.
     * @param height The surface height in device pixels.
     */
    void setSurface(int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
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
