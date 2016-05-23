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
package com.b3dgs.lionengine.game.collision.tile.it;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.handler.Displayable;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Layerable;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Rendering of our controllable entity.
 */
class MarioRenderer extends FeatureModel implements Displayable
{
    private final SpriteAnimated surface;

    @Service private Layerable layerable;
    @Service private Collidable collidable;
    @Service private Transformable transformable;
    @Service private Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public MarioRenderer(SetupSurface setup)
    {
        super();

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
    }

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        collidable.setCollisionVisibility(true);
        layerable.setLayer(1);
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(camera, transformable);
        surface.render(g);

        g.setColor(ColorRgba.GREEN);
        collidable.render(g);
    }
}
