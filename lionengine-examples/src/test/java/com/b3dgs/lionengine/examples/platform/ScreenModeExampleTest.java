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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.ManualLoopDriver;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;

/**
 * Runs {@link ScreenModeExample} on a headless platform without exclusive full screen (REQ-DOC-001, REQ-DISPLAY-001,
 * REQ-DISPLAY-002, REQ-DISPLAY-008).
 */
final class ScreenModeExampleTest
{
    /**
     * The supported modes are listed; full screen falls back to a full window, which the listener learns at the frame
     * boundary, with the reason.
     */
    @Test
    void testFullScreenFallback()
    {
        final HeadlessPlatform platform = new HeadlessPlatform(320,
                                                               240,
                                                               new DisplayRequest(ScreenMode.WINDOWED,
                                                                                  null,
                                                                                  ScalingPolicy.INTEGER,
                                                                                  Collections.emptyList()),
                                                               new Log().getSink());
        platform.setScreenModeSupported(ScreenMode.FULL_SCREEN, false);
        final InboxSpy inbox = new InboxSpy();
        platform.start(new ManualLoopDriver(), inbox);
        final List<EffectiveResult> results = new ArrayList<>();

        assertEquals(List.of(ScreenMode.WINDOWED, ScreenMode.FULL_WINDOW), ScreenModeExample.listSupported(platform));
        final DisplayRequest request = ScreenModeExample.requestFullScreen(platform, results::add);
        assertTrue(results.isEmpty());
        platform.iterate();
        inbox.deliverAll();

        assertEquals(1, results.size());
        final EffectiveResult result = results.get(0);
        assertEquals(request, result.requested());
        assertEquals(ScreenMode.FULL_WINDOW, result.effective().screenMode());
        assertEquals(ScalingPolicy.INTEGER, result.effective().scalingPolicy());
        assertTrue(result.degraded());
        assertEquals("FULL_SCREEN not supported, FULL_WINDOW used", result.reason());
    }
}
