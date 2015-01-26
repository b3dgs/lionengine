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
package com.b3dgs.lionengine.example.core._1_minimal;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.awt.Engine;

/**
 * Program starts here. When you start the jvm, ensure that this main function is called.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AppJava
{
    /**
     * Main function called by the jvm.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine (name = "First Code", version = "1.0.0", resources directory = "resources")
        // The engine is initialized with our parameters:
        // - The name of our program: "First Code"
        // - The program version: "1.0.0"
        // - The main resources directory, relative to the execution directory: ./resources/
        // This mean that any resources loaded with Media.get(...) will have this directory as prefix.
        // To load resources from JAR, this alternative is preferred if external folder is not possible:
        // Engine.start("AppJava", Version.create(1, 0, 0), Verbose.CRITICAL, AppJava.class);
        Engine.start("AppJava", Version.create(1, 0, 0), Verbose.CRITICAL, "resources");

        // Resolution configuration (output = 640*480 at 60Hz). This is the output configuration.
        // As our native is in 320*240 (described in the Scene), the output will be scaled by 2.
        // If the current frame rate is lower than the required in the native,
        // the extrapolation value will allow to compensate any data calculation.
        final Resolution output = new Resolution(640, 480, 60);

        // Final configuration (rendering will be scaled by 2 considering source and output resolution).
        // This is the final configuration container, including color depth and window mode.
        final Config config = new Config(output, 16, true);

        // Program starter, setup with our configuration. It just needs one sequence reference to start.
        final Loader loader = new Loader(config);
        loader.start(Scene.class);
    }
}
