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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Renderable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableModel;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.trait.body.Body;
import com.b3dgs.lionengine.game.object.trait.body.BodyModel;
import com.b3dgs.lionengine.game.object.trait.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.trait.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Implementation of our controllable entity.
 */
class Entity extends ObjectGame implements Updatable, Renderable, TileCollidableListener
{
    /** Ground location y. */
    private static final int GROUND = 32;

    /** Movement force. */
    public final Force movement = new Force();
    /** Jump force. */
    public final Force jump = new Force();
    /** Surface. */
    public final SpriteAnimated surface;
    /** Transformable model. */
    public final Transformable transformable = addTrait(new TransformableModel());
    /** Tile collidable. */
    protected final TileCollidable tileCollidable = addTrait(new TileCollidableModel());
    /** Collidable reference. */
    protected final Collidable collidable = addTrait(new CollidableModel());
    /** State factory. */
    protected final StateFactory factory = new StateFactory();
    /** Mirrorable model. */
    private final Mirrorable mirrorable = addTrait(new MirrorableModel());
    /** Body model. */
    private final Body body = addTrait(new BodyModel());
    /** State handler. */
    private final StateHandler handler = new StateHandler(factory);
    /** Camera reference. */
    private final Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Entity(SetupSurface setup, Services services)
    {
        super(setup, services);
        camera = services.get(Camera.class);
        collidable.setOrigin(Origin.CENTER_TOP);

        final ConfigFrames frames = ConfigFrames.create(getConfigurer());
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);
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
    protected void respawn(int x)
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
    protected void changeState(Enum<?> next)
    {
        handler.changeState(next);
    }

    /**
     * Set the device that will control the entity.
     * 
     * @param device The device controller.
     */
    protected void setControl(InputDeviceDirectional device)
    {
        handler.addInput(device);
    }

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(EntityState.values(), factory, this);
        handler.start(EntityState.IDLE);
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
    }

    @Override
    public void render(Graphic g)
    {
        surface.setLocation(camera, transformable);
        surface.render(g);
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
