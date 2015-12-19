/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world.renderer;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.editor.world.ObjectControl;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;

/**
 * Handle the objects selection rendering.
 */
public class WorldSelectedObjects implements WorldRenderListener
{
    /** Color of the box around the selected object. */
    private static final ColorRgba COLOR_ENTITY_SELECTION = new ColorRgba(128, 240, 128, 192);

    /** Camera reference. */
    private final Camera camera;
    /** Handler object. */
    private final Handler handlerObject;
    /** Object controller. */
    private final ObjectControl objectControl;

    /**
     * Create the renderer.
     * 
     * @param services The services reference.
     */
    public WorldSelectedObjects(Services services)
    {
        camera = services.get(Camera.class);
        handlerObject = services.get(Handler.class);
        objectControl = services.get(ObjectControl.class);
    }

    /*
     * WorldRenderListener
     */

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        g.setColor(COLOR_ENTITY_SELECTION);

        for (final Transformable object : handlerObject.get(Transformable.class))
        {
            if (objectControl.isOver(object.getOwner()) || objectControl.isSelected(object.getOwner()))
            {
                final int x = (int) (camera.getViewpointX(object.getX()) * scale);
                final int y = (int) ((camera.getViewpointY(object.getY()) - object.getHeight()) * scale);
                g.drawRect(x, y, object.getWidth(), object.getHeight(), true);
            }
        }
    }
}
