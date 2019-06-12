/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.feature.SequenceGame;

/**
 * Game loop designed to handle our world.
 */
class Scene extends SequenceGame
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final Timing timing = new Timing();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE, services -> new World(services, false));
    }

    @Override
    public void load()
    {
        try
        {
            new World(services, true).saveToFile(Medias.create("world.lvl"));
        }
        catch (final LionEngineException exception)
        {
            assertEquals("Fail save", exception.getCause().getMessage());

            world.saveToFile(Medias.create("world.lvl"));
        }

        assertThrows(() -> new World(services, true).loadFromFile(Medias.create("world.lvl")),
                     "[world.lvl] Error on loading from file !");

        world.loadFromFile(Medias.create("world.lvl"));
        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (timing.elapsed(200L))
        {
            end();
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
