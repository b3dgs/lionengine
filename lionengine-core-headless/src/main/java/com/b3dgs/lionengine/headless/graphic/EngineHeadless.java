/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless.graphic;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Engine headless implementation.
 */
public class EngineHeadless extends Engine
{
    /** User directory property. */
    private static final String PROPERTY_USER_DIR = "user.dir";

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version)
    {
        Engine.start(new EngineHeadless(name, version, Constant.EMPTY_STRING));
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, String resourcesDir)
    {
        Engine.start(new EngineHeadless(name, version, resourcesDir));
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param classResource The class loader reference (resources entry point, non <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, Class<?> classResource)
    {
        Engine.start(new EngineHeadless(name, version, classResource));
    }

    /** String resources directory. */
    private final Optional<String> resourcesDir;
    /** Class resource. */
    private final Optional<Class<?>> classResource;

    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineHeadless(String name, Version version, String resourcesDir)
    {
        super(name, version);

        Check.notNull(resourcesDir);

        this.resourcesDir = Optional.of(resourcesDir);
        classResource = Optional.empty();
        Medias.setResourcesDirectory(resourcesDir);
    }

    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param classResource The class loader reference (resources entry point, non <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineHeadless(String name, Version version, Class<?> classResource)
    {
        super(name, version);

        Check.notNull(classResource);

        this.classResource = Optional.of(classResource);
        resourcesDir = Optional.empty();
        Medias.setLoadFromJar(classResource);
    }

    /*
     * Engine
     */

    @Override
    protected void open()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicHeadless());
        if (resourcesDir.isPresent())
        {
            final String workingDir = Constant.getSystemProperty(PROPERTY_USER_DIR, Constant.EMPTY_STRING);
            Verbose.info("Resources directory = ", UtilFolder.getPath(workingDir, resourcesDir.get()));
        }
        else
        {
            Verbose.info("Class resources = ", classResource.get().getName());
        }
    }

    @Override
    protected void close()
    {
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
        AudioFactory.clearFormats();
    }
}
