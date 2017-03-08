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
package com.b3dgs.lionengine.game.feature.it;

import org.junit.Assert;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Game loop designed to handle our world.
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final Timing timing = new Timing();
    private final World world;
    private final Context context;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        this.context = context;
        world = new World(context, false);
    }

    @Override
    public void load()
    {
        try
        {
            new World(context, true).saveToFile(Medias.create("world.lvl"));
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Fail save", exception.getCause().getMessage());

            world.saveToFile(Medias.create("world.lvl"));
        }
        try
        {
            new World(context, true).loadFromFile(Medias.create("world.lvl"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals("Fail load", exception.getCause().getMessage());

            world.loadFromFile(Medias.create("world.lvl"));
        }
        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        world.update(extrp);
        if (timing.elapsed(200L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        world.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
