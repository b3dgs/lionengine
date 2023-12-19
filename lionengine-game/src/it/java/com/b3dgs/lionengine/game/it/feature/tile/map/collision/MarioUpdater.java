/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
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
@FeatureInterface
public final class MarioUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final double GRAVITY = 3.5;

    private final Body body;
    private final Transformable transformable;
    private final TileCollidable tileCollidable;

    private final Force movement = new Force();
    private final Force jump = new Force();

    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param body The body feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param tileCollidable The tile collidable feature.
     */
    public MarioUpdater(Services services,
                        Setup setup,
                        Body body,
                        Transformable transformable,
                        Collidable collidable,
                        TileCollidable tileCollidable)
    {
        super(services, setup);

        this.body = body;
        this.transformable = transformable;
        this.tileCollidable = tileCollidable;

        movement.setVelocity(1.0);

        jump.setSensibility(0.1);
        jump.setVelocity(0.5);
        jump.setDestination(0.0, 0.0);

        body.setGravity(GRAVITY);
        body.setGravityMax(GRAVITY);
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
        else if (Axis.Y == category.getAxis() && transformable.getY() < transformable.getOldY())
        {
            tileCollidable.apply(result);
            body.resetGravity();
            jump.setDirection(0.0, 10.0);
        }
    }
}
