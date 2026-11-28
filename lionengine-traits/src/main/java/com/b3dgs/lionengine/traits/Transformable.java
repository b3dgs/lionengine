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

import com.b3dgs.lionengine.ecs.TraitType;

/**
 * Standard trait: where an object is and how large it is, in world units (REQ-STD-001, ADR-0011, ADR-0013, ADR-0014,
 * ADR-0030, ADR-0034, ADR-0080).
 *
 * <p>
 * Data only, no logic. Fields, all {@code double} in meters (ADR-0014):
 * </p>
 * <ul>
 * <li>the position of the object <b>center</b> ({@link #getX()}, {@link #getY()}), in world coordinates: X grows to the
 * right, Y grows <b>up</b> (ADR-0013, ADR-0034);</li>
 * <li>the size, the <b>full</b> width and height ({@link #getWidth()}, {@link #getHeight()}), so the object covers
 * {@code x - width / 2} to {@code x + width / 2};</li>
 * <li>the previous position ({@link #getPreviousX()}, {@link #getPreviousY()}): the position at the start of the
 * current step, written by {@link TransformCaptureSystem} before any other system runs. Rendering draws
 * {@code previous + (position - previous) * alpha}, with the interpolation factor {@code alpha} of the loop, from
 * {@code 0} to {@code 1} (ADR-0017, ADR-0030, decision 8); such a presentation value never feeds the simulation.</li>
 * </ul>
 *
 * <p>
 * The values are not checked: the game keeps them finite and the size positive or zero. A new or reset instance has
 * every field at {@code 0}. A game that places an object at its creation, or teleports it, also sets the previous
 * position to the new position, so that the frames rendered before the next capture do not slide the object from its
 * old place.
 * </p>
 *
 * <p>
 * Interactions: {@link MovementSystem} (slot {@value MovementSystem#SLOT}) moves the position of the objects that also
 * have a {@link Movable}; game systems may change any field. Registered in a world with {@link #TYPE}; instances are
 * pooled by the world and must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 64 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Transformable
{
    /**
     * The trait type to register in a world: creates instances at {@code 0} and resets every field to {@code 0} on
     * removal. Shared by every world, which each register it once.
     */
    public static final TraitType<Transformable> TYPE = new TraitType<>()
    {
        @Override
        public Transformable create()
        {
            return new Transformable();
        }

        @Override
        public void reset(Transformable trait)
        {
            trait.x = 0.0;
            trait.y = 0.0;
            trait.width = 0.0;
            trait.height = 0.0;
            trait.previousX = 0.0;
            trait.previousY = 0.0;
        }
    };

    /** Center horizontal position, in meters. */
    private double x;
    /** Center vertical position, in meters, Y up. */
    private double y;
    /** Full width, in meters. */
    private double width;
    /** Full height, in meters. */
    private double height;
    /** Center horizontal position at the start of the step, in meters. */
    private double previousX;
    /** Center vertical position at the start of the step, in meters. */
    private double previousY;

    /**
     * Creates a transformable in its reset state, every field at {@code 0}.
     */
    public Transformable()
    {
        super();
    }

    /**
     * Returns the horizontal position of the center.
     *
     * @return The horizontal position, in meters.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Returns the vertical position of the center.
     *
     * @return The vertical position, in meters, Y up.
     */
    public double getY()
    {
        return y;
    }

    /**
     * Sets the position of the center; the previous position is unchanged.
     *
     * @param x The horizontal position, in meters.
     * @param y The vertical position, in meters, Y up.
     */
    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the full width.
     *
     * @return The width, in meters.
     */
    public double getWidth()
    {
        return width;
    }

    /**
     * Returns the full height.
     *
     * @return The height, in meters.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Sets the full size.
     *
     * @param width The width, in meters, positive or zero.
     * @param height The height, in meters, positive or zero.
     */
    public void setSize(double width, double height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the horizontal position of the center at the start of the step.
     *
     * @return The previous horizontal position, in meters.
     */
    public double getPreviousX()
    {
        return previousX;
    }

    /**
     * Returns the vertical position of the center at the start of the step.
     *
     * @return The previous vertical position, in meters, Y up.
     */
    public double getPreviousY()
    {
        return previousY;
    }

    /**
     * Sets the previous position, overwritten by {@link TransformCaptureSystem} at the start of the next step: used
     * when an object is placed or teleported, together with {@link #setPosition(double, double)}.
     *
     * @param previousX The previous horizontal position, in meters.
     * @param previousY The previous vertical position, in meters, Y up.
     */
    public void setPreviousPosition(double previousX, double previousY)
    {
        this.previousX = previousX;
        this.previousY = previousY;
    }

    /**
     * Copies the position into the previous position.
     */
    void capture()
    {
        previousX = x;
        previousY = y;
    }

    /**
     * Moves the position by a displacement.
     *
     * @param dx The horizontal displacement, in meters.
     * @param dy The vertical displacement, in meters.
     */
    void translate(double dx, double dy)
    {
        x += dx;
        y += dy;
    }
}
