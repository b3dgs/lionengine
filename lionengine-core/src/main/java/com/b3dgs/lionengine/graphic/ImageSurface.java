/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Surface;

/**
 * Represents the surface of an image.
 */
public interface ImageSurface extends Surface
{
    /**
     * Get the image surface.
     * 
     * @param <T> The surface type.
     * @return The image surface.
     */
    <T> T getSurface();

    /**
     * Get the image transparency type.
     * 
     * @return The image transparency type.
     */
    Transparency getTransparency();

    /**
     * Get the transparent color.
     * 
     * @return The transparent color used, <code>null</code> if no transparency.
     */
    ColorRgba getTransparentColor();
}
