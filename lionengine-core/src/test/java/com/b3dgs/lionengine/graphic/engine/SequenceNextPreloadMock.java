/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Mock sequence.
 */
final class SequenceNextPreloadMock extends Sequence
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    SequenceNextPreloadMock(Context context)
    {
        super(context, new Resolution(640, 240, 60));
    }

    @Override
    public void load()
    {
        load(SequenceFailMock.class);
    }

    @Override
    public void update(double extrp)
    {
        end();
    }

    @Override
    public void render(Graphic g)
    {
        // Mock
    }
}
