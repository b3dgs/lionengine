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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.ManualLoopDriver;
import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.CustomFilter;
import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Contract suite of {@link Platform} (ADR-0018, ADR-0020, ADR-0031, ADR-0040, ADR-0045, ADR-0046, REQ-FE-001,
 * REQ-FE-003, REQ-RT-008, REQ-PLATFORM-002, REQ-DISPLAY-007).
 *
 * <p>
 * Published in the {@code platform-api} test JAR: every platform binds it by extending this class, creating a
 * platform, injecting events as its own threads would, and running one frame on its runtime thread. The suite starts
 * the platform with a loop driver that delivers the inbox at the start of each iteration, as the game loop does.
 * </p>
 */
public abstract class PlatformContract
{
    /** Logical frame width of the suite. */
    protected static final int WIDTH = 320;
    /** Logical frame height of the suite. */
    protected static final int HEIGHT = 240;
    /** Windowed request with {@code FIT} scaling and no filter. */
    protected static final DisplayRequest FIT = request(ScalingPolicy.FIT);

    /**
     * Creates a windowed request without display mode change nor filter.
     *
     * @param policy The scaling policy.
     * @return The request.
     */
    protected static DisplayRequest request(ScalingPolicy policy)
    {
        return new DisplayRequest(ScreenMode.WINDOWED, null, policy, Collections.emptyList());
    }

    /**
     * Creates a platform, not started.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param request The initial display request.
     * @return The platform.
     * @throws IllegalArgumentException If a size is not positive or the request is {@code null}.
     */
    protected abstract Platform createPlatform(int logicalWidth, int logicalHeight, DisplayRequest request);

    /**
     * Posts an event other than a resize, as a thread of the platform would.
     *
     * @param platform The started platform.
     * @param kind The kind of event, not {@link PlatformEvent.Kind#SURFACE_RESIZED}.
     */
    protected abstract void post(Platform platform, PlatformEvent.Kind kind);

    /**
     * Posts a resize of the output surface, as a thread of the platform would.
     *
     * @param platform The started platform.
     * @param surfaceWidth The new surface width in device pixels.
     * @param surfaceHeight The new surface height in device pixels.
     */
    protected abstract void resize(Platform platform, int surfaceWidth, int surfaceHeight);

    /**
     * Runs one frame on the runtime thread: the frame boundary of the platform, then one iteration of the loop.
     *
     * @param platform The started platform.
     */
    protected abstract void frame(Platform platform);

