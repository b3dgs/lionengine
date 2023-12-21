/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package com.b3dgs.lionengine.example.core.minimal;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Program starts here. When you start the JVM, ensure that this main function is called.
 */
public final class AppJava
{
    /**
     * Main function called by the JVM.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // The engine is initialized with our parameters:
        // - The name of our program: "AppJava"
        // - The program version: "1.0.0"
        // - The resources location, relative to the class package: AppJava
        // This mean that any resources loaded with Media.get(...) will have this directory as prefix.
        EngineAwt.start(AppJava.class.getSimpleName(), new Version(1, 0, 0), AppJava.class);

        // Final configuration (rendering will be scaled by 2 considering source and output resolution).
        // This is the final configuration container, including color depth and window mode.
        // Program starter, setup with our configuration. It just needs one sequence reference to start.
        Loader.start(Config.windowed(Scene.NATIVE.get2x()), Scene.class);
    }

    /**
     * Private constructor.
     */
    private AppJava()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
