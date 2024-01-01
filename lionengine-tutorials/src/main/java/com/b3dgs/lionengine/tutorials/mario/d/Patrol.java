/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.DeviceControllerVoid;

/**
 * Patrol specific implementation.
 */
@FeatureInterface
public final class Patrol extends FeatureModel
                          implements Routine, CollidableListener, TileCollidableListener, Recyclable
{
    private static final double SPEED_X = 0.25;
    private static final long DEAD_TICK = 30L;

    private final Identifiable identifiable;
    private final Transformable transformable;
    private final Collidable collidable;
    private final EntityModel model;

    private final Tick deadTick = new Tick();

    private double side = SPEED_X;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param identifiable The identifiable feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param model The model feature.
     */
    public Patrol(Services services,
                  Setup setup,
                  Identifiable identifiable,
                  Transformable transformable,
                  Collidable collidable,
                  EntityModel model)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.transformable = transformable;
        this.collidable = collidable;
        this.model = model;

        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return side;
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        deadTick.update(extrp);
        if (deadTick.elapsed(DEAD_TICK))
        {
            identifiable.destroy();
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (Axis.X == category.getAxis())
        {
            if (category.getName().startsWith("right"))
            {
                side = -SPEED_X;
            }
            else if (category.getName().startsWith("left"))
            {
                side = SPEED_X;
            }
        }
    }

    @Override
    public void notifyCollided(Collidable other, Collision with, Collision by)
    {
        final Transformable collider = other.getFeature(Transformable.class);
        if (collider.getY() < collider.getOldY() && collider.getY() > transformable.getY())
        {
            collider.teleportY(transformable.getY() + transformable.getHeight());
            collidable.setEnabled(false);
            model.getLife().decrease(1);
            Sfx.CRUSH.play();
            deadTick.start();
        }
    }

    @Override
    public void recycle()
    {
        collidable.setEnabled(true);
        model.getLife().fill();
        deadTick.stop();
    }
}
