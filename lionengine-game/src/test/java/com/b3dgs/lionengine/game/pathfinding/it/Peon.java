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
package com.b3dgs.lionengine.game.pathfinding.it;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.identifiable.IdentifiableModel;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel implements Updatable, Renderable
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    private final Timing timing = new Timing();
    private final Transformable transformable;
    private final Pathfindable pathfindable;
    private final SpriteAnimated surface;

    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        addFeature(new IdentifiableModel());
        transformable = addFeatureAndGet(new TransformableModel(setup));
        pathfindable = addFeatureAndGet(new PathfindableModel(setup));

        transformable.teleport(320, 256);

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        timing.start();
    }

    @Override
    public void prepareFeatures(Featurable owner, Services services)
    {
        super.prepareFeatures(owner, services);

        pathfindable.setSpeed(5.0, 5.0);
        pathfindable.setDestination(28, 8);
    }

    @Override
    public void update(double extrp)
    {
        pathfindable.update(extrp);
        surface.setLocation(viewer, transformable);

        if (timing.elapsed(500L))
        {
            pathfindable.setDestination(12, 12);
            timing.stop();
        }
    }

    @Override
    public void render(Graphic g)
    {
        pathfindable.render(g);
        surface.render(g);
    }
}
