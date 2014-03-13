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
package com.b3dgs.lionengine.example.game.cursor;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Cursor. */
    private final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        cursor = new Cursor(mouse, source, UtilityMedia.get("cursor", "cursor1.png"), UtilityMedia.get("cursor",
                "cursor2.png"));
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        cursor.setSensibility(0.5, 0.5);
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }

        if (mouse.hasClickedOnce(Click.LEFT))
        {
            cursor.setSurfaceId(0);
        }
        if (mouse.hasClickedOnce(Click.RIGHT))
        {
            cursor.setSurfaceId(1);
        }
        if (mouse.hasClickedOnce(Click.MIDDLE))
        {
            cursor.setSyncMode(!cursor.isSynchronized());
        }

        cursor.update(extrp);
        if (!cursor.isSynchronized())
        {
            mouse.lock();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(source);
        cursor.render(g);
    }
}
