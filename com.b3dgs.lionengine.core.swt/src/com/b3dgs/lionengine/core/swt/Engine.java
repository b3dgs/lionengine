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
package com.b3dgs.lionengine.core.swt;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Verbose;

/**
 * <b>LionEngine</b>.
 * <p>
 * By <a href="http://www.b3dgs.com"><b>Byron 3D Games Studio</b></a>
 * </p>
 * <p>
 * Standard engine initialization:
 * </p>
 * 
 * <pre>
 * public final class AppMinimal
 * {
 *     public static void main(String[] args)
 *     {
 *         // Start engine (name = &quot;First Code&quot;, version = &quot;1.0.0&quot;, resources directory = &quot;resources&quot;)
 *         // The engine is initialized with our parameters:
 *         // - The name of our program: &quot;First Code&quot;
 *         // - The program version: &quot;1.0.0&quot;
 *         // - The main resources directory, relative to the execution directory: ./resources/
 *         // This mean that any resources loaded with Media.get(...) will have this directory as prefix.
 *         Engine.start(&quot;Minimal&quot;, Version.create(1, 0, 0), &quot;resources&quot;);
 * 
 *         // Resolution configuration (output = 640*480 at 60Hz). This is corresponding to the output configuration.
 *         // As our native is in 320*240 (described in the Scene), the output will be scaled by 2.
 *         // If the current frame rate is lower than the required in the native,
 *         // the extrapolation value will allow to compensate any data calculation.
 *         final Resolution output = new Resolution(640, 480, 60);
 * 
 *         // Final configuration (rendering will be scaled by 2 considering source and output resolution).
 *         // This is the final configuration container, including color depth and window mode.
 *         final Config config = new Config(output, 16, true);
 * 
 *         // Program starter, setup with our configuration. It just needs one sequence reference to start.
 *         final Loader loader = new Loader(config);
 *         loader.start(Scene.class);
 *     }
 * }
 * </pre>
 * 
 * @since 13 June 2010
 * @version 7.1.0
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Engine
        extends EngineCore
{
    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (must not be <code>null</code>).
     * @throws LionEngineException If the engine has already been started.
     */
    public static void start(String name, Version version, Verbose level, String resourcesDir)
            throws LionEngineException
    {
        Engine.start(name, version, level, resourcesDir, null);
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     * @param classResource The class loader reference (resources entry point).
     * @throws LionEngineException If classResource is <code>null</code>
     */
    public static void start(String name, Version version, Verbose level, Class<?> classResource)
            throws LionEngineException
    {
        Check.notNull(classResource);

        Engine.start(name, version, level, null, classResource);
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     * @param resourcesDir The main resources directory.
     * @param classResource The class loader reference (resources entry point).
     * @throws LionEngineException If the engine has already been started.
     */
    private static void start(String name, Version version, Verbose level, String resourcesDir, Class<?> classResource)
            throws LionEngineException
    {
        if (!EngineCore.isStarted())
        {
            EngineCore.start(name, version, level, new FactoryGraphicSwt(), new FactoryMediaSwt());

            UtilityMedia.setLoadFromJar(classResource);
            UtilityMedia.setResourcesDirectory(resourcesDir);

            final String workingDir = EngineCore.getSystemProperty("user.dir", null);
            if (workingDir != null && resourcesDir != null)
            {
                Verbose.info("Resources directory = ", UtilFile.getPath(workingDir, UtilityMedia.getRessourcesDir()));
            }
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     * 
     * @throws LionEngineException If the engine has not been started.
     */
    public static void terminate() throws LionEngineException
    {
        EngineCore.terminate();
        UtilityMedia.setResourcesDirectory(null);
        UtilityMedia.setLoadFromJar(null);
    }

    /**
     * Private constructor.
     */
    private Engine()
    {
        throw new RuntimeException();
    }
}
