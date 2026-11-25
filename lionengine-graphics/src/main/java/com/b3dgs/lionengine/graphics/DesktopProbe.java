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
package com.b3dgs.lionengine.graphics;

import java.awt.Point;

/**
 * Probe of the Android job (features/build-skeleton.md, section Tests): a {@code java.desktop} use in a shared module,
 * which the Maven build accepts and the Android job must refuse. Removed once the failure is shown.
 */
final class DesktopProbe
{
    /**
     * Creates a point.
     *
     * @return The point.
     */
    static Point createPoint()
    {
        return new Point(1, 2);
    }

    /**
     * Private constructor.
     */
    private DesktopProbe()
    {
        super();
    }
}
