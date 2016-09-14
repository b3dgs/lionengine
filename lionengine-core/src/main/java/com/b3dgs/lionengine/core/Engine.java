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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Engine base implementation. This class is intended to be inherited by an engine implementation depending of the
 * library used (as it is done for AWT, SWT and Android engine implementation).
 * 
 * @version 8.3.3
 * @since 13 June 2010
 */
public abstract class Engine
{
    /** Engine name. */
    public static final String NAME = "LionEngine";
    /** Engine author. */
    public static final String AUTHOR = "Pierre-Alexandre";
    /** Engine website. */
    public static final String WEBSITE = "http://lionengine.b3dgs.com";
    /** Engine version. */
    public static final Version VERSION = Version.create(8, 3, 3);
    /** Engine starting. */
    private static final String ENGINE_STARTING = "Starting \"" + Engine.NAME + " \"";
    /** Engine terminated. */
    private static final String ENGINE_TERMINATED = Engine.NAME + " terminated";
    /** For string. */
    private static final String FOR = " for ";
    /** Error message engine not started. */
    private static final String ERROR_STARTED_NOT = "The engine has not been started !";
    /** Error message engine already started. */
    private static final String ERROR_STARTED_ALREADY = "The engine has already been started !";

    /** Started engine flag. */
    private static volatile boolean started;
    /** Last engine used. */
    private static volatile Engine engine;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param engine The engine implementation used.
     * @throws LionEngineException If the engine has already been started.
     */
    public static synchronized void start(Engine engine)
    {
        if (started)
        {
            throw new LionEngineException(ERROR_STARTED_ALREADY);
        }
        Verbose.info(getStartingMessage(engine));

        engine.open();
        Engine.engine = engine;
        started = true;
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same JVM execution.
     */
    public static synchronized void terminate()
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }
        engine.close();
        engine = null;
        started = false;
        Verbose.info(ENGINE_TERMINATED);
    }

    /**
     * Get the program name.
     * 
     * @return The program name.
     * @throws LionEngineException If the engine has not been started.
     */
    public static String getProgramName()
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }
        return engine.getName();
    }

    /**
     * Get the program version.
     * 
     * @return The program version.
     * @throws LionEngineException If the engine has not been started.
     */
    public static Version getProgramVersion()
    {
        if (!started)
        {
            throw new LionEngineException(ERROR_STARTED_NOT);
        }
        return engine.getVersion();
    }

    /**
     * Check if engine is started.
     * 
     * @return <code>true</code> if started, <code>false</code> else.
     */
    public static synchronized boolean isStarted()
    {
        return started;
    }

    /**
     * Get the starting message for the specified engine.
     * 
     * @param engine The starting engine.
     * @return The starting message.
     */
    private static String getStartingMessage(Engine engine)
    {
        final StringBuilder message = new StringBuilder(ENGINE_STARTING);
        message.append(VERSION)
               .append(Constant.QUOTE)
               .append(FOR)
               .append(Constant.QUOTE)
               .append(engine.getName())
               .append(Constant.SPACE)
               .append(engine.getVersion())
               .append(Constant.QUOTE);
        return message.toString();
    }

    /** User program name. */
    private final String programName;
    /** User program version. */
    private final Version programVersion;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @throws LionEngineException If invalid parameters.
     */
    protected Engine(String name, Version version)
    {
        Check.notNull(name);
        Check.notNull(version);

        programName = name;
        programVersion = version;
    }

    /**
     * Get the program name.
     * 
     * @return The program name.
     */
    public final String getName()
    {
        return programName;
    }

    /**
     * Get the program version.
     * 
     * @return The program version.
     */
    public final Version getVersion()
    {
        return programVersion;
    }

    /**
     * Open engine. Has to be called before anything and only one time, in the main.
     */
    protected abstract void open();

    /**
     * Close the engine. It is necessary to call this function only if the engine need to be started again during
     * the same JVM execution.
     */
    protected abstract void close();
}
