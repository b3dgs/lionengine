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
package com.b3dgs.lionengine.example.lionheart;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.lionheart.menu.Menu;
import com.b3dgs.lionengine.swing.Theme;

/**
 * Program starts here.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AppLionheart
{
    /** Application name. */
    public static final String NAME = "Lionheart";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 1, 0);
    /** Sprites directory. */
    public static final String SPRITES_DIR = "sprites";
    /** Levels directory. */
    public static final String LEVELS_DIR = "levels";
    /** Sheets directory. */
    public static final String TILES_DIR = "tiles";
    /** Rasters directory. */
    public static final String RASTERS_DIR = "rasters";
    /** Main entity directory name. */
    public static final String ENTITIES_DIR = "entities";
    /** Main entity directory name. */
    public static final String BACKGROUNDS_DIR = "backgrounds";
    /** Effects directory. */
    public static final String EFFECTS_DIR = "effects";
    /** Musics directory. */
    public static final String MUSICS_DIR = "musics";
    /** Sound fx directory name. */
    public static final String SFX_DIR = "sfx";
    /** Show collision bounds. */
    public static final boolean SHOW_COLLISIONS = false;
    /** Enable sound. */
    private static final boolean ENABLE_SOUND = false;
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "lionheart");
    /** Raster enabled. */
    public static boolean RASTER_ENABLED = true;

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args)
    {
        Engine.start(AppLionheart.NAME, AppLionheart.VERSION, AppLionheart.RESOURCES);
        Sfx.setEnabled(AppLionheart.ENABLE_SOUND);
        SonicArranger.setEnabled(AppLionheart.ENABLE_SOUND);

        final Resolution output = new Resolution(1280, 800, 60);
        final Config config = new Config(output, 16, true);
        final boolean enableEditor = false;
        if (enableEditor)
        {
            AppLionheart.RASTER_ENABLED = false;
            Theme.set(Theme.SYSTEM);
            final Editor editor = new Editor();
            editor.start();
        }
        else
        {
            final Loader loader = new Loader(config);
            loader.start(new Menu(loader));
        }
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new RuntimeException();
    }
}
