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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.example.warcraft.menu.Menu;

/**
 * Game loop designed to handle the world.
 */
public final class Scene
        extends Sequence
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** World reference. */
    private final World world;
    /** Game configuration. */
    private final GameConfig config;
    /** Music. */
    private final Midi music;

    /**
     * Standard constructor.
     * 
     * @param loader The loader reference.
     * @param config The game configuration.
     */
    public Scene(final Loader loader, GameConfig config)
    {
        super(loader, Scene.NATIVE);
        this.config = config;
        world = new World(this, config);
        music = AudioMidi.loadMidi(Media.get(ResourcesLoader.MUSICS_DIR, "orcs.mid"));
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get(ResourcesLoader.MAPS_DIR, config.map));
        // music.play(true);
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
        if (keyboard.isPressedOnce(Key.ESCAPE))
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
    protected void onTerminate(boolean hasNextSequence)
    {
        music.stop();
    }
}
