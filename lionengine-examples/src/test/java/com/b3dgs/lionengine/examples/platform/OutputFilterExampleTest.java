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
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link OutputFilterExample} on a headless platform with fake render backends (REQ-DOC-001, REQ-FILTER-001,
 * REQ-FILTER-002).
 */
final class OutputFilterExampleTest
{
    /**
     * Starts a headless platform with a fake backend.
     *
     * @param backend The backend.
     * @param inbox The inbox.
     * @return The started platform.
     */
    private static HeadlessPlatform start(RecordingBackend backend, InboxSpy inbox)
    {
        final HeadlessPlatform platform = new HeadlessPlatform(320,
                                                               240,
                                                               new DisplayRequest(ScreenMode.WINDOWED,
                                                                                  null,
                                                                                  ScalingPolicy.INTEGER,
                                                                                  Collections.emptyList()),
                                                               new Log().getSink());
        platform.attach(backend);
        platform.start(new ManualLoopDriver(), inbox);
        return platform;
    }

    /**
     * On a backend with bilinear filtering only, {@code HQ2X} falls back to bilinear and CRT is skipped, which the
     * listener learns at the frame boundary, with the reasons.
     */
    @Test
    void testFallback()
    {
        final InboxSpy inbox = new InboxSpy();
        final HeadlessPlatform platform = start(new RecordingBackend(OutputFilter.BILINEAR), inbox);
        final List<EffectiveResult> results = new ArrayList<>();

        final DisplayRequest request = OutputFilterExample.requestRetroLook(platform, results::add);
        platform.iterate();
        inbox.deliverAll();

        final EffectiveResult result = results.get(0);
        assertEquals(request, result.requested());
        assertEquals(ScalingPolicy.INTEGER, result.effective().scalingPolicy());
        assertEquals(List.of("lionengine:bilinear"), OutputFilterExample.applied(result));
        assertTrue(result.degraded());
        assertEquals("Output filter lionengine:hq2x not implemented by the render backend, lionengine:bilinear used; "
                     + "Output filter not implemented by the render backend, skipped: lionengine:crt",
                     result.reason());
    }

    /**
     * On a backend with every filter, the chain is applied as requested.
     */
    @Test
    void testApplied()
    {
        final InboxSpy inbox = new InboxSpy();
        final HeadlessPlatform platform = start(new RecordingBackend(OutputFilter.HQ2X, OutputFilter.CRT), inbox);
        final List<EffectiveResult> results = new ArrayList<>();

        OutputFilterExample.requestRetroLook(platform, results::add);
        platform.iterate();
        inbox.deliverAll();

        assertEquals(List.of("lionengine:hq2x", "lionengine:crt"), OutputFilterExample.applied(results.get(0)));
        assertFalse(results.get(0).degraded());
    }
}
