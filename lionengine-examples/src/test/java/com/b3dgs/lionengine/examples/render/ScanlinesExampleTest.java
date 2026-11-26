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
package com.b3dgs.lionengine.examples.render;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Canvas;
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
import com.b3dgs.lionengine.platform.awt.AwtSurface;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.awt.AwtRenderBackend;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link ScanlinesExample} on the CPU renderer and on the GPU fake, headless (REQ-DOC-001, REQ-FILTER-003).
 */
final class ScanlinesExampleTest
{
    /**
     * Creates a headless platform.
     *
     * @return The platform, not started.
     */
    private static HeadlessPlatform platform()
    {
        return new HeadlessPlatform(320,
                                    240,
                                    new DisplayRequest(ScreenMode.WINDOWED,
                                                       null,
                                                       ScalingPolicy.FIT,
                                                       Collections.emptyList()),
                                    new Log().getSink());
    }

    /**
     * Installs the filter on a started platform and returns the result the listener receives.
     *
     * @param platform The platform, with its backend attached.
     * @param backend The backend.
     * @return The effective result of the request.
     */
    private static EffectiveResult install(HeadlessPlatform platform, RenderBackend backend)
    {
        final InboxSpy inbox = new InboxSpy();
        platform.start(new ManualLoopDriver(), inbox);
        final List<EffectiveResult> results = new ArrayList<>();
        ScanlinesExample.install(backend, platform, results::add);
        platform.iterate();
        inbox.deliverAll();
        return results.get(0);
    }

    /**
     * Odd rows are darkened to half, even rows kept, alpha opaque.
     */
    @Test
    void testDarken()
    {
        final int[] source =
        {
            0xFFFFFFFF, 0xFF808080,
            0xFFFFFFFF, 0xFF808080
        };
        final int[] target = new int[4];

        new ScanlinesExample().getCpuFilter().apply(source, 2, 2, target, 2, 2);

        assertArrayEquals(new int[]
        {
            0xFFFFFFFF, 0xFF808080, 0xFF7F7F7F, 0xFF404040
        }, target);
    }

    /**
     * On the CPU renderer of the desktop, the filter is applied after the scaling filter.
     */
    @Test
    void testCpuRenderer()
    {
        final HeadlessPlatform platform = platform();
        final Canvas canvas = new Canvas();
        platform.setCapability(AwtSurface.class, () -> canvas);
        final AwtRenderBackend backend = new AwtRenderBackend(platform);
        platform.attach(backend);

        final EffectiveResult result = install(platform, backend);

        assertEquals(List.of(ScanlinesExample.SCANLINES), result.effective().filters());
        assertFalse(result.degraded());
    }

    /**
     * On a GPU backend, without a fragment shader, the filter is skipped and the skip is reported.
     */
    @Test
    void testGpuBackendSkips()
    {
        final HeadlessPlatform platform = platform();
        final RecordingBackend backend = new RecordingBackend();
        platform.attach(backend);

        final EffectiveResult result = install(platform, backend);

        assertTrue(result.effective().filters().isEmpty());
        assertEquals("Output filter not implemented by the render backend, skipped: game:scanlines", result.reason());
        assertEquals(List.of(OutputFilter.NONE, ScanlinesExample.SCANLINES), result.requested().filters());
    }
}
