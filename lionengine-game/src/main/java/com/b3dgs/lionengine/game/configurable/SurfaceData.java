/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.configurable;

/**
 * Represents the surface data from a configurable node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SurfaceData
{
    /** The image descriptor. */
    private final String image;
    /** The icon descriptor (can be <code>null</code>). */
    private final String icon;

    /**
     * Constructor.
     * 
     * @param image The image file path.
     * @param icon The icon file path (can be <code>null</code>).
     */
    public SurfaceData(String image, String icon)
    {
        this.image = image;
        this.icon = icon;
    }

    /**
     * Get the image descriptor.
     * 
     * @return The image descriptor.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Get the icon descriptor.
     * 
     * @return The icon descriptor (<code>null</code> if none).
     */
    public String getIcon()
    {
        return icon;
    }
}
