/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the surface data from a configurer.
 * 
 * @see com.b3dgs.lionengine.game.object.SetupSurface
 */
public final class SurfaceConfig
{
    /** Surface node name. */
    public static final String SURFACE = Configurer.PREFIX + "surface";
    /** Surface image node. */
    public static final String SURFACE_IMAGE = "image";
    /** Surface icon node. */
    public static final String SURFACE_ICON = "icon";

    /**
     * Create the surface data from node.
     * 
     * @param configurer The configurer reference.
     * @return The surface data.
     * @throws LionEngineException If unable to read node.
     */
    public static SurfaceConfig create(Configurer configurer)
    {
        final String surface = configurer.getString(SurfaceConfig.SURFACE_IMAGE, SurfaceConfig.SURFACE);

        return new SurfaceConfig(surface, SurfaceConfig.getSurfaceIcon(configurer));
    }

    /** The image descriptor. */
    private final String image;
    /** The icon descriptor (can be <code>null</code>). */
    private final String icon;

    /**
     * Disabled constructor.
     */
    private SurfaceConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Create the surface configuration.
     * 
     * @param image The image file path.
     * @param icon The icon file path (can be <code>null</code>).
     */
    private SurfaceConfig(String image, String icon)
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

    /**
     * Get the surface icon if existing.
     * 
     * @param configurer The configurer reference.
     * @return The surface icon, <code>null</code> if none.
     */
    private static String getSurfaceIcon(Configurer configurer)
    {
        if (configurer.getRoot().getChild(SurfaceConfig.SURFACE).hasAttribute(SurfaceConfig.SURFACE_ICON))
        {
            return configurer.getString(SurfaceConfig.SURFACE_ICON, SurfaceConfig.SURFACE);
        }
        return null;
    }
}
