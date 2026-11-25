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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.DisplayState;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Runs {@code platform-api} on Android API 21 (ADR-0044): the display state shared by the platforms, with its fallback
 * reasons. Expected values are the ones of the JVM test DisplayStateTest. The joined reasons are the regression test of
 * the {@code CharSequence.isEmpty()} call found by lint NewApi (API 35), which threw {@code NoSuchMethodError} on API
 * 21.
 */
@RunWith(AndroidJUnit4.class)
public final class PlatformTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 180;
    /** Reason of the scaling fallback. */
    private static final String REASON_INTEGER = "INTEGER scaling falls back to FIT: the surface is smaller than the "
                                                 + "logical frame";

    /**
     * Creates a request.
     *
     * @param policy The scaling policy.
     * @param filters The filters.
     * @return The request.
     */
    private static DisplayRequest request(ScalingPolicy policy, OutputFilter... filters)
    {
        return new DisplayRequest(ScreenMode.WINDOWED, null, policy, Arrays.asList(filters));
    }

    /**
     * Creates the test.
     */
    public PlatformTest()
    {
        super();
    }

    /**
     * A sound request is not degraded, and the geometry is the scaling of the surface.
     */
    @Test
    public void testResolve()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setSurface(WIDTH * 2, HEIGHT * 2);
        state.resolve(request(ScalingPolicy.INTEGER), ScreenMode.WINDOWED, null, "");

        assertFalse(state.getResult().degraded());
        assertEquals("", state.getResult().reason());
        assertEquals(new PresentationGeometry(0, 0, WIDTH * 2, HEIGHT * 2, 2.0, 2.0), state.getGeometry());
    }

    /**
     * The reasons of the platform and of the state are joined in order.
     */
    @Test
    public void testReasons()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setSurface(WIDTH / 2, HEIGHT / 2);
        state.resolve(request(ScalingPolicy.INTEGER), ScreenMode.WINDOWED, null, "Platform reason");
        assertTrue(state.getResult().degraded());
        assertEquals("Platform reason; " + REASON_INTEGER, state.getResult().reason());

        state.resolve(request(ScalingPolicy.INTEGER, OutputFilter.CRT), ScreenMode.WINDOWED, null, "");
        assertEquals(REASON_INTEGER + "; Output filters not applied: no render backend attached",
                     state.getResult().reason());
    }

    /**
     * With a backend, the supported filters are kept, the others skipped and named; the platform modes are kept.
     */
    @Test
    public void testFiltersAndModes()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setBackend(new RenderBackend()
        {
            @Override
            public TextureStore getTextureStore()
            {
                return null;
            }

            @Override
            public boolean isSupported(OutputFilter filter)
            {
                return OutputFilter.NONE.equals(filter) || OutputFilter.BILINEAR.equals(filter);
            }

            @Override
            public void present(CommandBuffer commands)
            {
                // Nothing to draw.
            }
        });
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);
        state.resolve(request(ScalingPolicy.FIT, OutputFilter.NONE, OutputFilter.CRT, OutputFilter.BILINEAR),
                      ScreenMode.FULL_SCREEN,
                      mode,
                      "");

        assertEquals(Collections.singletonList(OutputFilter.BILINEAR), state.getResult().effective().filters());
        assertEquals("Output filter not implemented by the render backend, skipped: lionengine:crt",
                     state.getResult().reason());
        assertEquals(ScreenMode.FULL_SCREEN, state.getResult().effective().screenMode());
        assertEquals(mode, state.getResult().effective().displayMode());
    }
}
