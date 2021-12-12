/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt.graphic;

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
 * Engine Awt implementation.
 */
public class EngineAwt extends Engine
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
        Engine.start(new EngineAwt(name, version, null, null));
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (can be <code>null</code>, {@link Medias#DEFAULT_RESOURCES_DIR}
     *            used then).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, String resourcesDir)
    {
        Engine.start(new EngineAwt(name, version, resourcesDir, null));
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesClass The class loader reference (resources entry point, can be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, Class<?> resourcesClass)
    {
        Engine.start(new EngineAwt(name, version, null, resourcesClass));
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (can be <code>null</code>, {@link Medias#DEFAULT_RESOURCES_DIR}
     *            used then).
     * @param resourcesClass The class loader reference (resources entry point, can be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, String resourcesDir, Class<?> resourcesClass)
    {
        Engine.start(new EngineAwt(name, version, resourcesDir, resourcesClass));
    }

    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (can be <code>null</code>, {@link Medias#DEFAULT_RESOURCES_DIR}
     *            used then).
     * @param resourcesClass The class loader reference (resources entry point, can be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineAwt(String name, Version version, String resourcesDir, Class<?> resourcesClass)
    {
        super(name, version);

        Medias.setResourcesDirectory(resourcesDir);
        Medias.setLoadFromJar(resourcesClass);
    }

    /*
     * Engine
     */

    @Override
    protected void open()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicAwt());

        final String workingDir = Constant.getSystemProperty(PROPERTY_USER_DIR, Constant.EMPTY_STRING);
        Verbose.info("Resources dir = ", UtilFolder.getPath(workingDir, Medias.getResourcesDirectory()));
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
