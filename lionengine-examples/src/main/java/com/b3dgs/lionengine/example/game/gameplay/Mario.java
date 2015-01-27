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

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.example.game.gameplay.state.EntityState;
import com.b3dgs.lionengine.example.game.gameplay.state.EntityStateFactory;
import com.b3dgs.lionengine.example.game.gameplay.state.EntityStateType;
import com.b3dgs.lionengine.example.game.gameplay.state.StateIdle;
import com.b3dgs.lionengine.example.game.gameplay.state.StateJump;
import com.b3dgs.lionengine.example.game.gameplay.state.StateTurn;
import com.b3dgs.lionengine.example.game.gameplay.state.StateWalk;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.SetupSurface;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Mario
        extends EntityPlatform
        implements Movable
{
    /** Setup. */
    private static final SetupSurface SETUP = new SetupSurface(Core.MEDIA.create("mario.xml"));
    /** Ground location y. */
    private static final int GROUND = 32;

    /** Desired fps value. */
    private final int desiredFps;
    /** Movable model. */
    private final MovableModel movable;
    /** Animations list. */
    private final EnumMap<EntityStateType, Animation> animations;
    /** State factory. */
    private final EntityStateFactory<Mario> statesFactory;
    /** Entity state. */
    private EntityState<Mario> state;

    /**
     * Constructor.
     * 
     * @param desiredFps The desired fps.
     */
    public Mario(int desiredFps)
    {
        super(Mario.SETUP);
        this.desiredFps = desiredFps;
        movable = new MovableModel();
        animations = new EnumMap<>(EntityStateType.class);
        statesFactory = new EntityStateFactory<>();
        final Configurer configurer = Mario.SETUP.getConfigurer();
        setMoveSpeedMax(configurer.getDouble("movementSpeed", "data"));
        setJumpHeightMax(configurer.getDouble("jumpSpeed", "data"));
        teleport(100, 32);
        setMass(configurer.getDouble("mass", "data"));
        loadAnimations(configurer);

        statesFactory.addState(EntityStateType.IDLE, new StateIdle<Mario>(getAnimation(EntityStateType.IDLE)));
        statesFactory.addState(EntityStateType.WALK, new StateWalk<Mario>(getAnimation(EntityStateType.WALK)));
        statesFactory.addState(EntityStateType.TURN, new StateTurn<Mario>(getAnimation(EntityStateType.TURN)));
        statesFactory.addState(EntityStateType.JUMP, new StateJump<Mario>(getAnimation(EntityStateType.JUMP)));
        state = statesFactory.getState(EntityStateType.IDLE);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        final EntityState<Mario> state = this.state.handleInput(this, statesFactory, keyboard);
        if (state != null)
        {
            this.state = state;
            state.enter(this);
        }
    }

    /**
     * Load all existing animations defined in the xml file.
     * 
     * @param configurer The configurer reference.
     */
    private void loadAnimations(Configurer configurer)
    {
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        for (final EntityStateType state : EntityStateType.values())
        {
            try
            {
                animations.put(state, configAnimations.getAnimation(state.getAnimationName()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    /**
     * Get an animation from its state.
     * 
     * @param state The state reference.
     * @return The animation reference.
     */
    private Animation getAnimation(EntityStateType state)
    {
        return animations.get(state);
    }

    /*
     * EntityPlatform
     */

    @Override
    public void prepare(Services context)
    {
        // Nothing to do
    }

    @Override
    protected void handleActions(double extrp)
    {
        // Nothing to do
    }

    @Override
    protected void handleMovements(double extrp)
    {
        state.updateState(this);
        updateMove(extrp);
        updateGravity(extrp, movable, getJumpDirection());
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        // Block player to avoid infinite falling
        if (getLocationY() < Mario.GROUND)
        {
            setJumpDirection(0.0, 0.0);
            resetGravity();
            teleportY(Mario.GROUND);
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimation(extrp);
    }

    /*
     * Movable
     */

    @Override
    public void updateMove(double extrp)
    {
        movable.updateMove(extrp);
    }

    @Override
    public void resetMove()
    {
        movable.resetMove();
    }

    @Override
    public void setMoveToReach(double fh, double fv)
    {
        movable.setMoveToReach(fh, fv);
    }

    @Override
    public void setJumpDirection(double fh, double fv)
    {
        movable.setJumpDirection(fh, fv);
    }

    @Override
    public void setMoveVelocity(double velocity)
    {
        movable.setMoveVelocity(velocity);
    }

    @Override
    public void setMoveSensibility(double sensibility)
    {
        movable.setMoveSensibility(sensibility);
    }

    @Override
    public void setMoveSpeedMax(double max)
    {
        movable.setMoveSpeedMax(max);
    }

    @Override
    public void setJumpHeightMax(double max)
    {
        movable.setJumpHeightMax(max);
    }

    @Override
    public double getJumpHeightMax()
    {
        return movable.getJumpHeightMax();
    }

    @Override
    public Direction getJumpDirection()
    {
        return movable.getJumpDirection();
    }

    @Override
    public double getMoveSpeedMax()
    {
        return movable.getMoveSpeedMax();
    }

    @Override
    public boolean isMoveDecreasingX()
    {
        return movable.isMoveDecreasingX();
    }

    @Override
    public boolean isMoveIncreasingX()
    {
        return movable.isMoveIncreasingX();
    }

    @Override
    public double getDirectionHorizontal()
    {
        return movable.getDirectionHorizontal();
    }

    @Override
    public double getDirectionVertical()
    {
        return movable.getDirectionVertical();
    }
}
