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
package com.b3dgs.lionengine.render;

/**
 * Result of the scaling function: where the logical frame is presented on the output surface, and the policy actually
 * applied (ADR-0006, ADR-0043, ADR-0046, REQ-DISPLAY-004, REQ-DISPLAY-007).
 *
 * <p>
 * {@link #of(ScalingPolicy, int, int, int, int)} is the one scaling function of the engine: platforms compute their
 * presentation geometry with it, so every platform and backend gets the same values. Sizes and positions are in device
 * pixels of the surface, origin at the top-left; the scales convert logical-frame pixels to device pixels.
 * </p>
 *
 * <ul>
 * <li>{@link ScalingPolicy#FIT}: scale {@code min(surface width / logical width, surface height / logical height)} on
 * both axes; the presented size is the logical size times the scale, rounded to the nearest pixel and never larger
 * than the surface; centered.</li>
 * <li>{@link ScalingPolicy#INTEGER}: the largest integer scale at which the logical frame fits, centered; when the
 * surface is smaller than the logical frame on an axis, {@code FIT} is applied instead and reported as the policy.</li>
 * <li>{@link ScalingPolicy#STRETCH}: the whole surface, with the scale of each axis.</li>
 * </ul>
 *
 * <p>
 * Centering puts the extra pixel of an odd margin on the right or bottom. An empty surface (a minimized window) gives
 * an empty rectangle at {@code (0, 0)} and scales of {@code 1}. Immutable and thread-safe.
 * </p>
 *
 * @param policy The policy applied: the requested one, or {@code FIT} after an {@code INTEGER} fallback.
 * @param x The left edge of the presented frame, in device pixels.
 * @param y The top edge of the presented frame, in device pixels.
 * @param width The width of the presented frame, in device pixels (not negative).
 * @param height The height of the presented frame, in device pixels (not negative).
 * @param scaleX The horizontal scale from logical-frame pixels to device pixels (positive).
 * @param scaleY The vertical scale from logical-frame pixels to device pixels (positive).
 * @since 11.0.0
 */
public record FrameScaling(ScalingPolicy policy, int x, int y, int width, int height, double scaleX, double scaleY)
{
    /** Scale of an empty surface. */
    private static final double EMPTY_SCALE = 1.0;
    /** Error message of a missing policy. */
    private static final String ERROR_POLICY = "Scaling policy must not be null";

    /**
     * Scales the logical frame to the output surface.
     *
     * @param policy The requested scaling policy (never {@code null}).
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @param surfaceWidth The surface width in device pixels (not negative).
     * @param surfaceHeight The surface height in device pixels (not negative).
     * @return The presented rectangle, the scales and the policy applied.
     * @throws IllegalArgumentException If {@code policy} is {@code null}, a logical size is not positive or a surface
     *             size is negative.
     */
    public static FrameScaling of(ScalingPolicy policy,
                                  int logicalWidth,
                                  int logicalHeight,
                                  int surfaceWidth,
                                  int surfaceHeight)
    {
        checkArguments(policy, logicalWidth, logicalHeight, surfaceWidth, surfaceHeight);
        final int factor = Math.min(surfaceWidth / logicalWidth, surfaceHeight / logicalHeight);
        final FrameScaling scaling;
        if (surfaceWidth == 0 || surfaceHeight == 0)
        {
            scaling = empty(policy);
        }
        else if (policy == ScalingPolicy.STRETCH)
        {
            scaling = new FrameScaling(policy,
                                       0,
                                       0,
                                       surfaceWidth,
                                       surfaceHeight,
                                       surfaceWidth / (double) logicalWidth,
                                       surfaceHeight / (double) logicalHeight);
        }
        else if (policy == ScalingPolicy.INTEGER && factor >= 1)
        {
            scaling = centered(policy, logicalWidth * factor, logicalHeight * factor, surfaceWidth, surfaceHeight)
                      .withScale(factor);
        }
        else
        {
            scaling = fit(logicalWidth, logicalHeight, surfaceWidth, surfaceHeight);
        }
        return scaling;
    }

