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
package com.b3dgs.lionengine.example.game.gameplay;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Mario
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Setup. */
    private static final SetupSurface SETUP = new SetupSurface(Core.MEDIA.create("mario.xml"));
    /** Ground location y. */
    private static final int GROUND = 32;

    /** Surface. */
    private final SpriteAnimated surface;
    /** Transformable model. */
    private final Transformable transformable;
    /** Camera reference. */
    private final Camera camera;
    /** Desired fps value. */
    private final int desiredFps;
    /** State factory. */
    private final StateFactory factory;
    /** Entity state. */
    private State state;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     */
    public Mario(Services services)
    {
        super(SETUP, services);

        transformable = new TransformableModel(this, SETUP.getConfigurer());
        addTrait(transformable);

        desiredFps = services.get(Integer.class).intValue();
        factory = new StateFactory();

        final Configurer configurer = Mario.SETUP.getConfigurer();
        setMoveSpeedMax(configurer.getDouble("movementSpeed", "data"));
        setJumpHeightMax(configurer.getDouble("jumpSpeed", "data"));
        teleport(100, 32);
        setMass(configurer.getDouble("mass", "data"));

        surface = Drawable.loadSpriteAnimated(SETUP.surface, 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
        addTrait(surface);

        loadStates(configurer, factory);
        state = factory.getState(MarioState.IDLE);
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
            current.enter();
        }
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
    public void update(double extrp)
    {
        surface.setLocation(camera, transformable.getX(), transformable.getY());
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
