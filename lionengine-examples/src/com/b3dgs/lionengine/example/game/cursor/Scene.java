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
package com.b3dgs.lionengine.example.game.cursor;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.minimal
 */
public class Scene
        extends Sequence
{
    /** Cursor. */
    private final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        cursor = new Cursor(mouse, source, Media.get("cursor", "cursor1.png"), Media.get("cursor", "cursor2.png"));
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        // Nothing to do
    }

    @Override
    protected void update(double extrp)
    {
        if (mouse.hasClickedOnce(Click.LEFT))
        {
            cursor.setSurfaceId(0);
        }
        if (mouse.hasClickedOnce(Click.RIGHT))
        {
            cursor.setSurfaceId(1);
        }
        cursor.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(source);
        cursor.render(g);
    }
}
