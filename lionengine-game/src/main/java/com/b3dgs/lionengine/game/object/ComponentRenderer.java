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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Renderable;

/**
 * Renderer component implementation which render {@link Renderable} objects.
 */
public class ComponentRenderer implements ComponentRenderable
{
    /**
     * Create a renderer component.
     */
    public ComponentRenderer()
    {
        super();
    }

    /*
     * ComponentRenderable
     */

    @Override
    public void render(Graphic g, HandledObjects objects)
    {
        for (final Renderable renderable : objects.get(Renderable.class))
        {
            renderable.render(g);
        }
    }
}
