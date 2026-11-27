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

import java.awt.Canvas;

/**
 * Capability of the AWT platform giving the output surface to the AWT modules ({@code render-awt}, {@code input-awt}),
 * the only way an AWT type leaves {@code platform-awt} (ADR-0020, ADR-0022, ADR-0033, REQ-PLATFORM-001).
 *
 * <p>
 * Looked up with {@code platform.getCapability(AwtSurface.class)}. The canvas exists from the creation of the
 * platform; the platform shows it in its window at the start, and only then does it have a buffer strategy. Its size
 * is in AWT units: the platform reports the surface in device pixels (ADR-0043), and a renderer that draws in device
 * pixels resets the default transform of the graphics it gets from the canvas.
 * </p>
 *
 * <p>
 * Threads: renderers use the canvas on the runtime thread, input adapters register their listeners before the start;
 * AWT calls the listeners on its event thread.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface AwtSurface
{
    /**
     * Returns the canvas that shows the frames and receives the input events.
     *
     * @return The canvas (never {@code null}).
     */
    Canvas getCanvas();
}
