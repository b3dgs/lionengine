/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Updating of our controllable entity.
 */
class MarioUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final double GRAVITY = 10.0;

    private final Force movement = new Force();
    private final Force jump = new Force();

    @FeatureGet private Body body;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private TileCollidable tileCollidable;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     */
    public MarioUpdater(Services services)
    {
        super();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        movement.setVelocity(1.0);

        jump.setSensibility(0.1);
        jump.setVelocity(0.5);
        jump.setDestination(0.0, 0.0);

        body.setGravity(GRAVITY);
        body.setGravityMax(GRAVITY);
        body.setDesiredFps(Scene.NATIVE.getRate());
    }

    @Override
    public void update(double extrp)
    {
        movement.setDirection(3, 0);
        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        transformable.moveLocation(extrp, body, movement, jump);
        tileCollidable.update(extrp);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (Axis.X == category.getAxis())
        {
            tileCollidable.apply(result);
        }
        else if (Axis.Y == category.getAxis())
        {
            if (transformable.getY() < transformable.getOldY())
            {
                tileCollidable.apply(result);
                body.resetGravity();
                jump.setDirection(0.0, 10.0);
            }
        }
    }
}
