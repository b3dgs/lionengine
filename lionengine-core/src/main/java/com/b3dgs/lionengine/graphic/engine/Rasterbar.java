/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Represents raster bar control.
 */
public interface Rasterbar
{
    /**
     * Clear defined colors.
     */
    void clearRasterbarColor();

    /**
     * Add color mapping from palette.
     * 
     * @param buffer The palette buffer reference.
     */
    void addRasterbarColor(ImageBuffer buffer);

    /**
     * Set offset factor.
     * 
     * @param offsetY The vertical offset.
     * @param factorY The vertical factor.
     */
    void setRasterbarOffset(int offsetY, int factorY);

    /**
     * Set vertical location.
     * 
     * @param y1 The top location.
     * @param y2 The bottom location.
     */
    void setRasterbarY(int y1, int y2);

    /**
     * Compute rendering, must be called when effect is desired.
     */
    void renderRasterbar();
}
