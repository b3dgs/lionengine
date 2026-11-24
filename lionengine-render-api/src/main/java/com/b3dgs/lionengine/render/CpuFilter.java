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
 * CPU implementation of an output filter, used by backends without a GPU such as {@code render-awt} (ADR-0025,
 * ADR-0033, REQ-FILTER-003).
 *
 * <p>
 * Buffers are packed {@code 0xAARRGGBB}, row-major, origin at the top-left, stride equal to the width. Thread: the
 * render thread. Must allocate nothing per call.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface CpuFilter
{
    /**
     * Filters the source buffer into the target buffer.
     *
     * @param source The source pixels, read only (never {@code null}).
     * @param sourceWidth The source width in pixels.
     * @param sourceHeight The source height in pixels.
     * @param target The target pixels, entirely written (never {@code null}).
     * @param targetWidth The target width in pixels.
     * @param targetHeight The target height in pixels.
     */
    void apply(int[] source, int sourceWidth, int sourceHeight, int[] target, int targetWidth, int targetHeight);
}
