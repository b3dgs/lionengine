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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;

/**
 * Mario description implementation.
 */
class Mario extends FeaturableModel
{
    /** Mario media. */
    public static final Media MEDIA = Medias.create("entity", "Mario.xml");

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Mario(Services services, Setup setup)
    {
        super();

        addFeature(new TransformableModel(setup));
        addFeature(new LayerableModel(1));
        addFeature(new MirrorableModel());
        addFeature(new BodyModel());
        addFeature(new TileCollidableModel(services, setup));

        final MarioModel model = new MarioModel(setup);
        addFeature(model);
        addFeature(new MarioUpdater(model));
        addFeature(new MarioRenderer(model));
    }
}
