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
package com.b3dgs.lionengine.game.it.feature.tile.map.pathfinding;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    /**
     * Create a peon.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Peon(Services services, Setup setup)
    {
        super();

        addFeature(new LayerableModel(1));

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        transformable.teleport(208, 224);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.setFrameOffsets(8, 8);

        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));
        pathfindable.setSpeed(6.0, 6.0);

        final Viewer viewer = services.get(Viewer.class);
        final Timing timing = new Timing();

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            surface.setLocation(viewer, transformable);

            if (timing.elapsed(500L))
            {
                pathfindable.setDestination(18, 14);
                timing.stop();
            }
        }));

        addFeature(new DisplayableModel(g ->
        {
            pathfindable.render(g);
            surface.render(g);
        }));

        timing.start();
    }
}
