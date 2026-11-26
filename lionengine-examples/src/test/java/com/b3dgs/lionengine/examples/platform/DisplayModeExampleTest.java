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
package com.b3dgs.lionengine.examples.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.ManualLoopDriver;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;

/**
 * Runs {@link DisplayModeExample} on a headless platform with a fake monitor (REQ-DOC-001, REQ-DISPLAY-003,
 * REQ-DISPLAY-008).
 */
final class DisplayModeExampleTest
{
    /** Full HD display mode at 60 Hz. */
    private static final DisplayMode FULL_HD = new DisplayMode(1920, 1080, 60, 32);
    /** HD display mode, unknown refresh rate and depth. */
    private static final DisplayMode HD = new DisplayMode(1280, 720, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN);

    /**
     * Creates the started headless platform, its monitor offering {@link #FULL_HD} and {@link #HD}.
     *
     * @param inbox The inbox.
     * @return The platform.
     */
    private static HeadlessPlatform start(InboxSpy inbox)
    {
        final HeadlessPlatform platform = new HeadlessPlatform(320,
                                                               240,
                                                               new DisplayRequest(ScreenMode.WINDOWED,
                                                                                  null,
                                                                                  ScalingPolicy.INTEGER,
                                                                                  Collections.emptyList()),
                                                               new Log().getSink());
        platform.setDisplayModes(List.of(FULL_HD, HD));
        platform.start(new ManualLoopDriver(), inbox);
        return platform;
    }

    /**
     * The modes are described for a menu; a listed mode is applied as chosen, which the listener learns at the frame
     * boundary.
     */
    @Test
    void testListedMode()
    {
        final InboxSpy inbox = new InboxSpy();
        final HeadlessPlatform platform = start(inbox);
        final List<EffectiveResult> results = new ArrayList<>();

        assertEquals(List.of("1920x1080 60 Hz 32 bits", "1280x720"), DisplayModeExample.describe(platform));
        final DisplayRequest request = DisplayModeExample.requestDisplayMode(platform, HD, results::add);
        platform.iterate();
        inbox.deliverAll();

        assertEquals(1, results.size());
        final EffectiveResult result = results.get(0);
        assertEquals(request, result.requested());
        assertEquals(HD, result.effective().displayMode());
        assertEquals(ScreenMode.FULL_SCREEN, result.effective().screenMode());
        assertEquals(ScalingPolicy.INTEGER, result.effective().scalingPolicy());
        assertFalse(result.degraded());
    }

    /**
     * A mode that is not listed is replaced by the nearest one, with the reason.
     */
    @Test
    void testNearestMode()
    {
        final InboxSpy inbox = new InboxSpy();
        final HeadlessPlatform platform = start(inbox);
        final List<EffectiveResult> results = new ArrayList<>();

        DisplayModeExample.requestDisplayMode(platform, new DisplayMode(1920, 1200, 60, 32), results::add);
        platform.iterate();
        inbox.deliverAll();

        final EffectiveResult result = results.get(0);
        assertEquals(FULL_HD, result.effective().displayMode());
        assertTrue(result.degraded());
        assertEquals("Display mode 1920x1200 60Hz 32bits not available, 1920x1080 60Hz 32bits used", result.reason());
    }
}
