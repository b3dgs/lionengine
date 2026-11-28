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

package com.b3dgs.lionengine.physics;

/**
 * A {@link ShapeQuery} over a fixed list of boxes, compared one by one: the reference of the contract, for the tests
 * of the collision systems until the tile map implements it (ADR-0045, ADR-0082).
 *
 * <p>
 * Public in the {@code physics} test JAR. Finds the boxes of the list overlapping the area by {@link Box#overlaps(Box)},
 * in list order; allocates nothing per query.
 * </p>
 */
public final class BoxListQuery implements ShapeQuery
{
    /** The solid boxes, copied. */
    private final Box[] solids;

    /**
     * Creates the query.
     *
     * @param solids The solid boxes, copied.
     */
    public BoxListQuery(Box... solids)
    {
        this.solids = new Box[solids.length];
        for (int i = 0; i < solids.length; i++)
        {
            this.solids[i] = new Box();
            this.solids[i].set(solids[i]);
        }
    }

    @Override
    public int query(Box area, Box[] results)
    {
        if (area == null || results == null)
        {
            throw new IllegalArgumentException("Area or results are null");
        }
        int count = 0;
        for (final Box solid : solids)
        {
            if (solid.overlaps(area))
            {
                if (count < results.length)
                {
                    results[count].set(solid);
                }
                count++;
            }
        }
        return count;
    }
}
