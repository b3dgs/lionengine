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
package com.b3dgs.lionengine.example.game.fog;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.map.feature.fog.Fovable;
import com.b3dgs.lionengine.game.map.feature.fog.FovableModel;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel
{
    /** Setup reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        final Layerable layerable = addFeatureAndGet(new LayerableModel());
        layerable.setLayer(1);

        final Fovable fovable = addFeatureAndGet(new FovableModel());
        fovable.setFov(3);

        final Timing timing = new Timing();
        timing.start();

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        transformable.teleport(64, 64);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        addFeature(new RefreshableModel(extrp ->
        {
            surface.setLocation(viewer, transformable);
            if (timing.elapsed(1000))
            {
                transformable.teleport(UtilRandom.getRandomInteger(19) * 16, UtilRandom.getRandomInteger(14) * 16);
                timing.restart();
            }
        }));

        addFeature(new DisplayableModel(surface::render));
    }
}
