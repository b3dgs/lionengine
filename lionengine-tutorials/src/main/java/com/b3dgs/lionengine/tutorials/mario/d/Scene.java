/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import java.io.IOException;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.midi.AudioMidi;
import com.b3dgs.lionengine.audio.midi.Midi;
import com.b3dgs.lionengine.audio.wav.AudioWav;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;

/**
 * Game loop designed to handle our little world.
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);
    /** Level file. */
    private static final Media LEVEL = Medias.create("map", "level.lvl");

    /** Music. */
    private final Midi music = AudioMidi.loadMidi(Medias.create("music", "music.mid"));
    /** World reference. */
    private final World world;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        final Keyboard keyboard = getInputDevice(Keyboard.class);
        keyboard.addActionPressed(Keyboard.ESCAPE, () -> end());
        world = new World(getConfig(), keyboard);
    }

    /**
     * Import and save the level.
     */
    private static void importAndSave()
    {
        final MapTile map = new MapTileGame();
        map.create(Medias.create("map", "level.png"));
        final MapTilePersister mapPersister = new MapTilePersisterModel(map);
        try (FileWriting file = Stream.createFileWriting(LEVEL))
        {
            mapPersister.save(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on saving map !");
        }
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        if (!LEVEL.exists())
        {
            importAndSave();
        }
        world.loadFromFile(LEVEL);
        music.setVolume(20);
        music.play(true);
    }

    @Override
    public void update(double extrp)
    {
        world.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        music.stop();
        AudioWav.terminate();
    }
}
