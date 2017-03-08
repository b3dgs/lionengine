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
package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Racket implementation.
 */
class Racket extends FeaturableModel implements Updatable, Renderable, CollidableListener
{
    /** Racket media. */
    public static final Media MEDIA = Medias.create("Racket.xml");
    /** Racket color. */
    private static final ColorRgba COLOR = ColorRgba.YELLOW;

    /** Transformable model. */
    private final Transformable transformable;
    /** Collidable model. */
    private final Collidable collidable;
    /** Move speed. */
    private final double speed;
    /** Ball reference. */
    private Transformable target;

    @Service private Viewer viewer;

    /**
     * Create an object.
     * 
     * @param setup The setup reference.
     */
    public Racket(Setup setup)
    {
        super();

        transformable = addFeatureAndGet(new TransformableModel(setup));
        collidable = addFeatureAndGet(new CollidableModel(setup));
        collidable.setOrigin(Origin.MIDDLE);

        transformable.teleportY(240 / 2);

        speed = UtilRandom.getRandomDouble() + 2.0;
    }

    /**
     * Set the racket side on screen.
     * 
     * @param left <code>true</code> for left side, <code>false</code> for right side.
     */
    public void setSide(boolean left)
    {
        if (left)
        {
            transformable.teleportX(transformable.getWidth() / 2);
        }
        else
        {
            transformable.teleportX(320 - transformable.getWidth() / 2);
        }
    }

    /**
     * Set the ball reference.
     * 
     * @param ball The ball reference.
     */
    public void setBall(Ball ball)
    {
        target = ball.getFeature(Transformable.class);
    }

    @Override
    public void update(double extrp)
    {
        final double diffY = target.getY() - transformable.getY();
        if (diffY < 0)
        {
            transformable.moveLocation(extrp, 0.0, -speed);
        }
        else if (diffY > 0)
        {
            transformable.moveLocation(extrp, 0.0, speed);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(COLOR);
        g.drawRect(viewer,
                   Origin.MIDDLE,
                   (int) transformable.getX(),
                   (int) transformable.getY(),
                   transformable.getWidth(),
                   transformable.getHeight(),
                   true);
        collidable.render(g);
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        // Nothing to do
    }
}
