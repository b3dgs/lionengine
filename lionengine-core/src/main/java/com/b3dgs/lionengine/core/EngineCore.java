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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;

/**
 * Engine base implementation. This class is intended to be inherited by an engine implementation depending of the
 * library used (as it is done for AWT, SWT and Android engine implementation).
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EngineCore
{
    /** Engine name. */
    public static final String NAME = "LionEngine";
    /** Engine version. */
    public static final Version VERSION = Version.create(8, 0, 0);
    /** Engine begin date. */
    public static final String BEGIN_DATE = "13 June 2010";
    /** Engine last release date. */
    public static final String LAST_RELEASE_DATE = "13 June 2015";
    /** Engine author. */
    public static final String AUTHOR = "Pierre-Alexandre";
    /** Engine website. */
    public static final String WEBSITE = "http://lionengine.b3dgs.com";
    /** Error message engine already started. */
    private static final String ERROR_STARTED_ALREADY = "The engine has already been started !";
    /** Error message engine not started. */
    private static final String ERROR_STARTED_NOT = "The engine has not been started !";
    /** Engine starting. */
    private static final String ENGINE_STARTING = "Starting \"LionEngine ";
    /** Engine terminated. */
    private static final String ENGINE_TERMINATED = "LionEngine terminated";
    /** Started engine flag. */
    private static volatile boolean started;
    /** User program name. */
    private static volatile String programName;
    /** User program version. */
    private static volatile Version programVersion;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param factoryGraphic The graphic factory (must not be <code>null</code>).
     * @param factoryMedia The media factory (must not be <code>null</code>).
     * @throws LionEngineException If the engine has already been started.
     */
    public static synchronized void start(String name, Version version, FactoryGraphic factoryGraphic,
            FactoryMedia factoryMedia) throws LionEngineException
    {
        if (started)
        {
            throw new LionEngineException(ERROR_STARTED_ALREADY);
        }

        Check.notNull(name);
        Check.notNull(version);
        Check.notNull(factoryGraphic);
        Check.notNull(factoryMedia);

        Verbose.prepareLogger();

        programName = name;
        programVersion = version;

        final StringBuilder message = new StringBuilder(ENGINE_STARTING);
        message.append(VERSION).append("\" for \"");
        message.append(programName).append(" ");
        message.append(programVersion).append("\"");
        Verbose.info(message.toString());

        Graphics.setFactoryGraphic(factoryGraphic);
        Medias.setFactoryMedia(factoryMedia);

        started = true;
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     * 
     * @throws LionEngineException If the engine has not been started.
     */
    public static synchronized void terminate() throws LionEngineException
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }

        Graphics.setFactoryGraphic(null);
        Medias.setFactoryMedia(null);

        programName = null;
        programVersion = null;
        started = false;

        Verbose.info(ENGINE_TERMINATED);
    }

    /**
     * Get the program name.
     * 
     * @return The program name.
     * @throws LionEngineException If the engine has not been started.
     */
    public static synchronized String getProgramName() throws LionEngineException
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }
        return programName;
    }

    /**
     * Get the program version.
     * 
     * @return The program version.
     * @throws LionEngineException If the engine has not been started.
     */
    public static synchronized Version getProgramVersion() throws LionEngineException
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }
        return programVersion;
    }

    /**
     * Check if engine is started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public static boolean isStarted()
    {
        return started;
    }

    /**
     * Get the system property. If the property is not valid due to a {@link SecurityException}, an empty string is
     * returned.
     * 
     * @param property The system property.
     * @param def The default value used if property is not available.
     * @return The system property value (<code>null</code> if there is not any corresponding property).
     */
    public static String getSystemProperty(String property, String def)
    {
        try
        {
            return System.getProperty(property);
        }
        catch (final SecurityException exception)
        {
            Verbose.exception(EngineCore.class, "getSystemProperty", exception);
            return def;
        }
    }
}
