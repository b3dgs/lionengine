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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.ManualLoopDriver;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.PlatformContract;
import com.b3dgs.lionengine.platform.PlatformEvent;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Test {@link HeadlessPlatform} against the {@link Platform} contract of the {@code platform-api} test JAR, and its
 * own rules: capability set, event injection, filters without backend (REQ-TEST-001, REQ-RT-008).
 */
final class HeadlessPlatformTest extends PlatformContract
{
    @Override
    protected Platform createPlatform(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        return new HeadlessPlatform(logicalWidth, logicalHeight, request, new Log().getSink());
    }

    @Override
    protected void post(Platform platform, PlatformEvent.Kind kind)
    {
        assertTrue(((HeadlessPlatform) platform).post(kind));
    }

    @Override
    protected void resize(Platform platform, int surfaceWidth, int surfaceHeight)
    {
        assertTrue(((HeadlessPlatform) platform).resize(surfaceWidth, surfaceHeight));
    }

    @Override
    protected void frame(Platform platform)
    {
        ((HeadlessPlatform) platform).iterate();
    }

    /**
     * Creates a headless platform with the suite's sizes.
     *
     * @return The platform, not started.
     */
    private static HeadlessPlatform create()
    {
        return new HeadlessPlatform(WIDTH, HEIGHT, FIT, new Log().getSink());
    }

    /**
     * A capability set by the test is found by its type, and absent once removed (REQ-RT-008).
     */
    @Test
    void testCapability()
    {
        final HeadlessPlatform platform = create();
        final Runnable capability = () ->
        {
            // Capability of the test.
        };

        platform.setCapability(Runnable.class, capability);
        assertTrue(platform.hasCapability(Runnable.class));
        assertSame(capability, platform.getCapability(Runnable.class));

        platform.setCapability(Runnable.class, null);
        assertFalse(platform.hasCapability(Runnable.class));
        assertNull(platform.getCapability(Runnable.class));
        assertThrows(IllegalArgumentException.class, () -> platform.setCapability(null, capability));
    }

    /**
     * The surface and the display mode start at the logical frame size; the worker pool has 2 workers.
     */
    @Test
    void testStartSizes()
    {
        final HeadlessPlatform platform = create();

        assertEquals(WIDTH, platform.getSurfaceWidth());
        assertEquals(HEIGHT, platform.getSurfaceHeight());
        assertEquals(new DisplayMode(WIDTH, HEIGHT, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN),
                     platform.getEffectiveResult().effective().displayMode());
        assertEquals(2, platform.getWorkerPool().getWorkerCount());
    }

    /**
     * Events need a started platform; a resize is posted with its size only.
     */
    @Test
    void testEventArguments()
    {
        final HeadlessPlatform platform = create();

        assertThrows(IllegalStateException.class, () -> platform.post(PlatformEvent.Kind.BACKGROUND));
        assertThrows(IllegalStateException.class, () -> platform.resize(1, 1));
        assertThrows(IllegalStateException.class, platform::iterate);

        platform.start(new ManualLoopDriver(), new InboxSpy());

        assertThrows(IllegalArgumentException.class, () -> platform.post(null));
        assertThrows(IllegalArgumentException.class, () -> platform.post(PlatformEvent.Kind.SURFACE_RESIZED));
        assertThrows(IllegalArgumentException.class, () -> platform.resize(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> platform.resize(1, -1));
        assertThrows(IllegalArgumentException.class,
                     () -> new HeadlessPlatform(WIDTH, HEIGHT, FIT, null));
    }

    /**
     * A requested display mode is applied; filters other than {@code NONE} are not, and it is reported with the
     * {@code INTEGER} fallback.
     */
    @Test
    void testDisplayModeAndFilters()
    {
        final HeadlessPlatform platform = create();
        final InboxSpy inbox = new InboxSpy();
        platform.start(new ManualLoopDriver(), inbox);
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);

        platform.apply(new DisplayRequest(ScreenMode.FULL_SCREEN, mode, ScalingPolicy.FIT, List.of(OutputFilter.NONE)));
        platform.iterate();

        final EffectiveResult none = platform.getEffectiveResult();
        assertEquals(mode, none.effective().displayMode());
        assertEquals(ScreenMode.FULL_SCREEN, none.effective().screenMode());
        assertFalse(none.degraded());

        platform.apply(new DisplayRequest(ScreenMode.WINDOWED,
                                          null,
                                          ScalingPolicy.INTEGER,
                                          List.of(OutputFilter.BILINEAR)));
        platform.resize(100, 100);
        inbox.deliverAll();
        platform.iterate();

        final EffectiveResult degraded = platform.getEffectiveResult();
        assertEquals(mode, degraded.effective().displayMode());
        assertTrue(degraded.effective().filters().isEmpty());
        assertTrue(degraded.degraded());
        assertTrue(degraded.reason().contains("INTEGER"));
        assertTrue(degraded.reason().contains("filters"));
    }
}
