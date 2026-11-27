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
package com.b3dgs.lionengine.platform.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.platform.PlatformEvent;

/**
 * Test of {@link AwtWindowEvents}: AWT events converted into platform events, headless.
 */
final class AwtWindowEventsTest
{
    /** Canvas width in AWT units. */
    private static final int WIDTH = 100;
    /** Canvas height in AWT units. */
    private static final int HEIGHT = 50;

    /** Received kinds. */
    private final List<PlatformEvent.Kind> kinds = new ArrayList<>();
    /** Received sizes: width, then height, per event. */
    private final List<Integer> sizes = new ArrayList<>();

    /**
     * Creates the converter of a canvas of {@link #WIDTH} by {@link #HEIGHT}.
     *
     * @return The converter.
     */
    private AwtWindowEvents create()
    {
        final Canvas canvas = new Canvas();
        canvas.setSize(WIDTH, HEIGHT);
        return new AwtWindowEvents(canvas, (kind, width, height) ->
        {
            kinds.add(kind);
            sizes.add(Integer.valueOf(width));
            sizes.add(Integer.valueOf(height));
        });
    }

    /**
     * Each window and canvas event gives its platform event with the surface size; moves, shows and hides give none.
     */
    @Test
    void testEvents()
    {
        final AwtWindowEvents events = create();

        events.windowClosing(null);
        events.windowIconified(null);
        events.windowDeiconified(null);
        events.windowGainedFocus(null);
        events.windowLostFocus(null);
        events.componentResized(null);
        events.componentMoved(null);
        events.componentShown(null);
        events.componentHidden(null);

        assertEquals(List.of(PlatformEvent.Kind.CLOSE_REQUESTED,
                             PlatformEvent.Kind.BACKGROUND,
                             PlatformEvent.Kind.FOREGROUND,
                             PlatformEvent.Kind.FOCUS_GAINED,
                             PlatformEvent.Kind.FOCUS_LOST,
                             PlatformEvent.Kind.SURFACE_RESIZED),
                     kinds);
        for (int i = 0; i < sizes.size(); i += 2)
        {
            assertEquals(WIDTH, sizes.get(i).intValue());
            assertEquals(HEIGHT, sizes.get(i + 1).intValue());
        }
    }

    /**
     * Sizes are converted to device pixels with the scale, rounded; an undisplayed canvas has a scale of 1.
     */
    @Test
    void testDevicePixels()
    {
        final Canvas canvas = new Canvas();

        assertEquals(1.0, AwtWindowEvents.getScaleX(canvas));
        assertEquals(1.0, AwtWindowEvents.getScaleY(canvas));
        assertEquals(WIDTH, AwtWindowEvents.toDevice(WIDTH, 1.0));
        assertEquals(2 * WIDTH, AwtWindowEvents.toDevice(WIDTH, 2.0));
        assertEquals(125, AwtWindowEvents.toDevice(WIDTH, 1.25));
        assertEquals(17, AwtWindowEvents.toDevice(11, 1.5));
        assertEquals(WIDTH, AwtWindow.toUnits(2 * WIDTH, 2.0));
        assertEquals(213, AwtWindow.toUnits(320, 1.5));
    }
}
