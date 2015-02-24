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
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Define a structure used to create multiple objects, sharing the same data and {@link ImageBuffer}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 */
public class SetupSurface
        extends Setup
{
    /** Surface reference. */
    public final ImageBuffer surface;
    /** Surface file name. */
    private final Media surfaceFile;

    /**
     * Create a setup without alpha for the surface.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media.
     */
    public SetupSurface(Media config) throws LionEngineException
    {
        this(config, false);
    }

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @param alpha The alpha use flag.
     * @throws LionEngineException If error when opening the media.
     */
    public SetupSurface(Media config, boolean alpha) throws LionEngineException
    {
        super(config);
        final String conf = config.getPath();
        final ConfigSurface surfaceData = ConfigSurface.create(getConfigurer());
        final String prefix = conf.substring(0, conf.lastIndexOf(Core.MEDIA.getSeparator()) + 1);
        surfaceFile = Core.MEDIA.create(prefix + surfaceData.getImage());
        surface = Core.GRAPHIC.getImageBuffer(surfaceFile, alpha);
    }

    /**
     * Get the surface file.
     * 
     * @return The surface file.
     */
    public final Media getSurfaceFile()
    {
        return surfaceFile;
    }

}
