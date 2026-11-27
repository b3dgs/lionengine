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
 * Color effect of a render command entry that a backend may apply per frame (ADR-0026, ADR-0056, REQ-SPRITE-004).
 *
 * <p>
 * A backend tells which effects it applies with {@link ColorCapability#isSupported(ColorEffect)}; it draws the entries
 * of an effect it does not support as if the field kept its default. An effect the backend cannot apply per frame is
 * baked on the CPU instead, by the {@code graphics} module.
 * </p>
 *
 * @since 11.0.0
 */
public enum ColorEffect
{
    /**
     * The tint of an entry ({@link CommandBuffer#tint(int)}), multiplied with the colors and the opacity of the
     * texture; default {@link CommandBuffer#NO_TINT}.
     */
    TINT,
    /** The opacity of an entry ({@link CommandBuffer#alpha(float)}), multiplied with the texture opacity; default 1. */
    ALPHA
}
