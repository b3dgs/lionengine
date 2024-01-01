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
package com.b3dgs.lionengine.editor.object.world.renderer;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.editor.object.world.ObjectControl;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

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

    @Override
    public void onRender(Graphic g, int width, int height, double scale, int tw, int th)
    {
        g.setColor(COLOR_ENTITY_SELECTION);

        for (final ObjectRepresentation object : handlerObject.get(ObjectRepresentation.class))
        {
            final Transformable transformable = object.getFeature(Transformable.class);
            if (objectControl.isOver(transformable) || objectControl.isSelected(transformable))
            {
                final Origin origin = object.getOrigin();
                final double x = camera.getViewpointX(origin.getX(transformable.getX(), transformable.getWidth()));
                final double y = camera.getViewpointY(origin.getY(transformable.getY(), -transformable.getHeight()));
                g.drawRect((int) (x * scale),
                           (int) (y * scale),
                           (int) (transformable.getWidth() * scale),
                           (int) (transformable.getHeight() * scale),
                           true);
            }
        }
    }
}
