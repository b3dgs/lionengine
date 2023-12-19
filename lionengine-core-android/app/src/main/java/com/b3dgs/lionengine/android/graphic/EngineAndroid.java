/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.android.graphic;

import android.util.DisplayMetrics;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.android.ActivityGame;
import com.b3dgs.lionengine.android.FactoryMediaAndroid;
import com.b3dgs.lionengine.android.WavFormat;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphics;

import java.io.File;

/**
 * Engine Android implementation.
 */
public final class EngineAndroid extends Engine
{
    /**
     * Start engine. Has to be called before anything and only one time, in the main.
     *
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public static void start(String name, Version version, ActivityGame activity)
    {
        Engine.start(new EngineAndroid(name, version, activity));
    }

    /** Activity reference. */
    private final ActivityGame activity;
    /** Closed flag. */
    private boolean closed;

    /**
     * Create engine.
     *
     * @param name The program name (must not be <code>null</code>).
     * @param version The program version (must not be <code>null</code>).
     * @param activity The activity reference (must not be <code>null</code>).
     * @throws LionEngineException If arguments error.
     */
    public EngineAndroid(String name, Version version, ActivityGame activity)
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
        ScreenAndroid.setView(view, new Rectangle(0, 0, metrics.widthPixels, metrics.heightPixels));
        activity.setContentView(view);

        AudioFactory.addFormat(new WavFormat());
        Medias.setFactoryMedia(new FactoryMediaAndroid(activity.getBaseContext(), activity.getAssets()));
        Medias.setResourcesDirectory(activity.getPackageName().replace(Constant.DOT, File.separator));
        Graphics.setFactoryGraphic(new FactoryGraphicAndroid());
    }

    @Override
    protected void close()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Graphics.setFactoryGraphic(null);
        AudioFactory.clearFormats();
    }

    @Override
    protected void postClose()
    {
        if (!closed)
        {
            closed = true;
            activity.finish();
        }
    }
}
