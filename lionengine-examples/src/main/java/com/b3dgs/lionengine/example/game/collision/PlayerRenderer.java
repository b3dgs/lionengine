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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Sprite;

/**
 * Player rendering implementation.
 */
class PlayerRenderer extends FeatureModel implements Displayable
{
    private final Sprite surface;
    private final Viewer viewer;

    @FeatureGet private Transformable transformable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public PlayerRenderer(Services services, PlayerModel model)
    {
        super();

        viewer = services.get(Viewer.class);
        surface = model.getSurface();
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(viewer, transformable);
        surface.render(g);
    }
}
