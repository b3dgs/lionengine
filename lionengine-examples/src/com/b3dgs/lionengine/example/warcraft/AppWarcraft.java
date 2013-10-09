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
package com.b3dgs.lionengine.example.warcraft;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.menu.Menu;

// Tutorial: Warcraft
// This last rts tutorial shows a prototype of a Warcraft, using as much things as possible.

/**
 * Program starts here.
 */
public final class AppWarcraft
{
    /** Program title. */
    public static final String PROGRAM = "Warcraft Remake";
    /** Program version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Effects directory. */
    public static final String EFFECTS_DIR = "effects";
    /** Configuration file extension. */
    public static final String CONFIG_FILE_EXTENSION = ".xml";
    /** Program path. */
    public static final String PATH = Media.getPath("resources", "warcraft");

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        Engine.start(AppWarcraft.PROGRAM, AppWarcraft.VERSION, AppWarcraft.PATH);

        // Resolution
        final Resolution output = new Resolution(640, 400, 60);

        // Configuration
        final Config config = new Config(output, 16, true);

        // Starter
        final Loader loader = new Loader(config);
        ResourcesLoader.load();
        loader.start(new Menu(loader));
    }

    /**
     * Private constructor.
     */
    private AppWarcraft()
    {
        throw new RuntimeException();
    }
}
