/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.examples.traits;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.traits.Collidable;
import com.b3dgs.lionengine.traits.CollisionSystem;
import com.b3dgs.lionengine.traits.Gravity;
import com.b3dgs.lionengine.traits.GravitySystem;
import com.b3dgs.lionengine.traits.JumpSystem;
import com.b3dgs.lionengine.traits.Jumpable;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * The world of {@link JumpExample}: a hero on the raised floor of {@link Ledge} jumps, cuts a jump by an early
 * release, walks off the ledge and jumps within the coyote time, then presses the jump just before landing and jumps
 * on landing from the jump buffer (REQ-STD-004, REQ-STD-008, REQ-STD-010, ADR-0030, ADR-0034, ADR-0083, ADR-0084).
 *
 * <p>
 * The game registers the standard trait types and adds the systems at their slots: {@link TransformCaptureSystem}
 * first, its own pad system at {@value #PAD_SLOT} (input and intent, below 1000, ADR-0034), {@link JumpSystem} at
 * {@value JumpSystem#SLOT}, its own walk system at {@value #WALK_SLOT}, {@link GravitySystem} at
 * {@value GravitySystem#SLOT}, {@link MovementSystem} at {@value MovementSystem#SLOT} and {@link CollisionSystem} at
 * {@value CollisionSystem#SLOT}, which pushes the hero out of the tiles and writes its grounded flag, read by the jump
 * system at the next step. The pad system stands in for the jump action of the game (REQ-MP-002): it writes the jump
 * held flag of the hero from a scripted timeline, held from a press step to a release step; the jump system sees the
 * press and the release by itself. The hero has the variable height on, with the default cut factor, coyote time and
 * jump buffer. Meters, Y up, meters per second.
 * </p>
 *
 * @since 11.0.0
 */
public final class Jumping
{
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** The slot of the pad system. */
    public static final int PAD_SLOT = 500;
    /** The slot of the walk system. */
    public static final int WALK_SLOT = 1500;
    /** Hero starting center X, in meters. */
    public static final double START_X = 1.0;
    /** Hero box size, in meters. */
    public static final double SIZE = 0.5;
    /** Hero starting center Y, in meters: standing on the raised floor. */
    public static final double START_Y = Ledge.UPPER_TOP + SIZE / 2.0;
    /** Gravity, in meters per second squared, negative pulls down. */
    public static final double ACCELERATION = -10.0;
    /** Terminal velocity, in meters per second: 1/6 m per step, under half of the box. */
    public static final double TERMINAL_VELOCITY = 10.0;
    /** Jump impulse, in meters per second. */
    public static final double IMPULSE = 5.0;
    /** Walking speed, in meters per second. */
    public static final double WALK_SPEED = 2.0;
    /** Step of the press of the first jump, held until after its landing. */
    public static final int PRESS_JUMP = 30;
    /** Step of the release of the first jump. */
    public static final int RELEASE_JUMP = 100;
    /** Step of the press of the cut jump. */
    public static final int PRESS_CUT = 120;
    /** Step of the early release of the cut jump, while ascending. */
    public static final int RELEASE_CUT = 126;
    /** Step the hero starts walking right. */
    public static final int WALK_START = 160;
    /** Step of the press after walking off the ledge, within the coyote time. */
    public static final int PRESS_COYOTE = 260;
    /** Step of the release of the coyote jump, while falling. */
    public static final int RELEASE_COYOTE = 300;
    /** Step of the press just before landing, kept by the jump buffer, then held. */
    public static final int PRESS_BUFFER = 327;

    /** The world. */
    private final World world = new World();
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The gravities. */
    private final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
    /** The collidables. */
    private final TraitStorage<Collidable> collidables = world.register(Collidable.TYPE);
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables = world.register(Jumpable.TYPE);
    /** The hero. */
    private final long hero = world.create();
    /** Steps played. */
    private int steps;

    /**
     * Creates the world, the hero, the ledge and the systems, and flushes.
     */
    public Jumping()
    {
        final Transformable transformable = transformables.add(hero);
        transformable.setPosition(START_X, START_Y);
        transformable.setPreviousPosition(START_X, START_Y);
        transformable.setSize(SIZE, SIZE);
        movables.add(hero);
        final Gravity gravity = gravities.add(hero);
        gravity.setAcceleration(ACCELERATION);
        gravity.setTerminalVelocity(TERMINAL_VELOCITY);
        collidables.add(hero).setSize(SIZE, SIZE);
        final Jumpable jumpable = jumpables.add(hero);
        jumpable.setImpulse(IMPULSE);
        jumpable.setVariableHeight(true);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(stepSeconds -> pad(), PAD_SLOT);
        world.addSystem(new JumpSystem(world, movables, jumpables), JumpSystem.SLOT);
        world.addSystem(stepSeconds -> walk(), WALK_SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(new CollisionSystem(world, transformables, collidables, movables, jumpables, new Ledge()),
                        CollisionSystem.SLOT);
        world.flush();
    }

    /**
     * Tells whether the pad holds the jump at a step of the timeline.
     *
     * @param step The step, from {@code 1}.
     * @return {@code true} if the jump is held at that step.
     */
    public static boolean isHeld(int step)
    {
        return isBetween(step, PRESS_JUMP, RELEASE_JUMP)
               || isBetween(step, PRESS_CUT, RELEASE_CUT)
               || isBetween(step, PRESS_COYOTE, RELEASE_COYOTE)
               || step >= PRESS_BUFFER;
    }

    /**
     * Tells whether a step is in a range.
     *
     * @param step The step.
     * @param from The first step of the range.
     * @param to The step after the range.
     * @return {@code true} if {@code from <= step < to}.
     */
    private static boolean isBetween(int step, int from, int to)
    {
        return step >= from && step < to;
    }

    /**
     * Plays one step: capture, pad, jump, walk, gravity, movement, collision with the ledge, flush.
     */
    public void step()
    {
        world.update(STEP_SECONDS);
    }

    /**
     * Returns the steps played.
     *
     * @return The steps played, from {@code 0}.
     */
    public int getSteps()
    {
        return steps;
    }

    /**
     * Returns the hero position.
     *
     * @return The hero transformable, as of the last step.
     */
    public Transformable getHero()
    {
        return transformables.get(hero);
    }

    /**
     * Returns the hero velocity.
     *
     * @return The hero movable, as of the last step.
     */
    public Movable getHeroMovable()
    {
        return movables.get(hero);
    }

    /**
     * Returns the hero jump.
     *
     * @return The hero jumpable, as of the last step: grounded flag, jump held flag and times remaining.
     */
    public Jumpable getHeroJumpable()
    {
        return jumpables.get(hero);
    }

    /**
     * Writes the jump held flag of the hero from the timeline.
     */
    private void pad()
    {
        steps++;
        jumpables.get(hero).setJumpHeld(isHeld(steps));
    }

    /**
     * Makes the hero walk right from {@link #WALK_START}, on the ground and in the air.
     */
    private void walk()
    {
        if (steps >= WALK_START)
        {
            final Movable movable = movables.get(hero);
            movable.setVelocity(WALK_SPEED, movable.getVelocityY());
        }
    }
}
