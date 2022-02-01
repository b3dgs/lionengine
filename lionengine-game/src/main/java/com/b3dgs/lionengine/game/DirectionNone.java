/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

/**
 * No direction.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class DirectionNone implements Direction
{
    /** No direction instance. */
    public static final Direction INSTANCE = new DirectionNone();

    /**
     * Create no direction.
     */
    private DirectionNone()
    {
        super();
    }

    /*
     * Direction
     */

    @Override
    public double getDirectionVertical()
    {
        return 0;
    }

    @Override
    public double getDirectionHorizontal()
    {
        return 0;
    }
}
