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
package com.b3dgs.lionengine.core.android;

import android.content.Context;
import android.content.res.AssetManager;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.FactoryMedia;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Media factory implementation.
 */
final class FactoryMediaAndroid implements FactoryMedia
{
    /** Asset manager. */
    private static volatile AssetManager assetManager;
    /** Context reference. */
    private static volatile Context context;

    /**
     * Set the asset manager.
     * 
     * @param assetManager The asset manager.
     */
    static synchronized void setAssertManager(AssetManager assetManager)
    {
        FactoryMediaAndroid.assetManager = assetManager;
    }

    /**
     * Set the context reference.
     * 
     * @param context The context reference.
     */
    static synchronized void setContext(Context context)
    {
        FactoryMediaAndroid.context = context;
    }

    /**
     * Internal constructor.
     */
    FactoryMediaAndroid()
    {
        super();
    }

    /*
     * FactoryMedia
     */

    @Override
    public Media create(String separator, String resourcesDir, String... path)
    {
        return new MediaAndroid(assetManager,
                                context,
                                separator,
                                resourcesDir,
                                UtilFolder.getPathSeparator(separator, path));
    }

    @Override
    public Media create(String separator, Class<?> loader, String... path)
    {
        return new MediaAndroid(assetManager,
                                context,
                                separator,
                                loader.getPackage().getName().replace(Constant.DOT, Medias.getSeparator()),
                                UtilFolder.getPathSeparator(separator, path));
    }
}
