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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.StateAnimationBased;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

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
    protected final Setup setup;

    private final Camera camera;

    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityUpdater(Services services, Setup setup)
    {
        super();

        this.setup = setup;
        camera = services.get(Camera.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        StateAnimationBased.Util.loadStates(EntityState.values(), factory, provider, setup);
        handler.changeState(EntityState.IDLE);
    }

    /**
     * Make the entity jump.
     */
    public void jump()
    {
        body.resetGravity();
        changeState(EntityState.JUMP);
        model.getJump().setDirection(0.0, 6.0);
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
        model.getJump().setDirection(Direction.ZERO);
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

        model.getMovement().update(extrp);
        model.getJump().update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        final SpriteAnimated surface = model.getSurface();
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
