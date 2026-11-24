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

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Output filter supplied by a game, registered explicitly on the render backend at startup (ADR-0025, ADR-0033,
 * ADR-0047, REQ-FILTER-003).
 *
 * <p>
 * It provides a fragment shader, a CPU implementation, or both. A backend lacking the implementation it needs skips
 * the filter and reports the skip in the effective result; it never throws.
 * </p>
 *
 * <p>
 * Shader rules: GLSL ES 1.00 feature set, no {@code #version} line and no precision qualifier in the file (the
 * backend prepends {@code #version 100} or {@code #version 120}); standard uniforms are the source texture, the source
 * and output sizes in pixels, and the presentation time in seconds. The shader is a {@code .frag} resource opened by
 * the backend through {@code Resources}.
 * </p>
 *
 * @since 11.0.0
 */
public interface CustomFilter
{
    /**
     * Returns the identifier of the filter, used in the filter chain of the display request.
     *
     * @return The identifier, with a custom id (never {@code null}, never a standard filter).
     */
    OutputFilter getFilter();

    /**
     * Returns the fragment shader for GPU backends.
     *
     * @return The key of the fragment shader resource, {@code null} if the filter has no GPU implementation.
     */
    ResourceKey getFragmentShader();

    /**
     * Returns the implementation for CPU backends.
     *
     * @return The CPU implementation, {@code null} if the filter has none.
     */
    CpuFilter getCpuFilter();
}
