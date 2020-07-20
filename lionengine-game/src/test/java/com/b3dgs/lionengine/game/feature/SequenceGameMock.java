/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Sequence game mock.
 */
final class SequenceGameMock extends SequenceGame
{
    /**
     * Mock.
     * 
     * @param context The context reference.
     */
    public SequenceGameMock(Context context)
    {
        super(context, services -> new WorldGame(services)
        {
            @Override
            protected void saving(FileWriting file) throws IOException
            {
                // Mock
            }

            @Override
            protected void loading(FileReading file) throws IOException
            {
                // Mock
            }
        });
    }

    @Override
    public void load()
    {
        // Mock
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        services.get(Sequencer.class).end();
    }
}
