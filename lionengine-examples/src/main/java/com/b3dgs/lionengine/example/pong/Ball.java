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
package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.collidable.CollidableListener;
import com.b3dgs.lionengine.game.trait.collidable.CollidableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Ball implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Ball
        extends ObjectGame
        implements Updatable, Renderable, CollidableListener
{
    /** Racket media. */
    public static final Media MEDIA = Core.MEDIA.create("Ball.xml");
    /** Ball color. */
    private static final ColorRgba COLOR = ColorRgba.GRAY;

    /** Current force. */
    private final Force force;
    /** Speed. */
    private final double speed;
    /** Transformable model. */
    private Transformable transformable;
    /** Collidable model. */
    private Collidable collidable;

    /**
     * {@link ObjectGame#ObjectGame(Setup, Services)}
     */
    public Ball(Setup setup, Services services) throws LionEngineException
    {
        super(setup, services);

        speed = 2.0;
        force = new Force(-speed, 0.0);
        force.setDestination(-speed, 0.0);
        force.setVelocity(speed);

        addTrait(TransformableModel.class);
        addTrait(CollidableModel.class);
    }

    @Override
    protected void prepareTraits()
    {
        transformable = getTrait(Transformable.class);
        transformable.teleport(320 / 2 - transformable.getWidth() / 2, 230 / 2 - transformable.getHeight() / 2);

        collidable = getTrait(Collidable.class);
        collidable.setCollisionVisibility(true);
    }

    @Override
    public void update(double extrp)
    {
        force.update(extrp);
        transformable.moveLocation(extrp, force);
        collidable.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(COLOR);
        g.drawOval((int) transformable.getX(), (int) transformable.getY(), transformable.getWidth(),
                transformable.getHeight(), true);
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        final Transformable object = collidable.getOwner().getTrait(Transformable.class);
        int side = 0;
        if (transformable.getX() < transformable.getOldX())
        {
            transformable.teleportX(object.getX() + object.getWidth() + 1);
            side = 1;
        }
        if (transformable.getX() > transformable.getOldX())
        {
            transformable.teleportX(object.getX() - object.getWidth() * 2 - 1);
            side = -1;
        }

        final double diff = (object.getY() - transformable.getY()) / object.getHeight() / 2;
        final int angle = (int) Math.round(diff * 180);
        force.setDestination(speed * UtilMath.cos(angle) * side, speed * UtilMath.sin(angle));
    }
}