    /**
     * Checks the arguments of the scaling function.
     *
     * @param policy The requested scaling policy.
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param surfaceWidth The surface width in device pixels.
     * @param surfaceHeight The surface height in device pixels.
     * @throws IllegalArgumentException If {@code policy} is {@code null}, a logical size is not positive or a surface
     *             size is negative.
     */
    private static void checkArguments(ScalingPolicy policy,
                                       int logicalWidth,
                                       int logicalHeight,
                                       int surfaceWidth,
                                       int surfaceHeight)
    {
        if (policy == null)
        {
            throw new IllegalArgumentException(ERROR_POLICY);
        }
        if (logicalWidth <= 0 || logicalHeight <= 0 || surfaceWidth < 0 || surfaceHeight < 0)
        {
            throw new IllegalArgumentException("Invalid scaling of "
                                               + logicalWidth
                                               + 'x'
                                               + logicalHeight
                                               + " to "
                                               + surfaceWidth
                                               + 'x'
                                               + surfaceHeight);
        }
    }

    /**
     * Scales with {@link ScalingPolicy#FIT}.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param surfaceWidth The surface width in device pixels, positive.
     * @param surfaceHeight The surface height in device pixels, positive.
     * @return The centered rectangle at the largest aspect-preserving scale.
     */
    private static FrameScaling fit(int logicalWidth, int logicalHeight, int surfaceWidth, int surfaceHeight)
    {
        final double scale = Math.min(surfaceWidth / (double) logicalWidth, surfaceHeight / (double) logicalHeight);
        final int width = (int) Math.min(surfaceWidth, Math.round(logicalWidth * scale));
        final int height = (int) Math.min(surfaceHeight, Math.round(logicalHeight * scale));
        return centered(ScalingPolicy.FIT, width, height, surfaceWidth, surfaceHeight).withScale(scale);
    }

    /**
     * Returns the scaling of an empty surface, smaller than the logical frame, so {@code INTEGER} falls back.
     *
     * @param policy The requested policy.
     * @return The empty scaling.
     */
    private static FrameScaling empty(ScalingPolicy policy)
    {
        ScalingPolicy applied = policy;
        if (policy == ScalingPolicy.INTEGER)
        {
            applied = ScalingPolicy.FIT;
        }
        return new FrameScaling(applied, 0, 0, 0, 0, EMPTY_SCALE, EMPTY_SCALE);
    }

    /**
     * Centers a presented size on the surface, with a scale of {@code 1} until {@link #withScale(double)}.
     *
     * @param policy The policy applied.
     * @param width The presented width in device pixels.
     * @param height The presented height in device pixels.
     * @param surfaceWidth The surface width in device pixels.
     * @param surfaceHeight The surface height in device pixels.
     * @return The centered rectangle.
     */
    private static FrameScaling centered(ScalingPolicy policy,
                                         int width,
                                         int height,
                                         int surfaceWidth,
                                         int surfaceHeight)
    {
        return new FrameScaling(policy,
                                (surfaceWidth - width) / 2,
                                (surfaceHeight - height) / 2,
                                width,
                                height,
                                EMPTY_SCALE,
                                EMPTY_SCALE);
    }

    /**
     * Creates a scaling.
     *
     * @param policy The policy applied (never {@code null}).
     * @param x The left edge of the presented frame, in device pixels.
     * @param y The top edge of the presented frame, in device pixels.
     * @param width The width of the presented frame, in device pixels (not negative).
     * @param height The height of the presented frame, in device pixels (not negative).
     * @param scaleX The horizontal scale (positive and finite).
     * @param scaleY The vertical scale (positive and finite).
     * @throws IllegalArgumentException If {@code policy} is {@code null}, a size is negative or a scale is not positive
     *             and finite.
     */
    public FrameScaling
    {
        if (policy == null)
        {
            throw new IllegalArgumentException(ERROR_POLICY);
        }
        if (width < 0 || height < 0 || !isValidScale(scaleX) || !isValidScale(scaleY))
        {
            throw new IllegalArgumentException("Invalid frame scaling");
        }
    }

    /**
     * Tells whether a scale is valid.
     *
     * @param scale The scale.
     * @return {@code true} if the scale is positive and finite.
     */
    private static boolean isValidScale(double scale)
    {
        return scale > 0.0 && !Double.isInfinite(scale);
    }

    /**
     * Returns this rectangle with the same scale on both axes.
     *
     * @param scale The scale (positive and finite).
     * @return The scaling with that scale.
     */
    private FrameScaling withScale(double scale)
    {
        return new FrameScaling(policy, x, y, width, height, scale, scale);
    }
}
