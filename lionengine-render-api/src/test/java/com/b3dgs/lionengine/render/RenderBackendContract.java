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
package com.b3dgs.lionengine.render;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Contract suite of {@link RenderBackend} and its {@link TextureStore} (ADR-0026, ADR-0045, ADR-0047, REQ-SPRITE-010,
 * REQ-FILTER-002).
 *
 * <p>
 * Published in the {@code render-api} test JAR: every backend binds it by extending this class and creating a backend
 * ready to present frames of {@link #WIDTH} by {@link #HEIGHT} pixels. What a backend draws is checked by its own
 * tests; this suite checks what every backend shares.
 * </p>
 */
public abstract class RenderBackendContract
{
    /** Logical frame width of the suite. */
    protected static final int WIDTH = 320;
    /** Logical frame height of the suite. */
    protected static final int HEIGHT = 240;

    /**
     * Creates a backend ready to present frames of {@link #WIDTH} by {@link #HEIGHT} pixels.
     *
     * @return The backend.
     */
    protected abstract RenderBackend createBackend();

    /**
     * Creates the pixels of a texture.
     *
     * @param width The width in pixels.
     * @param height The height in pixels.
     * @return Opaque pixels, each a different color.
     */
    private static int[] pixels(int width, int height)
    {
        final int[] argb = new int[width * height];
        for (int i = 0; i < argb.length; i++)
        {
            argb[i] = 0xFF000000 | i;
        }
        return argb;
    }

    /**
     * Created textures have distinct handles; they can be prepared, and released once or more.
     */
    @Test
    void testTextures()
    {
        final TextureStore store = createBackend().getTextureStore();
        assertNotNull(store);

        final int first = store.create(2, 3, pixels(2, 3));
        final int second = store.create(1, 1, pixels(1, 1));
        assertNotEquals(first, second);

        store.prepare(first);
        store.release(first);
        store.release(first);
        assertThrows(IllegalArgumentException.class, () -> store.prepare(first));
        store.prepare(second);
    }

    /**
     * Invalid texture data and unknown handles are rejected; releasing an unknown handle has no effect.
     */
    @Test
    void testInvalidTextures()
    {
        final TextureStore store = createBackend().getTextureStore();

        assertThrows(IllegalArgumentException.class, () -> store.create(0, 1, pixels(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> store.create(1, 0, pixels(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> store.create(1, 1, null));
        assertThrows(IllegalArgumentException.class, () -> store.create(2, 2, pixels(1, 3)));
        assertThrows(IllegalArgumentException.class, () -> store.prepare(Integer.MIN_VALUE));
        assertDoesNotThrow(() -> store.release(Integer.MIN_VALUE));
    }

    /**
     * {@code NONE} is always implemented; a {@code null} filter is rejected.
     */
    @Test
    void testFilters()
    {
        final RenderBackend backend = createBackend();

        assertTrue(backend.isSupported(OutputFilter.NONE));
        assertThrows(IllegalArgumentException.class, () -> backend.isSupported(null));
    }

    /**
     * Frames are presented without modifying the buffer, empty or not, with viewports and every field set.
     */
    @Test
    void testPresent()
    {
        final RenderBackend backend = createBackend();
        final int texture = backend.getTextureStore().create(4, 4, pixels(4, 4));
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, 4);

        backend.present(commands);

        commands.add(texture, 0, 0, 4, 4).position(10.0F, 20.0F).layer(1);
        commands.setViewport(new Viewport(-8, -8, WIDTH, HEIGHT));
        commands.add(texture, 1, 1, 2, 2)
                .position(WIDTH, HEIGHT)
                .scale(-2.0F, 3.0F)
                .rotation(45.0F)
                .pivot(1.0F, 1.0F)
                .tint(0x8000FF00)
                .alpha(0.25F);
        backend.present(commands);
        backend.present(commands);

        assertEquals(2, commands.size());
        assertEquals(1, commands.getEntry(0));
        assertEquals(10.0F, commands.getX(0));
        assertThrows(IllegalArgumentException.class, () -> backend.present(null));
    }
}
