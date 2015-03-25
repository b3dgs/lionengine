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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.body.Body;
import com.b3dgs.lionengine.game.trait.body.BodyModel;
import com.b3dgs.lionengine.game.trait.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.trait.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Mario
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Mario.xml");
    /** Ground location y. */
    static final int GROUND = 32;

    /** Surface. */
    private final SpriteAnimated surface;
    /** Camera reference. */
    private final Camera camera;
    /** State factory. */
    private final StateFactory factory = new StateFactory();
    /** Movement force. */
    private final Force movement = new Force();
    /** Jump force. */
    private final Force jump = new Force();
    /** Mirrorable model. */
    private Mirrorable mirrorable;
    /** Transformable model. */
    private Transformable transformable;
    /** Body model. */
    private Body body;
    /** Entity state. */
    private State state;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Mario(SetupSurface setup, Services services)
    {
        super(setup, services);

        surface = Drawable.loadSpriteAnimated(setup.surface, 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        camera = services.get(Camera.class);

        addTrait(TransformableModel.class);
        addTrait(MirrorableModel.class);
        addTrait(BodyModel.class);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        final State current = state.handleInput(factory, keyboard);
        if (current != null)
        {
            state = current;
        }
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
     * @param factory The state factory reference.
     */
    private void loadStates(StateFactory factory)
    {
        final ConfigAnimations configAnimations = ConfigAnimations.create(getConfigurer());
        for (final MarioState state : MarioState.values())
        {
            try
            {
                final Animation animation = configAnimations.getAnimation(state.getAnimationName());
                factory.addState(state, state.create(this, animation));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    @Override
    protected void prepareTraits()
    {
        transformable = getTrait(Transformable.class);
        transformable.teleport(160, GROUND);

        mirrorable = getTrait(Mirrorable.class);

        body = getTrait(Body.class);
        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);

        loadStates(factory);
        state = factory.getState(MarioState.IDLE);
    }

    @Override
    public void update(double extrp)
    {
        state.update(extrp);
        mirrorable.update(extrp);
        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        if (transformable.getY() < GROUND)
        {
            transformable.teleportY(GROUND);
            jump.setDirection(Direction.ZERO);
            body.resetGravity();
        }
        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
        surface.setLocation(camera, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
