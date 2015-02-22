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
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.trait.Collidable;
import com.b3dgs.lionengine.game.trait.CollidableListener;
import com.b3dgs.lionengine.game.trait.CollidableModel;
import com.b3dgs.lionengine.game.trait.Launchable;
import com.b3dgs.lionengine.game.trait.LaunchableModel;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Projectile implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Projectile
        extends ObjectGame
        implements Updatable, Renderable, CollidableListener
{
    /** Media. */
    public static final Media PULSE = Core.MEDIA.create("Pulse.xml");

    /** Projectile surface. */
    private final Sprite sprite;
    /** Transformable model. */
    private final Transformable transformable;
    /** Collidable model. */
    private final Collidable collidable;
    /** Launchable model. */
    private final Launchable launchable;
    /** Viewer reference. */
    private final Viewer viewer;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param context The context reference.
     */
    public Projectile(SetupSurface setup, Services context)
    {
        super(setup, context);
        viewer = context.get(Viewer.class);
        sprite = Drawable.loadSprite(setup.surface);
        sprite.setOrigin(Origin.MIDDLE);

        transformable = new TransformableModel(this, setup.getConfigurer());
        addTrait(transformable);

        collidable = new CollidableModel(this, context);
        collidable.setOrigin(Origin.MIDDLE);
        addTrait(collidable);

        launchable = new LaunchableModel(this);
        addTrait(launchable);

        collidable.addCollision(ConfigCollisions.create(setup.getConfigurer()).getCollision("default"));
    }

    @Override
    public void update(double extrp)
    {
        launchable.update(extrp);
        collidable.update(extrp);
        sprite.setLocation(viewer.getViewpointX(transformable.getX()), viewer.getViewpointY(transformable.getY()));
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
