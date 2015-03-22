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
package com.b3dgs.lionengine.example.game.extraction;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.extractable.Extractable;
import com.b3dgs.lionengine.game.trait.extractable.ExtractableModel;
import com.b3dgs.lionengine.game.trait.layerable.Layerable;
import com.b3dgs.lionengine.game.trait.layerable.LayerableModel;
import com.b3dgs.lionengine.game.trait.pathfindable.Pathfindable;
import com.b3dgs.lionengine.game.trait.pathfindable.PathfindableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Building implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class GoldMine
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Gold mine media reference. */
    public static final Media GOLD_MINE = Core.MEDIA.create("GoldMine.xml");

    /** Surface reference. */
    private final Sprite surface;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Transformable model. */
    private Transformable transformable;

    /**
     * Create a building.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public GoldMine(SetupSurface setup, Services services)
    {
        super(setup, services);

        surface = Drawable.loadSprite(setup.surface);
        surface.setOrigin(Origin.TOP_LEFT);

        viewer = services.get(Viewer.class);

        addTrait(TransformableModel.class);
        addTrait(ExtractableModel.class);
        addTrait(PathfindableModel.class);
        addTrait(LayerableModel.class);
    }

    @Override
    protected void prepareTraits()
    {
        transformable = getTrait(Transformable.class);

        final Pathfindable pathfindable = getTrait(Pathfindable.class);
        pathfindable.setLocation(21, 14);

        final Extractable extractable = getTrait(Extractable.class);
        extractable.setResourcesQuantity(100);

        final Layerable layerable = getTrait(Layerable.class);
        layerable.setLayer(Integer.valueOf(1));
    }

    @Override
    public void update(double extrp)
    {
        surface.setLocation(viewer, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
