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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.render.Viewport;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;

/**
 * Test of what {@link AwtRenderBackend} draws on the logical frame, headless (REQ-SPRITE-010).
 */
final class AwtRenderBackendTest
{
    /** Logical frame width. */
    private static final int WIDTH = 64;
    /** Logical frame height. */
    private static final int HEIGHT = 48;
    /** Opaque black. */
    private static final int BLACK = 0xFF000000;
    /** Opaque red. */
    private static final int RED = 0xFFFF0000;
    /** Opaque green. */
    private static final int GREEN = 0xFF00FF00;
    /** Opaque white. */
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * Creates pixels of one color.
     *
     * @param width The width.
     * @param height The height.
     * @param argb The color.
     * @return The pixels.
     */
    private static int[] fill(int width, int height, int argb)
    {
        final int[] pixels = new int[width * height];
        Arrays.fill(pixels, argb);
        return pixels;
    }

    /**
     * Creates pixels whose left half is red and right half green.
     *
     * @param width The width, even.
     * @param height The height.
     * @return The pixels.
     */
    private static int[] halves(int width, int height)
    {
        final int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++)
        {
            pixels[i] = i % width < width / 2 ? RED : GREEN;
        }
        return pixels;
    }

    /** Backend of the test. */
    private final AwtRenderBackend backend = new AwtRenderBackend(AwtRenderTests.platform(WIDTH, HEIGHT));
    /** Textures of the backend. */
    private final TextureStore textures = backend.getTextureStore();
    /** Commands of the test. */
    private final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 4);

    /**
     * Presents the commands and returns a pixel of the logical frame.
     *
     * @param x The horizontal position.
     * @param y The vertical position.
     * @return The pixel, opaque.
     */
    private int pixel(int x, int y)
    {
        return backend.getLogicalFrame().getRGB(x, y);
    }

    /**
     * Asserts that a pixel is near a color, each channel within one unit.
     *
     * @param expected The expected color.
     * @param x The horizontal position.
     * @param y The vertical position.
     */
    private void assertNear(int expected, int x, int y)
    {
        final int actual = pixel(x, y);
        for (int shift = 0; shift < 32; shift += 8)
        {
            final int difference = ((expected >>> shift) & 0xFF) - ((actual >>> shift) & 0xFF);
            assertTrue(Math.abs(difference) <= 1, Integer.toHexString(actual));
        }
    }

    /**
     * The platform must have an AWT surface; only {@code NONE} is supported; the buffer must not be {@code null}.
     */
    @Test
    void testArguments()
    {
        final HeadlessPlatform platform = new HeadlessPlatform(WIDTH,
                                                               HEIGHT,
                                                               AwtRenderTests.REQUEST,
                                                               AwtRenderTests.SINK);

        assertThrows(IllegalArgumentException.class, () -> new AwtRenderBackend(null));
        assertThrows(IllegalArgumentException.class, () -> new AwtRenderBackend(platform));
        assertTrue(backend.isSupported(OutputFilter.NONE));
        assertFalse(backend.isSupported(OutputFilter.BILINEAR));
        assertThrows(IllegalArgumentException.class, () -> backend.isSupported(null));
        assertThrows(IllegalArgumentException.class, () -> backend.present(null));
        assertEquals(WIDTH, backend.getLogicalFrame().getWidth());
        assertEquals(HEIGHT, backend.getLogicalFrame().getHeight());
    }

    /**
     * An entry draws its source rectangle at its position; each frame starts black.
     */
    @Test
    void testPosition()
    {
        final int texture = textures.create(4, 2, halves(4, 2));
        commands.add(texture, 1, 0, 2, 2).position(10.0F, 20.0F);

        backend.present(commands);

        assertEquals(BLACK, pixel(9, 20));
        assertEquals(RED, pixel(10, 20));
        assertEquals(GREEN, pixel(11, 21));
        assertEquals(BLACK, pixel(12, 20));
        assertEquals(BLACK, pixel(10, 22));

        commands.clear();
        backend.present(commands);

        assertEquals(BLACK, pixel(10, 20));
    }

    /**
     * Higher layers are drawn over lower ones, whatever the submission order.
     */
    @Test
    void testLayers()
    {
        commands.add(textures.create(2, 2, fill(2, 2, GREEN)), 0, 0, 2, 2).layer(1);
        commands.add(textures.create(2, 2, fill(2, 2, RED)), 0, 0, 2, 2);

        backend.present(commands);

        assertEquals(GREEN, pixel(0, 0));
    }

    /**
     * An entry is offset by its viewport and clipped to it.
     */
    @Test
    void testViewport()
    {
        final int texture = textures.create(16, 16, fill(16, 16, RED));
        commands.setViewport(new Viewport(8, 8, 4, 4));
        commands.add(texture, 0, 0, 16, 16);
        commands.resetViewport();
        commands.add(texture, 0, 0, 1, 1).position(30.0F, 30.0F);

        backend.present(commands);

        assertEquals(BLACK, pixel(7, 7));
        assertEquals(RED, pixel(8, 8));
        assertEquals(RED, pixel(11, 11));
        assertEquals(BLACK, pixel(12, 12));
        assertEquals(RED, pixel(30, 30));
    }

    /**
     * Scales stretch the entry from its pivot; a negative scale mirrors it; the pivot is drawn at the position.
     */
    @Test
    void testScaleMirrorAndPivot()
    {
        final int texture = textures.create(2, 1, halves(2, 1));
        commands.add(texture, 0, 0, 2, 1).position(4.0F, 4.0F).scale(3.0F, 2.0F);
        commands.add(texture, 0, 0, 2, 1).position(20.0F, 0.0F).scale(-1.0F, 1.0F);
        commands.add(texture, 0, 0, 2, 1).position(30.0F, 30.0F).pivot(1.0F, 1.0F);

        backend.present(commands);

        assertEquals(RED, pixel(4, 4));
        assertEquals(RED, pixel(6, 5));
        assertEquals(GREEN, pixel(7, 5));
        assertEquals(GREEN, pixel(9, 5));
        assertEquals(BLACK, pixel(10, 4));
        assertEquals(BLACK, pixel(4, 6));
        assertEquals(GREEN, pixel(18, 0));
        assertEquals(RED, pixel(19, 0));
        assertEquals(BLACK, pixel(20, 0));
        assertEquals(RED, pixel(29, 29));
        assertEquals(GREEN, pixel(30, 29));
        assertEquals(BLACK, pixel(30, 30));
    }

    /**
     * A rotation of 90 degrees turns the entry counter-clockwise on screen around its pivot.
     */
    @Test
    void testRotation()
    {
        commands.add(textures.create(4, 2, halves(4, 2)), 0, 0, 4, 2).position(20.0F, 20.0F).rotation(90.0F);

        backend.present(commands);

        assertEquals(RED, pixel(20, 19));
        assertEquals(RED, pixel(21, 18));
        assertEquals(GREEN, pixel(20, 17));
        assertEquals(GREEN, pixel(21, 16));
        assertEquals(BLACK, pixel(20, 20));
        assertEquals(BLACK, pixel(22, 18));
    }

    /**
     * The tint multiplies the colors and the opacity; the alpha multiplies the opacity.
     */
    @Test
    void testTintAndAlpha()
    {
        final int texture = textures.create(2, 2, fill(2, 2, WHITE));
        commands.add(texture, 0, 0, 1, 1).tint(GREEN);
        commands.add(texture, 0, 0, 1, 1).position(1.0F, 0.0F).alpha(0.5F);
        commands.add(texture, 0, 0, 1, 1).position(2.0F, 0.0F).tint(0x80FFFFFF);
        commands.add(texture, 0, 0, 1, 1).position(3.0F, 0.0F).tint(0x80FF0000).alpha(0.5F);
        commands.add(texture, 0, 0, 1, 1).position(4.0F, 0.0F).tint(0xFF0000FF);

        backend.present(commands);

        assertEquals(GREEN, pixel(0, 0));
        assertNear(0xFF808080, 1, 0);
        assertNear(0xFF808080, 2, 0);
        assertNear(0xFF400000, 3, 0);
        assertEquals(0xFF0000FF, pixel(4, 0));
    }

    /**
     * A tinted source rectangle partly outside its texture draws its inside part at the right place; outside it, it
     * draws nothing.
     */
    @Test
    void testTintedOutsideTexture()
    {
        final int texture = textures.create(2, 2, fill(2, 2, WHITE));
        commands.add(texture, -1, -1, 4, 4).position(10.0F, 10.0F).tint(RED);
        commands.add(texture, 5, 5, 2, 2).position(20.0F, 20.0F).tint(RED);

        backend.present(commands);

        assertEquals(BLACK, pixel(10, 10));
        assertEquals(RED, pixel(11, 11));
        assertEquals(RED, pixel(12, 12));
        assertEquals(BLACK, pixel(13, 13));
        assertEquals(BLACK, pixel(20, 20));
    }

    /**
     * Textures are uploaded once, at prepare or first use; a released texture draws nothing; handles are never
     * reused, beyond the initial slots.
     */
    @Test
    void testTextures()
    {
        final AwtTextures store = (AwtTextures) textures;
        final int[] pixels = fill(1, 1, RED);
        final int first = store.create(1, 1, pixels);
        store.prepare(first);
        assertSame(store.getImage(first), store.getImage(first));

        int last = first;
        for (int i = 0; i < 40; i++)
        {
            final int handle = store.create(1, 1, pixels);
            assertTrue(handle > last);
            last = handle;
        }
        assertTrue(store.isAlive(last));
        assertNotSame(store.getImage(first), store.getImage(last));
        assertFalse(store.isAlive(0));
        assertFalse(store.isAlive(last + 1));

        commands.add(first, 0, 0, 1, 1);
        store.release(first);
        backend.present(commands);

        assertFalse(store.isAlive(first));
        assertEquals(BLACK, pixel(0, 0));
    }
}
