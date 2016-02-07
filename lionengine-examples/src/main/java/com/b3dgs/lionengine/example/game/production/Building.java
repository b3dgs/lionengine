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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Renderable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.layer.LayerableModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.trait.producible.ProducibleListener;
import com.b3dgs.lionengine.game.object.trait.producible.ProducibleModel;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;

/**
 * Building implementation.
 */
class Building extends ObjectGame implements Updatable, Renderable, ProducibleListener
{
    /** Farm media reference. */
    public static final Media FARM = Medias.create("Farm.xml");
    /** Barracks media reference. */
    public static final Media BARRACKS = Medias.create("Barracks.xml");

    /** Transformable model. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Layerable model. */
    private final Layerable layerable = addTrait(new LayerableModel());
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Visible flag. */
    private boolean visible;

    /**
     * Create a building.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Building(SetupSurface setup, Services services)
    {
        super(setup, services);
        viewer = services.get(Viewer.class);

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 2, 1);
        surface.setOrigin(Origin.TOP_LEFT);

        layerable.setLayer(Integer.valueOf(1));

        addTrait(new ProducibleModel());
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    @Override
    public void render(Graphic g)
    {
        if (visible)
        {
            surface.render(g);
        }
    }

    @Override
    public void notifyProductionStarted()
    {
        surface.setLocation(viewer, transformable);
        visible = true;
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
