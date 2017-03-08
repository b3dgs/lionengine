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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.io.awt.Keyboard;

/**
 * Mario updating implementation.
 */
class MarioUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final int GROUND = 32;

    private final StateFactory factory = new StateFactory();
    private final StateHandler handler = new StateHandler(factory);
    private final Force movement;
    private final Force jump;
    private final SpriteAnimated surface;
    private final Setup setup;

    @Service private Mirrorable mirrorable;
    @Service private Transformable transformable;
    @Service private Body body;
    @Service private TileCollidable tileCollidable;

    @Service private Camera camera;
    @Service private Keyboard keyboard;

    /**
     * Constructor.
     * 
     * @param model The mario model.
     */
    public MarioUpdater(MarioModel model)
    {
        super();

        movement = model.getMovement();
        jump = model.getJump();
        surface = model.getSurface();
        setup = model.getSetup();
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        StateAnimationBased.Util.loadStates(MarioState.values(), factory, provider, setup);
        handler.changeState(MarioState.IDLE);
        handler.addInput(keyboard);

        respawn();
    }

    /**
     * Respawn the mario.
     */
    public void respawn()
    {
        transformable.teleport(400, GROUND);
        camera.resetInterval(transformable);
        jump.setDirection(Direction.ZERO);
        body.resetGravity();
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        mirrorable.update(extrp);
        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            respawn();
        }

        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
        surface.setLocation(camera, transformable);
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            body.resetGravity();
        }
    }
}