    /**
     * Creates and starts a platform with the suite's frame size and {@code FIT} request.
     *
     * @param driver The loop driver.
     * @return The started platform.
     */
    private Platform start(DeliveringDriver driver)
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);
        platform.start(driver, driver.inbox);
        return platform;
    }

    /**
     * Computes the geometry expected from the scaling function.
     *
     * @param platform The platform.
     * @param policy The requested policy.
     * @return The geometry.
     */
    private static PresentationGeometry expected(Platform platform, ScalingPolicy policy)
    {
        final FrameScaling scaling = FrameScaling.of(policy,
                                                     platform.getLogicalWidth(),
                                                     platform.getLogicalHeight(),
                                                     platform.getSurfaceWidth(),
                                                     platform.getSurfaceHeight());
        return new PresentationGeometry(scaling.x(),
                                        scaling.y(),
                                        scaling.width(),
                                        scaling.height(),
                                        scaling.scaleX(),
                                        scaling.scaleY());
    }

    /**
     * The logical frame keeps its creation size; invalid creation arguments are rejected.
     */
    @Test
    void testLogicalFrame()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertEquals(WIDTH, platform.getLogicalWidth());
        assertEquals(HEIGHT, platform.getLogicalHeight());
        assertTrue(platform.getSurfaceWidth() >= 0);
        assertTrue(platform.getSurfaceHeight() >= 0);
        assertThrows(IllegalArgumentException.class, () -> createPlatform(0, HEIGHT, FIT));
        assertThrows(IllegalArgumentException.class, () -> createPlatform(WIDTH, 0, FIT));
        assertThrows(IllegalArgumentException.class, () -> createPlatform(WIDTH, HEIGHT, null));
    }

    /**
     * The platform starts once, with a driver and an inbox.
     */
    @Test
    void testStart()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertThrows(IllegalArgumentException.class, () -> platform.start(null, driver.inbox));
        assertThrows(IllegalArgumentException.class, () -> platform.start(driver, null));
        platform.start(driver, driver.inbox);
        assertThrows(IllegalStateException.class, () -> platform.start(driver, driver.inbox));
    }

    /**
     * An unknown capability is absent; presence and lookup agree (REQ-RT-008).
     */
    @Test
    void testUnknownCapability()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertNull(platform.getCapability(UnknownCapability.class));
        assertFalse(platform.hasCapability(UnknownCapability.class));
    }

    /**
     * The worker pool, the log sink and the initial display state are available before the start.
     */
    @Test
    void testInitialState()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertNotNull(platform.getWorkerPool());
        assertTrue(platform.getWorkerPool().getWorkerCount() >= 0);
        assertNotNull(platform.getLogSink());
        assertEquals(FIT, platform.getEffectiveResult().requested());
        assertEquals(ScalingPolicy.FIT, platform.getEffectiveResult().effective().scalingPolicy());
        assertEquals(expected(platform, ScalingPolicy.FIT), platform.getPresentationGeometry());
    }

    /**
     * Going to the background pauses the loop at the next frame, coming back resumes it (REQ-PLATFORM-002).
     */
    @Test
    void testBackgroundAndForeground()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        frame(platform);

        post(platform, PlatformEvent.Kind.BACKGROUND);
        assertFalse(driver.manual.isPauseRequested());
        frame(platform);
        assertTrue(driver.manual.isPauseRequested());

        post(platform, PlatformEvent.Kind.FOREGROUND);
        frame(platform);
        assertFalse(driver.manual.isPauseRequested());
    }

    /**
     * Focus and context events do not stop or pause the loop.
     */
    @Test
    void testInformationalEvents()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);

        post(platform, PlatformEvent.Kind.FOCUS_LOST);
        post(platform, PlatformEvent.Kind.FOCUS_GAINED);
        post(platform, PlatformEvent.Kind.CONTEXT_LOST);
        post(platform, PlatformEvent.Kind.CONTEXT_RESTORED);
        frame(platform);

        assertFalse(driver.manual.isPauseRequested());
        assertFalse(driver.manual.isStopRequested());
        assertTrue(driver.manual.isRunning());
    }

    /**
     * A close request stops the loop when no listener vetoes it.
     */
    @Test
    void testCloseStops()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final AtomicInteger calls = new AtomicInteger();
        platform.addCloseListener(() ->
        {
            calls.incrementAndGet();
            return true;
        });

        post(platform, PlatformEvent.Kind.CLOSE_REQUESTED);
        frame(platform);

        assertEquals(1, calls.get());
        assertTrue(driver.manual.isStopRequested());
        assertFalse(driver.manual.isRunning());
    }

    /**
     * A listener returning {@code false} vetoes the close; once removed, the next close stops the loop.
     */
    @Test
    void testCloseVeto()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final AtomicInteger calls = new AtomicInteger();
        final CloseListener veto = () ->
        {
            calls.incrementAndGet();
            return false;
        };
        platform.addCloseListener(veto);

        post(platform, PlatformEvent.Kind.CLOSE_REQUESTED);
        frame(platform);
        assertEquals(1, calls.get());
        assertFalse(driver.manual.isStopRequested());

        platform.removeCloseListener(veto);
        platform.removeCloseListener(veto);
        post(platform, PlatformEvent.Kind.CLOSE_REQUESTED);
        frame(platform);
        assertEquals(1, calls.get());
        assertTrue(driver.manual.isStopRequested());
    }

    /**
     * A listener added while a close request is delivered takes effect after that delivery.
     */
    @Test
    void testListenerAddedDuringDelivery()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final AtomicInteger vetoes = new AtomicInteger();
        final CloseListener veto = () ->
        {
            vetoes.incrementAndGet();
            return false;
        };
        platform.addCloseListener(() ->
        {
            platform.addCloseListener(veto);
            return true;
        });

        post(platform, PlatformEvent.Kind.CLOSE_REQUESTED);
        frame(platform);

        assertEquals(0, vetoes.get());
        assertTrue(driver.manual.isStopRequested());
    }

    /**
     * A resize changes the surface at the next frame boundary, never the logical frame, and recomputes the geometry.
     */
    @Test
    void testResize()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        frame(platform);

        resize(platform, 1280, 720);
        frame(platform);

        assertEquals(1280, platform.getSurfaceWidth());
        assertEquals(720, platform.getSurfaceHeight());
        assertEquals(WIDTH, platform.getLogicalWidth());
        assertEquals(HEIGHT, platform.getLogicalHeight());
        assertEquals(new PresentationGeometry(160, 0, 960, 720, 3.0, 3.0), platform.getPresentationGeometry());
    }

    /**
     * A request is returned as given and applied at the next frame boundary, with its effective result and geometry.
     */
    @Test
    void testApplyAtFrameBoundary()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        resize(platform, 1280, 720);
        frame(platform);
        final DisplayRequest stretch = request(ScalingPolicy.STRETCH);

        assertSame(stretch, platform.apply(stretch));
        assertEquals(FIT, platform.getEffectiveResult().requested());

        frame(platform);

        final EffectiveResult result = platform.getEffectiveResult();
        assertSame(stretch, result.requested());
        assertEquals(ScalingPolicy.STRETCH, result.effective().scalingPolicy());
        assertEquals(ScreenMode.WINDOWED, result.effective().screenMode());
        assertNotNull(result.effective().displayMode());
        assertFalse(result.degraded());
        assertEquals(expected(platform, ScalingPolicy.STRETCH), platform.getPresentationGeometry());
    }

    /**
     * {@code INTEGER} falls back to {@code FIT} on a surface smaller than the logical frame; the fallback is reported
     * and ends when the surface is large enough again (REQ-DISPLAY-007).
     */
    @Test
    void testIntegerFallback()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        platform.apply(request(ScalingPolicy.INTEGER));
        resize(platform, 300, 200);
        frame(platform);

        final EffectiveResult fallback = platform.getEffectiveResult();
        assertEquals(ScalingPolicy.FIT, fallback.effective().scalingPolicy());
        assertTrue(fallback.degraded());
        assertFalse(fallback.reason().isEmpty());
        assertEquals(expected(platform, ScalingPolicy.FIT), platform.getPresentationGeometry());

        resize(platform, 1000, 800);
        frame(platform);

        final EffectiveResult integer = platform.getEffectiveResult();
        assertEquals(ScalingPolicy.INTEGER, integer.effective().scalingPolicy());
        assertFalse(integer.degraded());
        assertEquals(new PresentationGeometry(20, 40, 960, 720, 3.0, 3.0), platform.getPresentationGeometry());
    }

    /**
     * The platform supports at least one screen mode, and the query refuses {@code null} (REQ-DISPLAY-001).
     */
    @Test
    void testScreenModeCapability()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);
        boolean any = false;
        for (final ScreenMode mode : ScreenMode.values())
        {
            any |= platform.isSupported(mode);
        }

        assertTrue(any);
        assertThrows(IllegalArgumentException.class, () -> platform.isSupported(null));
    }

    /**
     * Every screen mode can be requested without exception: a supported mode is applied as requested, another falls
     * back to a supported mode, reported as degraded with its reason (REQ-DISPLAY-002, ADR-0006, ADR-0060).
     */
    @Test
    void testScreenModeFallback()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        for (final ScreenMode mode : ScreenMode.values())
        {
            platform.apply(new DisplayRequest(mode, null, ScalingPolicy.FIT, List.of()));
            frame(platform);

            final EffectiveResult result = platform.getEffectiveResult();
            final ScreenMode effective = result.effective().screenMode();
            assertTrue(platform.isSupported(effective), mode + " gave " + effective);
            if (platform.isSupported(mode))
            {
                assertEquals(mode, effective);
            }
            else
            {
                assertTrue(result.degraded());
                assertTrue(result.reason().startsWith(mode + " not supported, " + effective + " used"),
                           result.reason());
            }
        }
        platform.apply(FIT);
        frame(platform);
    }

    /**
     * The display modes are listed, at least one, without duplicate, unmodifiable and fixed (REQ-DISPLAY-003,
     * ADR-0061).
     */
    @Test
    void testDisplayModes()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);
        final List<DisplayMode> modes = platform.getDisplayModes();

        assertFalse(modes.isEmpty());
        assertEquals(modes.size(), new HashSet<>(modes).size());
        assertFalse(modes.contains(null));
        final DisplayMode first = modes.get(0);
        assertThrows(UnsupportedOperationException.class, () -> modes.add(first));
        assertEquals(modes, platform.getDisplayModes());
    }

    /**
     * A display mode that is not listed can be requested without exception: another mode is applied, reported as
     * degraded with its reason (REQ-DISPLAY-003, ADR-0006, ADR-0061).
     */
    @Test
    void testDisplayModeNotListed()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final DisplayMode missing = new DisplayMode(7, 5, 1, 1);
        assertFalse(platform.getDisplayModes().contains(missing));

        platform.apply(new DisplayRequest(ScreenMode.WINDOWED, missing, ScalingPolicy.FIT, List.of()));
        frame(platform);

        final EffectiveResult result = platform.getEffectiveResult();
        assertNotNull(result.effective().displayMode());
        assertNotEquals(missing, result.effective().displayMode());
        assertTrue(result.degraded());
        assertTrue(result.reason().startsWith("Display mode 7x5 1Hz 1bits not available, "), result.reason());
        platform.apply(FIT);
        frame(platform);
    }

    /**
     * The result of an applied request is delivered to the display listeners through the inbox, in the frame where it
     * is applied, in the order they were added; a removed listener is not called (REQ-DISPLAY-005, REQ-DISPLAY-008).
     */
    @Test
    void testDisplayListener()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final List<String> calls = new ArrayList<>();
        final DisplayListener removed = result -> calls.add("removed");
        platform.addDisplayListener(result -> calls.add("first " + result.effective().scalingPolicy()));
        platform.addDisplayListener(result -> calls.add("second " + result.requested().scalingPolicy()));
        platform.addDisplayListener(removed);
        platform.removeDisplayListener(removed);
        frame(platform);

        platform.apply(request(ScalingPolicy.STRETCH));
        assertTrue(calls.isEmpty());
        frame(platform);

        assertEquals(List.of("first STRETCH", "second STRETCH"), calls);
        frame(platform);
        assertEquals(2, calls.size());
        assertThrows(IllegalArgumentException.class, () -> platform.addDisplayListener(null));
    }

    /**
     * A resize that changes the effective result, such as the start of the {@code INTEGER} fallback, is announced to
     * the display listeners (REQ-DISPLAY-007, REQ-DISPLAY-008).
     */
    @Test
    void testDisplayListenerOnResize()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = start(driver);
        final List<EffectiveResult> results = new ArrayList<>();
        platform.apply(request(ScalingPolicy.INTEGER));
        frame(platform);
        platform.addDisplayListener(results::add);

        resize(platform, 300, 200);
        frame(platform);
        frame(platform);

        assertEquals(1, results.size());
        assertEquals(ScalingPolicy.FIT, results.get(0).effective().scalingPolicy());
        assertTrue(results.get(0).degraded());
    }

    /**
     * The backend is attached before the start only.
     */
    @Test
    void testAttach()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertThrows(IllegalArgumentException.class, () -> platform.attach(null));
        platform.attach(new FilterBackend());
        platform.attach(new FilterBackend());
        platform.start(driver, driver.inbox);
        final FilterBackend late = new FilterBackend();
        assertThrows(IllegalStateException.class, () -> platform.attach(late));
    }

    /**
     * The filters applied are those the attached backend implements, in order; the others are skipped and reported;
     * {@code NONE} is no filter (REQ-FILTER-002).
     */
    @Test
    void testFiltersOfBackend()
    {
        final DisplayRequest request = new DisplayRequest(ScreenMode.WINDOWED,
                                                          null,
                                                          ScalingPolicy.FIT,
                                                          List.of(OutputFilter.BILINEAR, OutputFilter.CRT));
        final Platform platform = createPlatform(WIDTH, HEIGHT, request);
        final EffectiveResult without = platform.getEffectiveResult();
        assertTrue(without.effective().filters().isEmpty());
        assertTrue(without.degraded());

        platform.attach(new FilterBackend());

        final EffectiveResult with = platform.getEffectiveResult();
        assertSame(request, with.requested());
        assertEquals(List.of(OutputFilter.BILINEAR), with.effective().filters());
        assertTrue(with.degraded());
        assertTrue(with.reason().contains(OutputFilter.CRT.id()));
    }

    /**
     * A requested filter the backend lacks falls back along its order, at the frame boundary, without exception:
     * {@code HQ2X} to bilinear, reported to the display listeners (REQ-FILTER-002, ADR-0006, ADR-0062).
     */
    @Test
    void testFilterFallback()
    {
        final DeliveringDriver driver = new DeliveringDriver();
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);
        platform.attach(new FilterBackend());
        final List<EffectiveResult> results = new ArrayList<>();
        platform.addDisplayListener(results::add);
        platform.start(driver, driver.inbox);

        platform.apply(new DisplayRequest(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, List.of(OutputFilter.HQ2X)));
        frame(platform);

        final EffectiveResult result = platform.getEffectiveResult();
        assertEquals(List.of(OutputFilter.BILINEAR), result.effective().filters());
        assertTrue(result.degraded());
        assertEquals("Output filter lionengine:hq2x not implemented by the render backend, lionengine:bilinear used",
                     result.reason());
        assertEquals(List.of(result), results);
        platform.apply(FIT);
        frame(platform);
    }

    /**
     * {@code null} requests and listeners are rejected; removing an unknown listener has no effect.
     */
    @Test
    void testArguments()
    {
        final Platform platform = createPlatform(WIDTH, HEIGHT, FIT);

        assertThrows(IllegalArgumentException.class, () -> platform.apply(null));
        assertThrows(IllegalArgumentException.class, () -> platform.addCloseListener(null));
        platform.removeCloseListener(() -> true);
        platform.removeCloseListener(null);
    }

    /**
     * Capability type that no platform provides.
     */
    private interface UnknownCapability
    {
        // Marker of the suite.
    }

    /**
     * Backend that implements {@code NONE} and bilinear filtering, and draws nothing.
     */
    private static final class FilterBackend implements RenderBackend, TextureStore
    {
        /** Last texture handle. */
        private int textures;

        /**
         * Creates the backend.
         */
        FilterBackend()
        {
            super();
        }

        @Override
        public TextureStore getTextureStore()
        {
            return this;
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
        public void register(CustomFilter filter)
        {
            // No custom filter in this test.
        }

        @Override
        public void present(CommandBuffer commands)
        {
            // Draws nothing.
        }

        @Override
        public int create(int width, int height, int[] argb)
        {
            textures++;
            return textures;
        }

        @Override
        public void prepare(int texture)
        {
            // Nothing to upload.
        }

        @Override
        public void release(int texture)
        {
            // Nothing to release.
        }
    }

    /**
     * Driver that delivers the inbox at the start of each iteration, as the game loop does.
     */
    private static final class DeliveringDriver implements LoopDriver
    {
        /** Recorded control requests and iterations. */
        private final ManualLoopDriver manual = new ManualLoopDriver();
        /** Inbox handed to the platform. */
        private final InboxSpy inbox = new InboxSpy();

        /**
         * Creates the driver.
         */
        DeliveringDriver()
        {
            super();
        }

        @Override
        public void iterate()
        {
            inbox.deliverAll();
            manual.iterate();
        }

        @Override
        public boolean isRunning()
        {
            return manual.isRunning();
        }

        @Override
        public void pause()
        {
            manual.pause();
        }

        @Override
        public void resume()
        {
            manual.resume();
        }

        @Override
        public void stop()
        {
            manual.stop();
        }

        @Override
        public void setTimeScale(double scale)
        {
            manual.setTimeScale(scale);
        }
    }
}
