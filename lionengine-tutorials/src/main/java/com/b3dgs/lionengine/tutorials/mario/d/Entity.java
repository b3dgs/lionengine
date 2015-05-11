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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.body.Body;
import com.b3dgs.lionengine.game.trait.body.BodyModel;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.collidable.CollidableModel;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidable;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidableListener;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidableModel;
import com.b3dgs.lionengine.game.trait.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.trait.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Entity
        extends ObjectGame
        implements Updatable, Renderable, TileCollidableListener
{
    /** Ground location y. */
    private static final int GROUND = 32;

    /** State factory. */
    protected final StateFactory factory = new StateFactory();
    /** Movement force. */
    private final Force movement = new Force();
    /** Jump force. */
    private final Force jump = new Force();
    /** Transformable model. */
    protected final Transformable transformable = addTrait(new TransformableModel());
    /** Tile collidable. */
    protected final TileCollidable tileCollidable = addTrait(new TileCollidableModel());
    /** Collidable reference. */
    protected final Collidable collidable = addTrait(new CollidableModel());
    /** Mirrorable model. */
    private final Mirrorable mirrorable = addTrait(new MirrorableModel());
    /** Body model. */
    private final Body body = addTrait(new BodyModel());
    /** Surface. */
    private final SpriteAnimated surface;
    /** Camera reference. */
    private final Camera camera;
    /** Controller reference. */
    private InputDeviceDirectional device;
    /** Entity state. */
    private State state;

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
        surface = Drawable.loadSpriteAnimated(setup.surface, frames.getHorizontal(), frames.getVertical());
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
        changeState(factory.getState(EntityState.JUMP));
        jump.setDirection(0.0, 6.0);
    }

    /**
     * Get the localizable reference.
     * 
     * @return The localizable reference.
     */
    public Localizable getLocalizable()
    {
        return transformable;
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the surface.
     * 
     * @return The surface reference.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }

    /**
     * Check the current entity state.
     * 
     * @param state The state to check.
     * @return <code>true</code> if it is this state, <code>false</code> else.
     */
    public boolean isState(EntityState state)
    {
        return this.state.getState() == state;
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
        state = factory.getState(EntityState.IDLE);
    }

    /**
     * Change the current state.
     * 
     * @param next The next state.
     */
    protected void changeState(State next)
    {
        state = next;
        state.enter();
    }

    /**
     * Set the device that will control the entity.
     * 
     * @param device The device controller.
     */
    protected void setControl(InputDeviceDirectional device)
    {
        this.device = device;
    }

    /**
     * Update the entity controls.
     */
    private void updateControl()
    {
        if (device != null)
        {
            final State current = state.handleInput(factory, device);
            if (current != null)
            {
                state = current;
                current.enter();
            }
        }
    }

    /**
     * Load all existing animations defined in the xml file.
     */
    private void loadStates()
    {
        final ConfigAnimations configAnimations = ConfigAnimations.create(getConfigurer());
        for (final EntityState type : EntityState.values())
        {
            try
            {
                final Animation animation = configAnimations.getAnimation(type.getAnimationName());
                final State state = type.create(this, animation);
                factory.addState(type, state);
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    @Override
    protected void onPrepared()
    {
        loadStates();
        state = factory.getState(EntityState.IDLE);
    }

    @Override
    public void update(double extrp)
    {
        updateControl();
        state.update(extrp);
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
