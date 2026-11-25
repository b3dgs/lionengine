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
 * Render backend: presents the render commands of a frame on the logical frame, then the logical frame on the output
 * surface (ADR-0006, ADR-0026, ADR-0033, ADR-0040, ADR-0046, ADR-0047, REQ-SPRITE-010, REQ-FILTER-002).
 *
 * <p>
 * The application creates the backend for its platform ({@code render-awt} for {@code platform-awt}), attaches it to
 * the platform before starting it, and gives its {@link TextureStore} to the modules that create textures. The
 * platform asks the backend which output filters it implements and reports the others as skipped in its effective
 * result. In the render callback of the loop, the game clears a {@link CommandBuffer}, writes the frame into it, then
 * calls {@link #present(CommandBuffer)}.
 * </p>
 *
 * <p>
 * Implementations: {@code render-awt}, {@code render-opengl}, and the recording backend of {@code test-support}.
 * Thread: the runtime thread, which is the render thread of the platform.
 * </p>
 *
 * @since 11.0.0
 */
public interface RenderBackend
{
    /**
     * Returns the textures of this backend.
     *
     * @return The texture store (never {@code null}).
     */
    TextureStore getTextureStore();

    /**
     * Tells whether this backend implements an output filter. {@link OutputFilter#NONE} is always implemented.
     *
     * @param filter The filter (never {@code null}).
     * @return {@code true} if the backend applies the filter.
     * @throws IllegalArgumentException If {@code filter} is {@code null}.
     */
    boolean isSupported(OutputFilter filter);

    /**
     * Presents a frame: draws the entries of the buffer on a cleared logical frame by ascending layer, then submission
     * order, each clipped to its viewport and offset by it, then scales the logical frame to the output surface with
     * the presentation geometry and the filters of the platform. The buffer is read, never modified.
     *
     * @param commands The render commands of the frame (never {@code null}).
     * @throws IllegalArgumentException If {@code commands} is {@code null}.
     */
    void present(CommandBuffer commands);
}
