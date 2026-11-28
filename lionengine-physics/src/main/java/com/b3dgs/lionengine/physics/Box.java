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
 * An axis-aligned box in world coordinates: a center, a full width and a full height, in meters (REQ-PHYS-001,
 * ADR-0013, ADR-0014, ADR-0021, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * A plain mutable value, reused by its owner so that collision paths allocate nothing: the center
 * ({@link #getX()}, {@link #getY()}) in meters, X right and Y up, and the full size ({@link #getWidth()},
 * {@link #getHeight()}) in meters, positive or zero. The box covers the open area from {@link #getMinX()} to
 * {@link #getMaxX()} and from {@link #getMinY()} to {@link #getMaxY()}, its bounds excluded: two boxes <em>overlap</em>
 * when, on each axis, each lower bound is strictly under the other upper bound
 * ({@link #overlaps(double, double, double, double)}), so boxes that only touch by an edge or a corner do not overlap.
 * The test is exact {@code double} comparisons of the bounds, the same on every platform.
 * </p>
 *
 * <p>
 * Invariants checked by {@link #set(double, double, double, double)}: every value is finite and the size is positive
 * or zero, so the bounds are finite and ordered. A new box is at {@code (0, 0)} with a size of {@code 0}.
 * </p>
 *
 * <p>
 * Not thread-safe; a box is owned by one caller at a time. No ECS, tile map or platform type.
 * </p>
 *
 * @since 11.0.0
 */
public final class Box
{
    /** Half of a size. */
    private static final double HALF = 0.5;
    /** Message of a {@code null} box. */
    private static final String NULL_BOX = "Box is null";
    /** Separator of two values in a message. */
    private static final String SEPARATOR = ", ";

    /** Center horizontal coordinate, in meters. */
    private double x;
    /** Center vertical coordinate, in meters. */
    private double y;
    /** Full width, in meters. */
    private double width;
    /** Full height, in meters. */
    private double height;

    /**
     * Creates a box at {@code (0, 0)} with a size of {@code 0}.
     */
    public Box()
    {
        super();
    }

    /**
     * Creates a box.
     *
     * @param x The center horizontal coordinate, in meters, finite.
     * @param y The center vertical coordinate, in meters, finite.
     * @param width The full width, in meters, positive or zero, finite.
     * @param height The full height, in meters, positive or zero, finite.
     * @throws IllegalArgumentException If a value is not finite or a size is negative.
     */
    public Box(double x, double y, double width, double height)
    {
        super();
        set(x, y, width, height);
    }

    /**
     * Tells whether two open intervals overlap: whether they share a length greater than zero.
     *
     * <p>
     * The overlap test of one axis, {@code minA < maxB && minB < maxA}: intervals that only touch at a bound do not
     * overlap. Allocates nothing.
     * </p>
     *
     * @param minA The lower bound of the first interval.
     * @param maxA The upper bound of the first interval.
     * @param minB The lower bound of the second interval.
     * @param maxB The upper bound of the second interval.
     * @return {@code true} if the intervals overlap.
     */
    public static boolean overlaps(double minA, double maxA, double minB, double maxB)
    {
        return minA < maxB && minB < maxA;
    }

    /**
     * Sets the box.
     *
     * @param x The center horizontal coordinate, in meters, finite.
     * @param y The center vertical coordinate, in meters, finite.
     * @param width The full width, in meters, positive or zero, finite.
     * @param height The full height, in meters, positive or zero, finite.
     * @throws IllegalArgumentException If a value is not finite or a size is negative; the box is left unchanged.
     */
    public void set(double x, double y, double width, double height)
    {
        if (!Double.isFinite(x) || !Double.isFinite(y))
        {
            throw new IllegalArgumentException("Box center is not finite: " + x + SEPARATOR + y);
        }
        if (!isSize(width) || !isSize(height))
        {
            throw new IllegalArgumentException("Box size is negative or not finite: " + width + SEPARATOR + height);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Copies another box.
     *
     * @param other The box copied.
     * @throws IllegalArgumentException If the box is {@code null}.
     */
    public void set(Box other)
    {
        if (other == null)
        {
            throw new IllegalArgumentException(NULL_BOX);
        }
        x = other.x;
        y = other.y;
        width = other.width;
        height = other.height;
    }

    /**
     * Tells whether this box overlaps another one: whether their interiors share an area (edges and corners that only
     * touch do not count).
     *
     * @param other The other box.
     * @return {@code true} if both boxes overlap on both axes.
     * @throws IllegalArgumentException If the box is {@code null}.
     */
    public boolean overlaps(Box other)
    {
        if (other == null)
        {
            throw new IllegalArgumentException(NULL_BOX);
        }
        return overlaps(getMinX(), getMaxX(), other.getMinX(), other.getMaxX())
               && overlaps(getMinY(), getMaxY(), other.getMinY(), other.getMaxY());
    }

    /**
     * Returns the center horizontal coordinate.
     *
     * @return The center X, in meters.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Returns the center vertical coordinate.
     *
     * @return The center Y, in meters, Y up.
     */
    public double getY()
    {
        return y;
    }

    /**
     * Returns the full width.
     *
     * @return The width, in meters, positive or zero.
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * Returns the full height.
     *
     * @return The height, in meters, positive or zero.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Returns the left bound.
     *
     * @return {@code x - width / 2}, in meters.
     */
    public double getMinX()
    {
        return x - width * HALF;
    }

    /**
     * Returns the right bound.
     *
     * @return {@code x + width / 2}, in meters.
     */
    public double getMaxX()
    {
        return x + width * HALF;
    }

    /**
     * Returns the bottom bound.
     *
     * @return {@code y - height / 2}, in meters.
     */
    public double getMinY()
    {
        return y - height * HALF;
    }

    /**
     * Returns the top bound.
     *
     * @return {@code y + height / 2}, in meters.
     */
    public double getMaxY()
    {
        return y + height * HALF;
    }

    /**
     * Describes the box.
     *
     * @return The center and the size.
     */
    @Override
    public String toString()
    {
        return "Box[x=" + x + SEPARATOR + "y=" + y + SEPARATOR + "width=" + width + SEPARATOR + "height=" + height
               + "]";
    }

    /**
     * Tells whether a value is a valid size.
     *
     * @param size The size.
     * @return {@code true} if positive or zero and finite.
     */
    private static boolean isSize(double size)
    {
        return size >= 0.0 && size < Double.POSITIVE_INFINITY;
    }
}
