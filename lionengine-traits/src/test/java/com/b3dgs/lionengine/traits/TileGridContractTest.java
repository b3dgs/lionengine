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
package com.b3dgs.lionengine.traits;

import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.ShapeQuery;
import com.b3dgs.lionengine.physics.ShapeQueryContract;

/**
 * Binds the {@link ShapeQuery} contract suite of the {@code physics} test JAR to the {@link TileGrid} stand-in of the
 * tile map (ADR-0045, ADR-0083).
 */
final class TileGridContractTest extends ShapeQueryContract
{
    /** Columns and rows of the suite. */
    private static final int SIZE = 8;

    @Override
    protected ShapeQuery create(Box... solids)
    {
        final TileGrid grid = new TileGrid(SIZE, SIZE);
        for (final Box solid : solids)
        {
            grid.set((int) Math.floor(solid.getX()), (int) Math.floor(solid.getY()), true);
        }
        return grid;
    }
}
