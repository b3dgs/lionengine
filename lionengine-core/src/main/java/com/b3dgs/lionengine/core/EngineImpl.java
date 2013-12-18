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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;

/**
 * Engine base implementation.
 * 
 * @author Pierre-Alexandre
 */
abstract class EngineImpl
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
    /** Error message verbose. */
    private static final String ERROR_VERBOSE_LEVEL = "The verbose level must not be null !";
    /** Engine terminated. */
    private static final String ENGINE_TERMINATED = "LionEngine terminated";
    /** Factory base name. */
    private static final String FACTORY_BASE = EngineImpl.class.getPackage().getName() + ".";
    /** Started engine flag. */
    static boolean started = false;
    /** Graphic factory. */
    static FactoryGraphic factoryGraphic;
    /** Input factory. */
    static FactoryInput factoryInput;
    /** Audio factory. */
    static FactoryAudio factoryAudio;
    /** User program name. */
    private static String programName;
    /** User program version. */
    private static String programVersion;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     */
    protected static void start(String name, Version version, Verbose level)
    {
        Check.notNull(name, EngineImpl.ERROR_PROGRAM_NAME);
        Check.notNull(version, EngineImpl.ERROR_PROGRAM_VERSION);
        Check.notNull(level, EngineImpl.ERROR_VERBOSE_LEVEL);

        if (!EngineImpl.started)
        {
            EngineImpl.init(name, version, level);

            // LionEngine started
            final StringBuilder message = new StringBuilder("Starting \"LionEngine ");
            message.append(EngineImpl.VERSION).append("\" for \"");
            message.append(EngineImpl.programName).append(" ");
            message.append(EngineImpl.programVersion).append("\"");
            Verbose.info(message.toString());

            EngineImpl.started = true;

            // Load low level factory
            EngineImpl.factoryGraphic = EngineImpl.getFactory("FactoryGraphicImpl");
            EngineImpl.factoryInput = EngineImpl.getFactory("FactoryInputImpl");
            EngineImpl.factoryAudio = EngineImpl.getFactory("FactoryAudioImpl");
            AudioWav.factoryAudio = EngineImpl.factoryAudio;
            AudioMidi.factoryAudio = EngineImpl.factoryAudio;
            UtilityImage.setGraphicFactory(EngineImpl.factoryGraphic);
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     */
    protected static void terminate()
    {
        EngineImpl.started = false;
        EngineImpl.programName = null;
        EngineImpl.programVersion = null;
        Verbose.info(EngineImpl.ENGINE_TERMINATED);
    }

    /**
     * Get the program name (Engine must have been started).
     * 
     * @return The program name.
     */
    public static String getProgramName()
    {
        return EngineImpl.programName;
    }

    /**
     * Get the program version (Engine must have been started).
     * 
     * @return The program version.
     */
    public static String getProgramVersion()
    {
        return EngineImpl.programVersion;
    }

    /**
     * Initialize engine.
     * 
     * @param name The program name.
     * @param version The program version.
     * @param level The verbose level.
     */
    protected static void init(String name, Version version, Verbose level)
    {
        Verbose.set(level);
        Verbose.prepareLogger();
        EngineImpl.programName = name;
        EngineImpl.programVersion = version.toString();
    }

    /**
     * Load low level factory.
     * 
     * @param name The factory name.
     * @return The factory instance.
     */
    private static <C> C getFactory(String name)
    {
        final String factoryName = EngineImpl.FACTORY_BASE + name;
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
     * Constructor.
     */
    EngineImpl()
    {
        throw new RuntimeException();
    }
}
