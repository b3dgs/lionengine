/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;

/**
 * SequenceLinkComplex implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SequenceLinkComplex
        extends Sequence
{
    /** Count value. */
    private int count;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public SequenceLinkComplex(Loader loader)
    {
        super(loader, new Resolution(320, 100, 32));
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        count = 0;
    }

    @Override
    protected void update(double extrp)
    {
        count++;
        if (count == 1)
        {
            start(new SequenceNext(loader), false);
        }
        if (count > 2)
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        System.out.println("ComplexLink rendering number " + count);
    }
}
