/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.swt;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Engine SWT implementation.
 */
public class EngineSwt extends Engine
{
    /**
     * Start engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version)
    {
        Engine.start(new EngineSwt(name, version, Constant.EMPTY_STRING));
    }

    /**
     * Start engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (can be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, String resourcesDir)
    {
        Engine.start(new EngineSwt(name, version, resourcesDir));
    }

    /**
     * Start engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param classResource The class loader reference (resources entry point, non <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, Class<?> classResource)
    {
        Engine.start(new EngineSwt(name, version, classResource));
    }

    /** String resources directory. */
    private final String resourcesDir;
    /** Class resource. */
    private final Class<?> classResource;

    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (can be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineSwt(String name, Version version, String resourcesDir)
    {
        super(name, version);

        Check.notNull(resourcesDir);

        this.resourcesDir = resourcesDir;
        classResource = null;
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
    public EngineSwt(String name, Version version, Class<?> classResource)
    {
        super(name, version);

        Check.notNull(classResource);

        this.classResource = classResource;
        resourcesDir = null;
        Medias.setLoadFromJar(classResource);
    }

    /*
     * Engine
     */

    @Override
    protected void open()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicSwt());
        if (resourcesDir != null)
        {
            final String workingDir = Constant.getSystemProperty("user.dir", Constant.EMPTY_STRING);
            Verbose.info("Resources directory = ", UtilFolder.getPath(workingDir, resourcesDir));
        }
        else if (classResource != null)
        {
            Verbose.info("Class resources = ", classResource.getName());
        }
    }

    @Override
    protected void close()
    {
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }
}
