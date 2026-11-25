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
package com.b3dgs.lionengine.graphics;

/**
 * Check of the image of a sheet, run by its group after the image is loaded and before the sheet is created; an
 * exception it throws adds nothing to the group and releases the image (REQ-SHEET-003).
 */
@FunctionalInterface
interface SheetCheck
{
    /**
     * Checks the size of the image.
     *
     * @param width The width of the image in pixels.
     * @param height The height of the image in pixels.
     */
    void check(int width, int height);
}
