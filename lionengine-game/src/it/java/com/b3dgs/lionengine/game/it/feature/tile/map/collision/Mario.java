/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;

/**
 * Implementation of our controllable entity.
 */
final class Mario extends FeaturableModel
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Mario.xml");

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Mario(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new TransformableModel(services, setup));
        addFeature(new BodyModel(services, setup));
        addFeatureAndGet(new LayerableModel(1));
        addFeatureAndGet(new CollidableModel(services, setup)).setOrigin(Origin.CENTER_BOTTOM);
        addFeature(new TileCollidableModel(services, setup));
        addFeature(new MarioUpdater(services, setup));
        addFeature(new MarioRenderer(services, setup));
    }
}
