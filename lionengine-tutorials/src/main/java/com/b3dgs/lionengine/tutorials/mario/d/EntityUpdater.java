/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Refreshable;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.feature.body.Body;
import com.b3dgs.lionengine.game.object.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Entity updating implementation.
 */
class EntityUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final int GROUND = 32;

    /** State factory. */
    protected final StateFactory factory = new StateFactory();
    /** State handler. */
    protected final StateHandler handler = new StateHandler(factory);
    /** Entity configurer. */
    protected final Configurer configurer;

    private final Force movement;
    private final Force jump;
    private final SpriteAnimated surface;

    @Service private Transformable transformable;
    @Service private TileCollidable tileCollidable;
    @Service private Collidable collidable;
    @Service private Mirrorable mirrorable;
    @Service private Body body;

    @Service private Camera camera;

    /**
     * Constructor.
     * 
     * @param model The model reference.
     */
    public EntityUpdater(EntityModel model)
    {
        super();

        movement = model.getMovement();
        jump = model.getJump();
        surface = model.getSurface();
        configurer = model.getConfigurer();
    }

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        StateAnimationBased.Util.loadStates(EntityState.values(), factory, owner, configurer);
        handler.changeState(EntityState.IDLE);

        tileCollidable.addListener(this);
    }

    /**
     * Make the entity jump.
     */
    public void jump()
    {
        body.resetGravity();
        changeState(EntityState.JUMP);
        jump.setDirection(0.0, 6.0);
    }

    /**
     * Check the current entity state.
     * 
     * @param state The state to check.
     * @return <code>true</code> if it is this state, <code>false</code> else.
     */
    public boolean isState(Enum<? extends StateAnimationBased> state)
    {
        return handler.isState(state);
    }

    /**
     * Respawn the entity.
     * 
     * @param x The horizontal location.
     */
    public void respawn(int x)
    {
        mirrorable.mirror(Mirror.NONE);
        transformable.teleport(x, GROUND);
        jump.setDirection(Direction.ZERO);
        body.resetGravity();
        collidable.setEnabled(true);
        tileCollidable.setEnabled(true);
        handler.changeState(EntityState.IDLE);
    }

    /**
     * Change the current state.
     * 
     * @param next The next state.
     */
    public void changeState(Enum<?> next)
    {
        handler.changeState(next);
    }

    /**
     * Set the device that will control the entity.
     * 
     * @param device The device controller.
     */
    public void setControl(InputDeviceDirectional device)
    {
        handler.addInput(device);
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
        collidable.update(extrp);

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
