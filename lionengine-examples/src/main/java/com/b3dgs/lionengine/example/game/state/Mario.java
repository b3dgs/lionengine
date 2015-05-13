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
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.StateHandler;
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

    /** State factory. */
    private final StateFactory factory = new StateFactory();
    /** State handler. */
    private final StateHandler handler = new StateHandler(factory);
    /** Movement force. */
    private final Force movement = new Force();
    /** Jump force. */
    private final Force jump = new Force();
    /** Transformable model. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Mirrorable model. */
    private final Mirrorable mirrorable = addTrait(new MirrorableModel());
    /** Body model. */
    private final Body body = addTrait(new BodyModel());
    /** Surface. */
    private final SpriteAnimated surface;
    /** Camera reference. */
    private final Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Mario(SetupSurface setup, Services services)
    {
        super(setup, services);
        camera = services.get(Camera.class);
        transformable.teleport(160, GROUND);

        surface = Drawable.loadSpriteAnimated(setup.surface, 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);
    }

    /**
     * Add an input controller.
     * 
     * @param input The input reference.
     */
    public void addInput(InputDevice input)
    {
        handler.addInput(input);
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
     */
    private void loadStates()
    {
        final ConfigAnimations configAnimations = ConfigAnimations.create(getConfigurer());
        for (final MarioState state : MarioState.values())
        {
            try
            {
                final Animation animation = configAnimations.getAnimation(state.getAnimationName());
                factory.addState(state.create(this, animation));
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
        handler.start(MarioState.IDLE);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
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
