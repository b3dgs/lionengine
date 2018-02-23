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
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.awt.KeyboardAwt;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * Updating of our controllable entity.
 */
class MarioUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final double GRAVITY = 6.0;

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final Context context;
    private final Keyboard keyboard;

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
        context = services.get(Context.class);
        keyboard = services.get(Keyboard.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setOrigin(Origin.CENTER_BOTTOM);

        jump.setVelocity(0.1);
        jump.setDestination(0.0, 0.0);
        transformable.teleport(350, 32);

        body.setDesiredFps(context.getConfig().getSource().getRate());
        body.setGravity(GRAVITY);
        body.setVectors(movement, jump);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDirection(2, 0);
        jump.setDirection(0.0, 8.0);
        if (keyboard.isPressed(KeyboardAwt.LEFT))
        {
            movement.setDirection(-2, 0);
        }
        if (keyboard.isPressed(KeyboardAwt.RIGHT))
        {
            movement.setDirection(2, 0);
        }
        if (keyboard.isPressedOnce(KeyboardAwt.UP))
        {
            jump.setDirection(0.0, 8.0);
        }

        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            transformable.teleportY(80);
            body.resetGravity();
        }
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis)
        {
            body.resetGravity();
        }
    }
}
