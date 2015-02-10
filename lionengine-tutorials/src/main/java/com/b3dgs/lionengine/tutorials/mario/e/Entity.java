/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.e;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.trait.Body;
import com.b3dgs.lionengine.game.trait.BodyModel;
import com.b3dgs.lionengine.game.trait.Collidable;
import com.b3dgs.lionengine.game.trait.CollidableModel;
import com.b3dgs.lionengine.game.trait.Mirrorable;
import com.b3dgs.lionengine.game.trait.MirrorableModel;
import com.b3dgs.lionengine.game.trait.TileCollidable;
import com.b3dgs.lionengine.game.trait.TileCollidableModel;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Entity
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Ground location y. */
    private static final int GROUND = 32;

    /** Transformable model. */
    protected final Transformable transformable;
    /** Tile collidable. */
    protected final TileCollidable tileCollidable;
    /** Collidable reference. */
    protected final Collidable collidable;
    /** State factory. */
    protected final StateFactory factory;
    /** Surface. */
    private final SpriteAnimated surface;
    /** Mirrorable model. */
    private final Mirrorable mirrorable;
    /** Body model. */
    private final Body body;
    /** Camera reference. */
    private final Camera camera;
    /** Movement force. */
    private final Force movement;
    /** Jump force. */
    private final Force jump;
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

        jump = new Force();
        movement = new Force();
        factory = new StateFactory();

        final Configurer configurer = setup.getConfigurer();
        transformable = new TransformableModel(this, configurer);
        addTrait(transformable);

        mirrorable = new MirrorableModel(this);
        addTrait(mirrorable);

        body = new BodyModel(this);
        addTrait(body);

        tileCollidable = new TileCollidableModel(this, setup.getConfigurer(), services);
        addTrait(tileCollidable);

        collidable = new CollidableModel(this, services);
        collidable.setOrigin(Origin.CENTER_TOP);
        collidable.addCollision(ConfigCollisions.create(setup.getConfigurer()).getCollision("default"));
        addTrait(collidable);

        camera = services.get(Camera.class);

        body.setVectors(movement, jump);
        body.setDesiredFps(services.get(Integer.class).intValue());
        body.setMass(2.0);

        final ConfigFrames frames = ConfigFrames.create(configurer);
        surface = Drawable.loadSpriteAnimated(setup.surface, frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        loadStates(configurer, factory);
        state = factory.getState(EntityState.IDLE);
    }

    /**
     * Respawn the entity.
     * 
     * @param x The horizontal location.
     */
    protected void respawn(int x)
    {
        transformable.teleport(x, GROUND);
        jump.setDirection(Direction.ZERO);
        body.resetGravity();
    }

    /**
     * Make the entity jump.
     */
    public void jump()
    {
        body.resetGravity();
        changeState(factory.getState(EntityState.JUMP));
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
     * Get the localizable reference.
     * 
     * @return The localizable reference.
     */
    public Localizable getLocalizable()
    {
        return transformable;
    }

    /**
     * Update the entity controls.
     */
    private void updateControl()
    {
        final State current = state.handleInput(factory, device);
        if (current != null)
        {
            state = current;
            current.enter();
        }
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
     * Load all existing animations defined in the xml file.
     * 
     * @param configurer The configurer reference.
     * @param factory The state factory reference.
     */
    private void loadStates(Configurer configurer, StateFactory factory)
    {
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
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
        surface.setLocation(camera, transformable.getX(), transformable.getY());
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
