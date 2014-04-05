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
package com.b3dgs.lionengine.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Version;

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
 * @version 6.2.0
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Engine
        extends EngineImpl
{
    /** Error message resource directory. */
    private static final String ERROR_RESOURCES_DIR = "The resources directory must not be null !";
    /** Error message temp directory. */
    private static final String ERROR_TEMP_DIRECTORY = "Temporary directory was not created !";

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (must not be <code>null</code>).
     */
    public static void start(String name, Version version, String resourcesDir)
    {
        Engine.start(name, version, resourcesDir, Verbose.CRITICAL);
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param resourcesDir The main resources directory (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     */
    public static void start(String name, Version version, String resourcesDir, Verbose level)
    {
        Check.notNull(resourcesDir, Engine.ERROR_RESOURCES_DIR);

        if (!EngineImpl.started)
        {
            EngineImpl.start(name, version, level);
            Engine.init(name, version, resourcesDir, level);

            // LionEngine started
            Verbose.info("Execution directory = ", UtilityMedia.WORKING_DIR + Media.getSeparator());
            Verbose.info("Resources directory = ",
                    Media.getPath(UtilityMedia.WORKING_DIR, UtilityMedia.getRessourcesDir()));
            Verbose.info("Temporary directory = ", UtilityFile.getTempDir() + Media.getSeparator());

            // Check version (clear temporary directory if version is different)
            final String versionFilename = Media.getPath(UtilityFile.getTempDir(), "version");
            Engine.checkVersion(versionFilename);
            Engine.storeVersion(versionFilename);
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     */
    public static void terminate()
    {
        EngineImpl.terminate();
        UtilityMedia.setResourcesDirectory(null);
        UtilityFile.setTempDirectory("");
        UtilityMedia.setLoadFromJar(null, false);
    }

    /**
     * Initialize engine.
     * 
     * @param name The program name.
     * @param version The program version.
     * @param resourcesDir The main resources directory.
     * @param level The verbose level.
     */
    private static void init(String name, Version version, String resourcesDir, Verbose level)
    {
        Media.setMediaImpl(MediaImpl.class);
        UtilityFile.setTempDirectory(name);
        UtilityMedia.setResourcesDirectory(resourcesDir);
    }

    /**
     * Check version by clearing temporary directory if version is different.
     * 
     * @param versionFilename The file describing the program version.
     */
    private static void checkVersion(String versionFilename)
    {
        final File tempDir = new File(UtilityFile.getTempDir());
        if (tempDir.exists())
        {
            boolean delete = true;
            try (DataInputStream reader = new DataInputStream(new FileInputStream(versionFilename));)
            {
                final String version = reader.readUTF();
                if (EngineImpl.getProgramVersion().equals(version))
                {
                    delete = false;
                }
            }
            catch (final IOException exception)
            {
                delete = true;
            }
            if (delete)
            {
                UtilityFile.deleteDirectory(tempDir);
            }
        }

        // Create temporary directory
        if (!tempDir.exists() && !tempDir.mkdir())
        {
            Verbose.warning(Engine.class, "checkVersion", Engine.ERROR_TEMP_DIRECTORY);
        }
    }

    /**
     * Store version if needed for next time.
     * 
     * @param versionFilename The file describing the program version.
     */
    private static void storeVersion(String versionFilename)
    {
        if (!UtilityFile.exists(versionFilename))
        {
            try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(versionFilename));)
            {
                writer.writeUTF(EngineImpl.getProgramVersion());
                writer.flush();
            }
            catch (final IOException exception)
            {
                Verbose.exception(EngineImpl.class, "storeVersion", exception);
            }
        }
    }

    /**
     * Constructor.
     */
    private Engine()
    {
        throw new RuntimeException();
    }
}
