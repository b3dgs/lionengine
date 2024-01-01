/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.android;

import android.content.Context;
import android.content.res.AssetManager;

import com.b3dgs.lionengine.FactoryMedia;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFolder;

/**
 * Media factory implementation.
 */
public final class FactoryMediaAndroid implements FactoryMedia
{
    /** Asset manager. */
    private final AssetManager assetManager;
    /** Context reference. */
    private final Context context;

    /**
     * Constructor.
     *
     * @param context The context reference.
     * @param assetManager The asset manager.
     */
    public FactoryMediaAndroid(Context context, AssetManager assetManager)
    {
        super();

        this.context = context;
        this.assetManager = assetManager;
    }

    /*
     * FactoryMedia
     */

    @Override
    public Media create(String separator, String resourcesDir, Class<?> resourcesClass, String... path)
    {
        return new MediaAndroid(assetManager,
                                context,
                                separator,
                                resourcesDir,
                                UtilFolder.getPathSeparator(separator, path));
    }
}
