/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.geom;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;

/**
 * Geometry factory.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Geom
{
    /**
     * Get the intersection point of two lines.
     * 
     * @param l1 The first line (must not be <code>null</code>).
     * @param l2 The second line (must not be <code>null</code>).
     * @return The intersection point, <code>null</code> if none.
     * @throws LionEngineException If invalid arguments.
     */
    public static Optional<Coord> intersection(Line l1, Line l2)
    {
        Check.notNull(l1);
        Check.notNull(l2);

        final double x1 = l1.getX1();
        final double x2 = l1.getX2();
        final double y1 = l1.getY1();
        final double y2 = l1.getY2();

        final double x3 = l2.getX1();
        final double x4 = l2.getX2();
        final double y3 = l2.getY1();
        final double y4 = l2.getY2();

        final double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Double.compare(d, 0) == 0)
        {
            return Optional.empty();
        }

        final double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        final double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return Optional.of(new Coord(xi, yi));
    }

    /**
     * Create a localizable.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The localizable.
     */
    public static Localizable createLocalizable(double x, double y)
    {
        return new LocalizableImpl(x, y);
    }

    /**
     * Create an area.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The rectangle width.
     * @param height The rectangle height.
     * @return The created area.
     */
    public static Area createArea(double x, double y, double width, double height)
    {
        return new AreaImpl(x, y, width, height);
    }

    /**
     * Check if two localizable are at the same location.
     * 
     * @param a The first localizable.
     * @param b The second localizable.
     * @return <code>true</code> if same location, <code>false</code> else.
     */
    public static boolean same(Localizable a, Localizable b)
    {
        return Double.compare(a.getX(), b.getX()) == 0 && Double.compare(a.getY(), b.getY()) == 0;
    }

    /**
     * Private constructor.
     */
    private Geom()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
