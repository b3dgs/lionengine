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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableModel;
import com.b3dgs.lionengine.game.layer.LayerableModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.body.BodyModel;
import com.b3dgs.lionengine.game.object.feature.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;

/**
 * Entity description implementation.
 */
class Entity extends ObjectGame
{
    /** Mario media. */
    public static final Media MARIO = Medias.create("entity", "Mario.xml");
    /** Goomba media. */
    public static final Media GOOMBA = Medias.create("entity", "Goomba.xml");
    /** Updater node. */
    private static final String NODE_UPDATER = Configurer.PREFIX + "updater";

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Entity(SetupSurface setup)
    {
        super(setup);

        addFeature(new TransformableModel(setup));
        addFeature(new LayerableModel());
        addFeature(new MirrorableModel());
        addFeature(new BodyModel());
        addFeature(new CollidableModel(setup));
        addFeature(new TileCollidableModel(setup));

        final EntityModel model = new EntityModel(setup);
        addFeature(model);

        final Configurer configurer = setup.getConfigurer();
        addFeature(configurer.getImplementation(EntityUpdater.class, EntityModel.class, model, NODE_UPDATER));

        addFeature(new EntityRenderer(model));
    }
}
