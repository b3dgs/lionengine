/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Version;

/**
 * Engine base implementation. This class is intended to be inherited by an engine implementation depending of the
 * library used (as it is done for AWT, SWT and Android implementation).
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @version 9.0.0
 * @since 13 June 2010
 */
public abstract class Engine
{
    /** Engine starting. */
    private static final String ENGINE_STARTING = "Starting \"" + Constant.ENGINE_NAME + " \"";
    /** Engine terminated. */
    private static final String ENGINE_TERMINATED = Constant.ENGINE_NAME + " terminated";
    /** For string. */
    private static final String FOR = " for ";
    /** Error message engine not started. */
    private static final String ERROR_STARTED_NOT = "The engine has not been started !";
    /** Error message engine already started. */
    private static final String ERROR_STARTED_ALREADY = "The engine has already been started !";

    /** Started engine flag. */
    private static volatile boolean started;
    /** Last engine used. */
    private static Engine engine;

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param engine The engine implementation used (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or the engine has already been started.
     */
    public static final synchronized void start(Engine engine)
    {
        Check.notNull(engine);

        if (started)
        {
            throw new LionEngineException(ERROR_STARTED_ALREADY);
        }
        Verbose.info(getStartingMessage(engine));

        Medias.setFactoryMedia(new FactoryMediaDefault());

        engine.open();
        Engine.engine = engine;
        started = true;
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same JVM execution. Does nothing if engine is not started.
     */
    public static final synchronized void terminate()
    {
        if (started)
        {
            engine.close();
            started = false;
            Verbose.info(ENGINE_TERMINATED);
            engine.postClose();
            engine = null;
        }
    }

    /**
     * Get the program name.
     * 
     * @return The program name.
     * @throws LionEngineException If the engine has not been started.
     */
    public static final synchronized String getProgramName()
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
    public static final synchronized Version getProgramVersion()
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
    public static final boolean isStarted()
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
        return new StringBuilder().append(ENGINE_STARTING)
                                  .append(Constant.ENGINE_VERSION)
                                  .append(Constant.QUOTE)
                                  .append(FOR)
                                  .append(Constant.QUOTE)
                                  .append(engine.getName())
                                  .append(Constant.SPACE)
                                  .append(engine.getVersion())
                                  .append(Constant.QUOTE)
                                  .toString();
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
        super();

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

    /**
     * Post close action. Engine is closed at this point. Must be used for last clean up.
     */
    protected void postClose()
    {
        // Nothing by default
    }
}
