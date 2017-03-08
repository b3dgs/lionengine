/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision.it;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Rendering of our controllable entity.
 */
class MarioRenderer extends FeatureModel implements Displayable
{
    private final TextGame text = new TextGame("Arial", 12, TextStyle.NORMAL);
    private final SpriteAnimated surface;

    @Service private Collidable collidable;
    @Service private Transformable transformable;
    @Service private Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public MarioRenderer(Setup setup)
    {
        super();

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.BLACK);
        text.setText("Mario");
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        collidable.setCollisionVisibility(true);
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(camera, transformable);
        surface.render(g);

        text.update(camera);
        text.setLocation((int) transformable.getX(), (int) transformable.getY());
        g.setColor(ColorRgba.BLACK);
        text.draw(g, transformable, 0, 28, Align.CENTER, "Mario");
        text.render(g);

        g.setColor(ColorRgba.GREEN);
        collidable.render(g);
    }
}
