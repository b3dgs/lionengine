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

import android.app.Activity;

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
 * public class MainActivity
 *         extends Activity
 * {
 *     &#064;Override
 *     protected void onPostCreate(Bundle savedInstanceState)
 *     {
 *         super.onPostCreate(savedInstanceState);
 * 
 *         Engine.start(&quot;Minimal&quot;, Version.create(1, 0, 0), this);
 *         final Resolution output = new Resolution(400, 240, 60);
 *         final Config config = new Config(output, 16, true);
 *         final Loader loader = new Loader(config);
 *         loader.start(new Scene(loader));
 *     }
 * 
 *     &#064;Override
 *     public void finish()
 *     {
 *         super.finish();
 *         Engine.terminate();
 *     }
 * }
 * </pre>
 * 
 * @since 13 June 2010
 * @version 6.1.0
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
        final ViewImpl view = new ViewImpl(activity);
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
