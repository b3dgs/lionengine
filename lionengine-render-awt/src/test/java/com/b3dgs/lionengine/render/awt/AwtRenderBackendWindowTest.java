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
package com.b3dgs.lionengine.render.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.platform.awt.AwtPlatform;
import com.b3dgs.lionengine.platform.awt.AwtSurface;
import com.b3dgs.lionengine.render.CommandBuffer;

/**
 * Test of the AWT renderer in a real window of the AWT platform (platform test, REQ-PLATFORM-001, REQ-FE-003).
 */
@Tag(AwtRenderFixtures.PLATFORM)
final class AwtRenderBackendWindowTest
{
    /** Logical frame width. */
    private static final int WIDTH = 32;
    /** Logical frame height. */
    private static final int HEIGHT = 24;
    /** Longest wait for the frames, in nanoseconds. */
    private static final long TIMEOUT = TimeUnit.SECONDS.toNanos(10L);
    /** Pause between two checks of the surface size, in nanoseconds. */
    private static final long CHECK_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);

    /**
     * Skips the tests without display.
     */
    @BeforeAll
    static void requireDisplay()
    {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    /**
     * Waits until the loop has presented a number of frames.
     *
     * @param driver The driver.
     * @param frames The number of frames.
     */
    private static void await(PresentingDriver driver, int frames)
    {
        final long end = System.nanoTime() + TIMEOUT;
        while (driver.getFrames() < frames)
        {
            assertTrue(System.nanoTime() < end, "Frames not presented in time");
            Thread.onSpinWait();
        }
    }

    /**
     * Waits until the platform has the surface size, which it takes from the resize event of the canvas. The window
     * system delivers that event after the resize, so frames presented before it are drawn with the earlier size. The
     * surface size is written by the loop thread before it presents a frame: the volatile frame counter is read before
     * each check, which makes the size visible to this thread.
     *
     * @param platform The platform.
     * @param driver The driver, whose frame counter is read.
     * @param width The surface width in device pixels.
     * @param height The surface height in device pixels.
     * @return The number of frames presented when the surface size was found.
     */
    private static int awaitSurface(AwtPlatform platform, PresentingDriver driver, int width, int height)
    {
        final long end = System.nanoTime() + TIMEOUT;
        int frames = driver.getFrames();
        while (platform.getSurfaceWidth() != width || platform.getSurfaceHeight() != height)
        {
            assertTrue(System.nanoTime() < end, "Surface size not received in time");
            LockSupport.parkNanos(CHECK_NANOS);
            frames = driver.getFrames();
        }
        return frames;
    }

    /**
     * Resizes the window so that the canvas has a size, and waits for the AWT event thread.
     *
     * @param canvas The canvas.
     * @param width The canvas width in AWT units.
     * @param height The canvas height in AWT units.
     * @throws InterruptedException If interrupted.
     * @throws InvocationTargetException If the resize fails.
     */
    private static void resize(Canvas canvas, int width, int height)
            throws InterruptedException, InvocationTargetException
    {
        EventQueue.invokeAndWait(() ->
        {
            final Frame window = (Frame) canvas.getParent();
            final Insets insets = window.getInsets();
            window.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
            window.validate();
        });
        EventQueue.invokeAndWait(() ->
        {
            // Processes the resize events queued before.
        });
    }

    /**
     * The logical frame is scaled into the window with the presentation geometry, black outside it; the stop closes
     * the window.
     *
     * @throws AWTException If the screen cannot be captured.
     * @throws InterruptedException If interrupted.
     * @throws InvocationTargetException If the resize fails.
     */
    @Test
    void testPresent() throws AWTException, InterruptedException, InvocationTargetException
    {
        final AwtPlatform platform = new AwtPlatform(WIDTH, HEIGHT, AwtRenderFixtures.REQUEST, AwtRenderFixtures.SINK);
        final AwtRenderBackend backend = new AwtRenderBackend(platform);
        platform.attach(backend);
        final int[] pixels = new int[WIDTH * HEIGHT];
        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i] = i % WIDTH < WIDTH / 2 ? 0xFFFF0000 : 0xFF00FF00;
        }
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 1);
        commands.add(backend.getTextureStore().create(WIDTH, HEIGHT, pixels), 0, 0, WIDTH, HEIGHT);
        final InboxSpy inbox = new InboxSpy();
        final PresentingDriver driver = new PresentingDriver(inbox, () -> backend.present(commands));
        final Thread game = new Thread(() -> platform.start(driver, inbox));
        game.start();
        await(driver, 3);

        final Canvas canvas = platform.getCapability(AwtSurface.class).getCanvas();
        final double scale = canvas.getGraphicsConfiguration().getDefaultTransform().getScaleX();
        assumeTrue(Double.compare(scale, 1.0) == 0, "Pixel checks written for a scale of 1");
        resize(canvas, 3 * WIDTH, 2 * HEIGHT);
        await(driver, awaitSurface(platform, driver, 3 * WIDTH, 2 * HEIGHT) + 5);

        final BufferedImage screen = new Robot().createScreenCapture(new Rectangle(canvas.getLocationOnScreen(),
                                                                                   canvas.getSize()));
        driver.stop();
        game.join(TimeUnit.NANOSECONDS.toMillis(TIMEOUT));

        assertFalse(game.isAlive());
        assertEquals(3 * WIDTH, screen.getWidth());
        // FIT: a scale of 2, the frame of 64 by 48 pixels centered, black bars of 16 pixels on the sides.
        assertEquals(0xFF000000, screen.getRGB(WIDTH / 4, HEIGHT));
        assertEquals(0xFFFF0000, screen.getRGB(WIDTH / 2 + 2, HEIGHT));
        assertEquals(0xFF00FF00, screen.getRGB(WIDTH / 2 + WIDTH + 30, HEIGHT));
        assertEquals(0xFF000000, screen.getRGB(3 * WIDTH - 2, HEIGHT));
    }

    /**
     * Loop driver delivering the inbox and presenting one frame per iteration until stopped; thread-safe.
     */
    private static final class PresentingDriver implements LoopDriver
    {
        /** Inbox delivered at each iteration. */
        private final InboxSpy inbox;
        /** Presentation of a frame. */
        private final Runnable present;
        /** Number of frames presented. */
        private volatile int frames;
        /** {@code true} once a stop is requested. */
        private volatile boolean stopRequested;

        /**
         * Creates the driver.
         *
         * @param inbox The inbox delivered at each iteration.
         * @param present The presentation of a frame.
         */
        PresentingDriver(InboxSpy inbox, Runnable present)
        {
            this.inbox = inbox;
            this.present = present;
        }

        /**
         * Returns the number of frames presented.
         *
         * @return The number of frames.
         */
        int getFrames()
        {
            return frames;
        }

        @Override
        public void iterate()
        {
            inbox.deliverAll();
            present.run();
            frames = frames + 1;
        }

        @Override
        public boolean isRunning()
        {
            return !stopRequested;
        }

        @Override
        public void pause()
        {
            // Not used by this test.
        }

        @Override
        public void resume()
        {
            // Not used by this test.
        }

        @Override
        public void stop()
        {
            stopRequested = true;
        }

        @Override
        public void setTimeScale(double scale)
        {
            // Not used by this test.
        }
    }
}
