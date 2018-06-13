/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.util.Optional;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Define a structure used to create multiple objects, sharing the same data and {@link ImageBuffer}.
 */
public class Setup extends Configurer
{
    /** No surface file. */
    static final String ERROR_SURFACE_FILE = "No surface file found !";
    /** No icon file. */
    static final String ERROR_ICON_FILE = "No icon file found !";
    /** No surface. */
    static final String ERROR_SURFACE = "No surface found !";
    /** Class error. */
    static final String ERROR_CLASS = "Class not found for: ";

    /** Surface reference. */
    protected final Optional<ImageBuffer> surface;
    /** Surface file name. */
    private final Optional<Media> surfaceFile;
    /** Icon file name. */
    private final Optional<Media> iconFile;
    /** Class reference (can be <code>null</code>). */
    private Class<?> clazz;

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media.
     */
    public Setup(Media config)
    {
        super(config);

        if (hasNode(SurfaceConfig.NODE_SURFACE))
        {
            final String conf = config.getPath();
            final SurfaceConfig surfaceData = SurfaceConfig.imports(getRoot());
            final String prefix = conf.substring(0, conf.lastIndexOf(Medias.getSeparator()) + 1);
            surfaceFile = Optional.of(Medias.create(prefix + surfaceData.getImage()));
            if (surfaceData.getIcon().isPresent())
            {
                iconFile = Optional.of(Medias.create(prefix + surfaceData.getIcon().get()));
            }
            else
            {
                iconFile = Optional.empty();
            }
            surface = Optional.of(Graphics.getImageBuffer(surfaceFile.get()));
        }
        else
        {
            surfaceFile = Optional.empty();
            iconFile = Optional.empty();
            surface = Optional.empty();
        }
    }

    /**
     * Get the class mapped to the setup. Lazy call (load class only first time, and keep its reference after).
     * 
     * @param <T> The element type.
     * @param classLoader The class loader used.
     * @return The class mapped to the setup.
     * @throws LionEngineException If the class was not found by the class loader.
     */
    @SuppressWarnings("unchecked")
    public final <T> Class<T> getConfigClass(ClassLoader classLoader)
    {
        if (clazz == null)
        {
            final FeaturableConfig config = FeaturableConfig.imports(this);
            try
            {
                clazz = classLoader.loadClass(config.getClassName());
            }
            catch (final ClassNotFoundException exception)
            {
                throw new LionEngineException(exception, Setup.ERROR_CLASS + getMedia().getPath());
            }
        }
        return (Class<T>) clazz;
    }

    /**
     * Get the surface file.
     * 
     * @return The surface file.
     * @throws LionEngineException If surface file not defined.
     */
    public final Media getSurfaceFile()
    {
        return surfaceFile.orElseThrow(() -> new LionEngineException(ERROR_SURFACE_FILE));
    }

    /**
     * Get the icon file.
     * 
     * @return The surface file.
     * @throws LionEngineException If icon file not defined.
     */
    public final Media getIconFile()
    {
        return iconFile.orElseThrow(() -> new LionEngineException(ERROR_ICON_FILE));
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface buffer.
     * @throws LionEngineException If surface not defined.
     */
    public final ImageBuffer getSurface()
    {
        return surface.orElseThrow(() -> new LionEngineException(ERROR_SURFACE));
    }
}
