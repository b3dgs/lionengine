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

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.collidable.CollidableListener;
import com.b3dgs.lionengine.game.trait.collidable.CollidableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Ship implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Ship
        extends ObjectGame
        implements Updatable, Renderable, CollidableListener
{
    /** Media. */
    public static final Media MEDIA = Core.MEDIA.create("Ship.xml");

    /** Surface. */
    private final SpriteAnimated sprite;
    /** Transformable model. */
    private final Transformable transformable;
    /** Collidable model. */
    private final Collidable collidable;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Weapon model. */
    private final Weapon weapon;
    /** Speed. */
    private final double speed;
    /** Target used. */
    private Localizable target;
    /** Location ship. */
    private double location;
    /** Start x. */
    private int x;
    /** Start y. */
    private int y;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param context The context reference.
     */
    public Ship(SetupSurface setup, Services context)
    {
        super(setup, context);

        viewer = context.get(Viewer.class);
        transformable = new TransformableModel(this, setup.getConfigurer());
        addTrait(transformable);

        collidable = new CollidableModel(this, setup.getConfigurer(), context);
        collidable.setOrigin(Origin.MIDDLE);
        collidable.addListener(this);
        addTrait(collidable);

        final ConfigFrames config = ConfigFrames.create(setup.getConfigurer());
        sprite = Drawable.loadSpriteAnimated(setup.surface, config.getHorizontal(), config.getVertical());

        sprite.setFrame(3);
        sprite.setOrigin(Origin.MIDDLE);

        final Factory factory = context.get(Factory.class);
        final Handler handler = context.get(Handler.class);

        weapon = factory.create(Weapon.PULSE_CANNON);
        weapon.setOwner(this);
        weapon.setOffset(6, -6);
        handler.add(weapon);

        x = 64;
        y = 192;
        speed = UtilRandom.getRandomDouble() / 1.5 + 0.75;
        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
        weapon.update(1.0);
    }

    /**
     * Mirror the object.
     */
    public void mirror()
    {
        sprite.setMirror(Mirror.VERTICAL);
        weapon.setOffset(0, 0);
        final int old = x;
        x = y;
        y = old;
        transformable.teleport(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
    }

    /**
     * Set the target to seek.
     * 
     * @param target The target to seek.
     */
    public void setTarget(Ship target)
    {
        this.target = target.transformable;
    }

    /**
     * Point the defined target by computing the angle.
     */
    private void pointTarget()
    {
        final int angle = (int) StrictMath.toDegrees(Math.atan((transformable.getX() - target.getX())
                / (target.getY() - transformable.getY())));
        if (sprite.getMirror() == Mirror.NONE)
        {
            sprite.rotate(-angle + 180);
        }
        else
        {
            sprite.rotate(angle + 180);
        }
    }

    @Override
    public void update(double extrp)
    {
        pointTarget();
        location += speed;
        transformable.setLocation(x + UtilMath.cos(location * 1.5) * 60, y + UtilMath.sin(location * 2) * 30);
        sprite.setLocation(viewer, transformable);
        weapon.fire(target);
        collidable.update(extrp);
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
        collidable.getOwner().destroy();
    }
}
