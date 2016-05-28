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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Building implementation.
 */
class Building extends FeaturableModel implements ProducibleListener
{
    /** Farm media reference. */
    public static final Media FARM = Medias.create("Farm.xml");
    /** Barracks media reference. */
    public static final Media BARRACKS = Medias.create("Barracks.xml");

    private final Transformable transformable;
    private final SpriteAnimated surface;

    private boolean visible;

    @Service private Viewer viewer;

    /**
     * Create a building.
     * 
     * @param setup The setup reference.
     */
    public Building(SetupSurface setup)
    {
        super();

        transformable = addFeatureAndGet(new TransformableModel(setup));

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 2, 1);
        surface.setOrigin(Origin.TOP_LEFT);

        addFeature(new LayerableModel(1));
        addFeature(new ProducibleModel(setup));
        addFeature(new DisplayableModel(g ->
        {
            if (visible)
            {
                surface.render(g);
            }
        }));
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
