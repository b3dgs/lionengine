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
package com.b3dgs.lionengine.example.core.dpi;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.awt.EngineAwt;
import com.b3dgs.lionengine.core.sequence.Loader;

/**
 * Main class.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
public class AppDpi
{
    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        EngineAwt.start(AppDpi.class.getSimpleName(), Version.create(1, 0, 0), AppDpi.class);

        run(320, 200);
        run(640, 400);
        run(1280, 720);

        Engine.terminate();
    }

    /**
     * Run scene with specific resolution.
     * 
     * @param width The width.
     * @param height The height.
     */
    private static void run(int width, int height)
    {
        final Resolution output = new Resolution(width, height, 60);
        Loader.start(Config.windowed(output), Scene.class).await();
    }
}
