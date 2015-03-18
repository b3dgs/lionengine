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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.object.ComponentRendererLayer;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.layerable.Layerable;
import com.b3dgs.lionengine.game.trait.layerable.LayerableModel;
import com.b3dgs.lionengine.game.trait.producible.Producible;
import com.b3dgs.lionengine.game.trait.producible.ProducibleListener;
import com.b3dgs.lionengine.game.trait.producible.ProducibleModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Building implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Building
        extends ObjectGame
        implements Updatable, Renderable, ProducibleListener
{
    /** Farm media reference. */
    public static final Media FARM = Core.MEDIA.create("Farm.xml");
    /** Barracks media reference. */
    public static final Media BARRACKS = Core.MEDIA.create("Barracks.xml");

    /** Transformable model. */
    private final Transformable transformable;
    /** Producible model. */
    private final Producible producible;
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Create a building.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Building(SetupSurface setup, Services services)
    {
        super(setup, services);

        transformable = new TransformableModel(this, setup.getConfigurer());
        addTrait(transformable);

        producible = new ProducibleModel(this, setup.getConfigurer());
        addTrait(producible);
        producible.addListener(this);

        final Layerable layerable = new LayerableModel(this);
        addTrait(layerable);
        layerable.setLayer(Integer.valueOf(1));
        layerable.addListener(services.get(ComponentRendererLayer.class));

        viewer = services.get(Viewer.class);

        surface = Drawable.loadSpriteAnimated(setup.surface, 2, 1);
        surface.setOrigin(Origin.TOP_LEFT);
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

    @Override
    public void notifyProductionStarted()
    {
        // Nothing to do
    }

    @Override
    public void notifyProductionProgress()
    {
        // Nothing to do
    }

    @Override
    public void notifyProductionEnded()
    {
        surface.setFrame(2);
    }
}
