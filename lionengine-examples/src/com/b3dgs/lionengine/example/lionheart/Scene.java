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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.example.lionheart.menu.Menu;

/**
 * Represents the scene where the player can control his hero over the map, fighting enemies.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Scene
        extends Sequence
{
    /** Original display. */
    public static final Resolution ORIGINAL_SCENE_DISPLAY = new Resolution(272, 208, 60);
    /** Scene display. */
    public static final Resolution SCENE_DISPLAY = new Resolution(320, 240, 60);

    /** World reference. */
    private final World world;
    /** Last level index played. */
    private int lastLevelIndex;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.ORIGINAL_SCENE_DISPLAY);
        world = new World(this);
        lastLevelIndex = -1;
    }

    /**
     * Load a level.
     * 
     * @param level The level to load.
     */
    private void loadLevel(LevelType level)
    {
        world.loadFromFile(Media.get(AppLionheart.LEVELS_DIR, level.getFilename()));
    }

    /**
     * Get the next level.
     * 
     * @return The next level.
     */
    private LevelType getNextLevel()
    {
        lastLevelIndex++;
        return LevelType.values()[lastLevelIndex % LevelType.LEVELS_NUMBER];
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        loadLevel(getNextLevel());
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
        if (keyboard.isPressedOnce(Key.ESCAPE) || world.isGameOver())
        {
            end(new Menu(loader));
        }
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        SonicArranger.play(world.level.getWorld().getMusic());
        update(extrp);
        render(g);
    }

    @Override
    protected void onResolutionChanged(int width, int height, int rate)
    {
        if (world != null)
        {
            world.setScreenSize(width, height);
        }
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        SonicArranger.stop();
        Sfx.stopAll();
        if (!hasNextSequence)
        {
            SonicArranger.terminate();
            Sfx.terminateAll();
        }
    }
}
