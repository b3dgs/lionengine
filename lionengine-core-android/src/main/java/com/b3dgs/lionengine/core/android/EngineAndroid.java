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
package com.b3dgs.lionengine.core.android;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.FactoryMediaDefault;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Engine Android implementation.
 */
public final class EngineAndroid extends Engine
{
    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, Activity activity)
    {
        Engine.start(new EngineAndroid(name, version, activity));
    }

    /** Activity reference. */
    private final Activity activity;

    /**
     * Create engine.
     * 
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineAndroid(String name, Version version, Activity activity)
    {
        super(name, version);
        Check.notNull(activity);
        this.activity = activity;
    }

    /*
     * Engine
     */

    @Override
    protected void open()
    {
        final ViewAndroid view = new ViewAndroid(activity);
        view.setWillNotDraw(false);

        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ScreenAndroid.setView(view, Geom.createRectangle(0, 0, metrics.widthPixels, metrics.heightPixels));
        activity.setContentView(view);

        FactoryMediaAndroid.setAssertManager(activity.getAssets());
        FactoryMediaAndroid.setContentResolver(activity.getContentResolver());

        Medias.setFactoryMedia(new FactoryMediaAndroid());
        Graphics.setFactoryGraphic(new FactoryGraphicAndroid());
    }

    @Override
    protected void close()
    {
        FactoryMediaAndroid.setAssertManager(null);
        FactoryMediaAndroid.setContentResolver(null);

        Medias.setFactoryMedia(new FactoryMediaDefault());
        Graphics.setFactoryGraphic(null);
    }
}
