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
 * The shape-query contract: finds the solid shapes of a static collision provider that overlap a box (REQ-PHYS-001,
 * ADR-0021, ADR-0028, ADR-0082).
 *
 * <p>
 * Implemented by a provider of static shapes, first the tile map of {@code tilemap} (its solid tile boxes, placed in
 * the world by its origin and tile size in meters, ADR-0028), and called by a collision system of {@code traits}; the
 * two meet through this interface only, so {@code tilemap} never depends on {@code ecs} and {@code traits} never on
 * {@code tilemap} (ADR-0021). The implementation and its contract tests come with the tile-collision batch.
 * </p>
 *
 * <p>
 * Hot path: called for each colliding object at each step, so it allocates nothing. The caller provides the result
 * boxes and keeps them; the query writes into them and keeps no reference to them or to the area.
 * </p>
 *
 * <p>
 * Called on the runtime thread only, in the update phase. One abstract method, so a lambda is a query.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface ShapeQuery
{
    /**
     * Finds the solid shapes overlapping an area, by the test of {@link Box#overlaps(Box)} (a shape that only touches
     * the area by an edge or a corner is not found).
     *
     * <p>
     * The first {@code min(count, results.length)} boxes of {@code results} receive the found shapes, in an order that
     * depends only on the content of the provider and the area (row by row for a tile map); the others are left
     * unchanged. The count is returned even when it exceeds the buffer, so the caller can grow the buffer and query
     * again.
     * </p>
     *
     * @param area The queried area, in world meters, read only and not kept.
     * @param results The boxes receiving the found shapes, none {@code null} up to the written count, not kept.
     * @return The number of solid shapes overlapping the area, possibly greater than {@code results.length}.
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    int query(Box area, Box[] results);
}
