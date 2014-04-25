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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;

/**
 * Engine base implementation. This class is intended to be inherited by an engine implementation depending of the
 * library used (as it is done for AWT, SWT and Android engine implementation).
 * 
 * @author Pierre-Alexandre
 */
public abstract class EngineCore
{
    /** Engine name. */
    public static final String NAME = "LionEngine";
    /** Engine version. */
    public static final String VERSION = "6.1.0";
    /** Engine begin date. */
    public static final String BEGIN_DATE = "13 June 2010";
    /** Engine last release date. */
    public static final String LAST_RELEASE_DATE = "30 March 2014";
    /** Engine author. */
    public static final String AUTHOR = "Pierre-Alexandre";
    /** Engine website. */
    public static final String WEBSITE = "http://lionengine.b3dgs.com";
    /** Error message program name. */
    private static final String ERROR_PROGRAM_NAME = "Program name must not be null !";
    /** Error message program version. */
    private static final String ERROR_PROGRAM_VERSION = "The version must not be null !";
    /** Error message verbose. */
    private static final String ERROR_VERBOSE_LEVEL = "The verbose level must not be null !";
    /** Error message graphic factory. */
    private static final String ERROR_FACTORY_GRAPHIC = "The graphic factory must not be null !";
    /** Error message media factory. */
    private static final String ERROR_FACTORY_MEDIA = "The media factory must not be null !";
    /** Error message engine already started. */
    private static final String ERROR_STARTED_ALREADY = "The engine has already been started !";
    /** Error message engine not started. */
    private static final String ERROR_STARTED_NOT = "The engine has not been started !";
    /** Empty property. */
    private static final String EMPTY_PROPERTY = "";
    /** Engine starting. */
    private static final String ENGINE_STARTING = "Starting \"LionEngine ";
    /** Engine terminated. */
    private static final String ENGINE_TERMINATED = "LionEngine terminated";
    /** Started engine flag. */
    private static boolean started = false;
    /** User program name. */
    private static String programName;
    /** User program version. */
    private static Version programVersion;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     * @param factoryGraphic The graphic factory (must not be <code>null</code>).
     * @param factoryMedia The media factory (must not be <code>null</code>).
     * @throws LionEngineException If the engine has already been started.
     */
    public static void start(String name, Version version, Verbose level, FactoryGraphic factoryGraphic,
            FactoryMedia factoryMedia)
    {
        if (!EngineCore.started)
        {
            Check.notNull(name, EngineCore.ERROR_PROGRAM_NAME);
            Check.notNull(version, EngineCore.ERROR_PROGRAM_VERSION);
            Check.notNull(level, EngineCore.ERROR_VERBOSE_LEVEL);
            Check.notNull(factoryGraphic, EngineCore.ERROR_FACTORY_GRAPHIC);
            Check.notNull(factoryMedia, EngineCore.ERROR_FACTORY_MEDIA);

            Verbose.set(level);
            Verbose.prepareLogger();

            EngineCore.programName = name;
            EngineCore.programVersion = version;

            FactoryGraphicProvider.setFactoryGraphic(factoryGraphic);
            FactoryMediaProvider.setFactoryMedia(factoryMedia);

            final StringBuilder message = new StringBuilder(EngineCore.ENGINE_STARTING);
            message.append(EngineCore.VERSION).append("\" for \"");
            message.append(EngineCore.programName).append(" ");
            message.append(EngineCore.programVersion).append("\"");
            Verbose.info(message.toString());

            EngineCore.started = true;
        }
        else
        {
            throw new LionEngineException(EngineCore.ERROR_STARTED_ALREADY);
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution. This function is automatically called when the program life cycle reach the end.
     * 
     * @throws LionEngineException If the engine has not been started.
     */
    public static void terminate()
    {
        if (EngineCore.started)
        {
            EngineCore.started = false;
            EngineCore.programName = null;
            EngineCore.programVersion = null;
            FactoryGraphicProvider.setFactoryGraphic(null);
            FactoryMediaProvider.setFactoryMedia(null);
            Verbose.info(EngineCore.ENGINE_TERMINATED);
        }
        else
        {
            throw new LionEngineException(EngineCore.ERROR_STARTED_NOT);
        }
    }

    /**
     * Get the program name.
     * 
     * @return The program name.
     * @throws LionEngineException If the engine has not been started.
     */
    public static String getProgramName()
    {
        if (!EngineCore.started)
        {
            throw new LionEngineException(EngineCore.ERROR_STARTED_NOT);
        }
        return EngineCore.programName;
    }

    /**
     * Get the program version.
     * 
     * @return The program version.
     * @throws LionEngineException If the engine has not been started.
     */
    public static Version getProgramVersion()
    {
        if (!EngineCore.started)
        {
            throw new LionEngineException(EngineCore.ERROR_STARTED_NOT);
        }
        return EngineCore.programVersion;
    }

    /**
     * Check if engine is started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public static boolean isStarted()
    {
        return EngineCore.started;
    }

    /**
     * Get the system property. If the property is not valid due to a {@link SecurityException}, an empty string is
     * returned.
     * 
     * @param property The system property.
     * @return The system property value (<code>null</code> if there is not any corresponding property).
     */
    public static String getSystemProperty(String property)
    {
        try
        {
            return System.getProperty(property);
        }
        catch (final SecurityException exception)
        {
            return EngineCore.EMPTY_PROPERTY;
        }
    }
}
