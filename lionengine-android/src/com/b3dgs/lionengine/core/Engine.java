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

import android.app.Activity;
import android.view.SurfaceView;

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
        extends EngineImpl
{
    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     */
    public static void start(String name, Version version, Activity activity)
    {
        Engine.start(name, version, activity, Verbose.CRITICAL);
    }

    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     * @param level The verbose level (must not be <code>null</code>).
     */
    public static void start(String name, Version version, Activity activity, Verbose level)
    {
        if (!EngineImpl.started)
        {
            Engine.createView(activity);

            EngineImpl.start(name, version, level);
            Engine.init(name, version, activity, level);
        }
    }

    /**
     * Terminate the engine. It is necessary to call this function only if the engine need to be started again during
     * the same jvm execution.
     */
    public static void terminate()
    {
        EngineImpl.terminate();
    }

    /**
     * Create the view.
     * 
     * @param activity The activity reference.
     */
    private static void createView(Activity activity)
    {
        final SurfaceView view = new SurfaceView(activity);
        view.setWillNotDraw(false);
        ScreenImpl.setView(view);
        activity.setContentView(view);
    }

    /**
     * Initialize engine.
     * 
     * @param name The program name.
     * @param version The program version.
     * @param activity The activity reference.
     * @param level The verbose level.
     */
    private static void init(String name, Version version, Activity activity, Verbose level)
    {
        Media.setMediaImpl(MediaImpl.class);
        UtilityMedia.setAssertManager(activity.getAssets());
        UtilityMedia.setContentResolver(activity.getContentResolver());
    }
}
