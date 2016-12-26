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
package com.b3dgs.lionengine.example.core.sequence;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * First sequence implementation.
 */
class SequenceFirst extends Sequence
{
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
        Verbose.info("SequenceFirst rendering number ", String.valueOf(count));
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
