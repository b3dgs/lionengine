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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;

/**
 * Mario description implementation.
 */
class Mario extends FeaturableModel
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Mario.xml");

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Mario(SetupSurface setup)
    {
        super();

        addFeature(new TransformableModel());
        addFeature(new BodyModel());
        addFeature(new CollidableModel(setup));
        addFeature(new TileCollidableModel(setup));

        final MarioModel model = new MarioModel(setup);
        addFeature(new MarioController(model));
        addFeature(new MarioUpdater(model));
        addFeature(new MarioRenderer(model));
        addFeature(new LayerableModel(1));
    }
}
