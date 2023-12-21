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
package com.b3dgs.lionengine.example.opengl;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Main class.
 */
public final class AppHelloWorld
{
    /**
     * Main function called by the JVM.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // EngineOpenGl.start(AppHelloWorld.class.getSimpleName(), new Version(1, 0, 0), "resources");
        Loader.start(Config.windowed(new Resolution(640, 480, 60)), Scene.class);
    }

    /**
     * Private constructor.
     */
    private AppHelloWorld()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
