/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityFile;
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
 *         loader.start(new Scene(loader));
 *     }
 * }
 * </pre>
 * 
 * @since 13 June 2010
 * @version 6.0.0
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Engine
{
    /** Engine name. */
    public static final String NAME = "LionEngine";
    /** Engine version. */
    public static final String VERSION = "6.0.0";
    /** Engine last release date. */
    public static final String BEGIN_DATE = "13 June 2010";
    /** Engine last release date. */
    public static final String LAST_RELEASE_DATE = "13 June 2012";
    /** Engine author. */
    public static final String AUTHOR = "Pierre-Alexandre";
    /** Engine website. */
    public static final String WEBSITE = "http://www.b3dgs.com";
    /** Error message program name. */
    private static final String ERROR_PROGRAM_NAME = "Program name must not be null !";
    /** Error message program version. */
    private static final String ERROR_PROGRAM_VERSION = "The version must not be null !";
    /** Error message resource directory. */
    private static final String ERROR_RESOURCES_DIR = "The resources directory must not be null !";
    /** Error message verbose. */
    private static final String ERROR_VERBOSE_LEVEL = "The verbose level must not be null !";
    /** Error message temp directory. */
    private static final String ERROR_TEMP_DIRECTORY = "Temporary directory was not created !";
    /** Started engine flag. */
    private static boolean started = false;
    /** Graphic factory. */
    static FactoryGraphic factoryGraphic;
    /** Geometry factory. */
    static FactoryGeom factoryGeom;
    /** Input factory. */
    static FactoryInput factoryInput;
    /** User program name. */
    private static String programName;
    /** User program version. */
    private static String programVersion;

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
        Check.notNull(name, Engine.ERROR_PROGRAM_NAME);
        Check.notNull(version, Engine.ERROR_PROGRAM_VERSION);
        Check.notNull(resourcesDir, Engine.ERROR_RESOURCES_DIR);
        Check.notNull(level, Engine.ERROR_VERBOSE_LEVEL);

        if (!Engine.started)
        {
            Engine.init(name, version, resourcesDir, level);

            // LionEngine started
            Verbose.info("Starting \"LionEngine ", Engine.VERSION, "\" for \"", Engine.programName, " ",
                    Engine.programVersion, "\"");
            Verbose.info("Execution directory = ", Media.WORKING_DIR + Media.getSeparator());
            Verbose.info("Resources directory = ", Media.getPath(Media.WORKING_DIR, Media.getRessourcesDir()));
            Verbose.info("Temporary directory = ", Media.getTempDir() + Media.getSeparator());

            // Check version (clear temporary directory if version is different)
            final String versionFilename = Media.getPath(Media.getTempDir(), "version");
            Engine.checkVersion(versionFilename);
            Engine.storeVersion(versionFilename);

            Engine.started = true;

            // Load low level factory
            Engine.factoryGraphic = Engine.getFactory("FactoryGraphicImpl");
            Engine.factoryGeom = Engine.getFactory("FactoryGeomImpl");
            Engine.factoryInput = Engine.getFactory("FactoryInputImpl");
            UtilityImage.setGraphicFactory(Engine.factoryGraphic);
            UtilityMath.setGeomFactory(Engine.factoryGeom);
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     */
    public static void terminate()
    {
        Media.setResourcesDirectory(null);
        Media.setTempDirectory("");
        Media.setLoadFromJar(null, false);
        Engine.started = false;
    }

    /**
     * Get the program name (Engine must have been started).
     * 
     * @return The program name.
     */
    public static String getProgramName()
    {
        return Engine.programName;
    }

    /**
     * Get the program version (Engine must have been started).
     * 
     * @return The program version.
     */
    public static String getProgramVersion()
    {
        return Engine.programVersion;
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
        Verbose.set(level);
        Verbose.prepareLogger();
        Engine.programName = name;
        Engine.programVersion = version.toString();
        Media.setTempDirectory(name);
        Media.setResourcesDirectory(resourcesDir);
    }

    /**
     * Check version by clearing temporary directory if version is different.
     * 
     * @param versionFilename The file describing the program version.
     */
    private static void checkVersion(String versionFilename)
    {
        final File tempDir = new File(Media.getTempDir());
        if (tempDir.exists())
        {
            boolean delete = true;
            try (DataInputStream reader = new DataInputStream(new FileInputStream(versionFilename));)
            {
                final String version = reader.readUTF();
                if (Engine.programVersion.equals(version))
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
                writer.writeUTF(Engine.programVersion);
                writer.flush();
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception);
            }
        }
    }

    /**
     * Load low level factory.
     * 
     * @param name The factory name.
     * @return The factory instance.
     */
    private static <C> C getFactory(String name)
    {
        final String factoryName = FactoryGeom.class.getPackage().getName() + "." + name;
        try
        {

            return (C) Class.forName(factoryName).newInstance();
        }
        catch (InstantiationException
               | IllegalAccessException
               | ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, "Unable to load the factory at: " + factoryName);
        }
    }

    /**
     * Private constructor.
     */
    private Engine()
    {
        throw new RuntimeException();
    }
}
