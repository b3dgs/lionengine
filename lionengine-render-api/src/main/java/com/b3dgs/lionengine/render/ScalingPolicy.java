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
 * How the logical frame is scaled to the output surface (ADR-0006, REQ-DISPLAY-004).
 *
 * <p>
 * The logical frame size never changes, and scaling never changes world units or simulation state. The platform
 * reports the effective policy and the resulting presentation geometry (ADR-0046).
 * </p>
 *
 * @since 11.0.0
 */
public enum ScalingPolicy
{
    /** Largest aspect-preserving scale that fits the surface, centered. The default. */
    FIT,
    /**
     * Largest integer scale that fits the surface, centered; falls back to {@link #FIT} when the surface is smaller
     * than the logical frame, and the fallback is reported.
     */
    INTEGER,
    /** Fills the whole surface; the aspect ratio is not preserved. */
    STRETCH
}
