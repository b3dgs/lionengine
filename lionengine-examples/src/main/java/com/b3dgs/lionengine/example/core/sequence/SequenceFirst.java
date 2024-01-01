/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.core.sequence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * First sequence implementation.
 */
public final class SequenceFirst extends Sequence
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceFirst.class);

    /** Count value (increases to end). */
    private int count;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public SequenceFirst(Context context)
    {
        super(context, new Resolution(320, 100, 32));
    }

    @Override
    public void load()
    {
        count = 0;
    }

    @Override
    public void update(double extrp)
    {
        count++;
        if (count > 2)
        {
            end(SequenceNext.class);
        }
    }

    @Override
    public void render(Graphic g)
    {
        LOGGER.info("SequenceFirst rendering number ", String.valueOf(count));
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
