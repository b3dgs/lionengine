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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.collidable.CollidableListener;
import com.b3dgs.lionengine.game.trait.collidable.CollidableModel;
import com.b3dgs.lionengine.game.trait.launchable.Launchable;
import com.b3dgs.lionengine.game.trait.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Projectile implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Projectile extends ObjectGame implements Updatable, Renderable, CollidableListener
{
    /** Media. */
    public static final Media PULSE = Medias.create("Pulse.xml");

    /** Transformable model. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Collidable model. */
    private final Collidable collidable = addTrait(new CollidableModel());
    /** Launchable model. */
    private final Launchable launchable = addTrait(new LaunchableModel());
    /** Projectile surface. */
    private final Sprite sprite;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Projectile(SetupSurface setup, Services services)
    {
        super(setup, services);
        viewer = services.get(Viewer.class);

        sprite = Drawable.loadSprite(setup.getSurface());
        sprite.setOrigin(Origin.MIDDLE);

        collidable.setOrigin(Origin.MIDDLE);
    }

    @Override
    public void update(double extrp)
    {
        launchable.update(extrp);
        collidable.update(extrp);
        sprite.setLocation(viewer, transformable);
        if (!viewer.isViewable(transformable, 0, 0))
        {
            destroy();
        }
    }

    @Override
    public void render(Graphic g)
    {
        sprite.render(g);
        collidable.render(g);
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        // Nothing to do
    }
}
