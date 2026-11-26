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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.InboxEvent;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.DisplayState;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ColorEffect;
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
        state.resolve(request(ScalingPolicy.INTEGER), null, "");

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
        state.resolve(request(ScalingPolicy.INTEGER), null, "Platform reason");
        assertTrue(state.getResult().degraded());
        assertEquals("Platform reason; " + REASON_INTEGER, state.getResult().reason());

        state.resolve(request(ScalingPolicy.INTEGER, OutputFilter.CRT), null, "");
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
            public boolean isSupported(ColorEffect effect)
            {
                return true;
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
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_WINDOW));
        state.resolve(new DisplayRequest(ScreenMode.FULL_SCREEN,
                                         null,
                                         ScalingPolicy.FIT,
                                         Arrays.asList(OutputFilter.NONE, OutputFilter.CRT, OutputFilter.BILINEAR)),
                      mode,
                      "");

        assertEquals(Collections.singletonList(OutputFilter.BILINEAR), state.getResult().effective().filters());
        assertEquals("FULL_SCREEN not supported, FULL_WINDOW used; "
                     + "Output filter not implemented by the render backend, skipped: lionengine:crt",
                     state.getResult().reason());
        assertEquals(ScreenMode.FULL_WINDOW, state.getResult().effective().screenMode());
        assertEquals(mode, state.getResult().effective().displayMode());
    }

    /**
     * The screen-mode fallback follows the orders of ADR-0006, and a posted result reaches the display listeners at
     * the inbox delivery (REQ-DISPLAY-002, REQ-DISPLAY-008, ADR-0060).
     */
    @Test
    public void testScreenModesAndListener()
    {
        final DisplayState state = new DisplayState(320, 240);
        state.setSupported(EnumSet.of(ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.FULL_SCREEN, state.selectScreenMode(ScreenMode.WINDOWED));
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.WINDOWED, state.selectScreenMode(ScreenMode.FULL_WINDOW));
        assertFalse(state.isSupported(ScreenMode.FULL_WINDOW));

        final List<String> calls = new ArrayList<>();
        final List<InboxEvent> events = new ArrayList<>();
        state.addListener(result -> calls.add(result.effective().screenMode().name()));
        state.resolve(new DisplayRequest(ScreenMode.FULL_WINDOW, null, ScalingPolicy.FIT, Collections.emptyList()),
                      null,
                      "");
        assertTrue(state.post(new Inbox()
        {
            @Override
            public boolean post(InboxEvent event)
            {
                return events.add(event);
            }

            @Override
            public void deliverAll()
            {
                for (final InboxEvent event : events)
                {
                    event.deliver();
                }
            }

            @Override
            public long getDroppedCount()
            {
                return 0L;
            }
        }));
        assertTrue(calls.isEmpty());
        events.get(0).deliver();
        assertEquals(Collections.singletonList("WINDOWED"), calls);
        assertEquals("FULL_WINDOW not supported, WINDOWED used", state.getResult().reason());
    }

    /**
     * The display modes are copied without duplicates and the nearest one is selected: size, then refresh rate, then
     * depth; a mode that does not match the request is reported (REQ-DISPLAY-003, ADR-0061).
     */
    @Test
    public void testDisplayModes()
    {
        final DisplayState state = new DisplayState(320, 240);
        final DisplayMode full = new DisplayMode(1920, 1080, 60, 32);
        final DisplayMode hd60 = new DisplayMode(1280, 720, 60, 32);
        final DisplayMode hd75 = new DisplayMode(1280, 720, 75, 32);
        state.setDisplayModes(Arrays.asList(full, hd75, hd60, full));

        assertEquals(Arrays.asList(full, hd75, hd60), state.getDisplayModes());
        assertEquals(hd60, state.selectDisplayMode(new DisplayMode(1300, 700, 0, 0), full));
        assertEquals(hd75, state.selectDisplayMode(new DisplayMode(1280, 720, 72, 32), full));
        assertTrue(DisplayState.matches(new DisplayMode(1920, 1080, 0, 0), full));

        state.resolve(new DisplayRequest(ScreenMode.WINDOWED, hd75, ScalingPolicy.FIT, Collections.emptyList()),
                      full,
                      "");
        assertEquals("Display mode 1280x720 75Hz 32bits not available, 1920x1080 60Hz 32bits used",
                     state.getResult().reason());
    }
}
