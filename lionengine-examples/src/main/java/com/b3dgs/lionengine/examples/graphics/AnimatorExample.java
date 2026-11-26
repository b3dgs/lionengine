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
package com.b3dgs.lionengine.examples.graphics;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.graphics.Animation;
import com.b3dgs.lionengine.graphics.AnimationLoop;
import com.b3dgs.lionengine.graphics.Animator;
import com.b3dgs.lionengine.graphics.AnimatorState;

/**
 * Plays animations with an {@link Animator} from the update step: a walk that loops, paused and resumed, then an
 * attack played once (REQ-ANIM-002, REQ-ANIM-003, ADR-0058).
 *
 * <p>
 * The game calls {@link Animator#update(double)} once per update step with the constant step of the loop, here
 * 1/20 second; the animator shows each frame for its duration and carries the time left over into the next frame, so
 * the speed does not depend on the step. {@link Animator#play(Animation)} starts an animation from its first frame:
 * the game calls it when its own state changes, not at every step. {@link Animator#pause()} keeps the frame and the
 * time spent in it until {@link Animator#resume()}. A {@link AnimationLoop#ONCE} animation becomes
 * {@link AnimatorState#FINISHED} on its last frame, which tells the game the attack is over. The render phase only
 * reads {@link Animator#getFrame()}, the cell to draw.
 * </p>
 *
 * @since 11.0.0
 */
public final class AnimatorExample
{
    /** The update step in seconds: 20 steps per second. */
    public static final double STEP = 0.05;
    /** The walk: four cells of 0.1 second, looping. */
    public static final Animation WALK = new Animation(new int[]
    {
        0, 1, 2, 3
    }, 0.1, AnimationLoop.LOOP);
    /** The attack: a short wind-up, a long strike, played once. */
    public static final Animation ATTACK = new Animation(new int[]
    {
        4, 5
    }, new double[]
    {
        0.05, 0.15
    }, AnimationLoop.ONCE);

    /** Steps of the walk before the pause. */
    private static final int WALK_STEPS = 5;
    /** Steps during the pause. */
    private static final int PAUSE_STEPS = 3;
    /** Steps of the walk after the pause. */
    private static final int RESUMED_STEPS = 2;
    /** Most steps given to the attack. */
    private static final int ATTACK_STEPS = 10;

    /**
     * Private constructor.
     */
    private AnimatorExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Walks, pauses, walks again, then attacks until the attack is finished, recording the cell drawn after each
     * update step.
     *
     * @return The cells drawn and the number of steps the attack lasted.
     */
    public static Result run()
    {
        final Animator animator = new Animator();
        final List<Integer> cells = new ArrayList<>();

        animator.play(WALK);
        steps(animator, WALK_STEPS, cells);
        animator.pause();
        steps(animator, PAUSE_STEPS, cells);
        animator.resume();
        steps(animator, RESUMED_STEPS, cells);

        animator.play(ATTACK);
        int attackSteps = 0;
        while (animator.getState() == AnimatorState.PLAYING && attackSteps < ATTACK_STEPS)
        {
            steps(animator, 1, cells);
            attackSteps++;
        }
        return new Result(cells, attackSteps, animator.getState());
    }

    /**
     * Runs update steps and records the cell to draw after each one, as the render phase would read it.
     *
     * @param animator The animator.
     * @param count The number of steps.
     * @param cells The cells drawn.
     */
    private static void steps(Animator animator, int count, List<Integer> cells)
    {
        for (int i = 0; i < count; i++)
        {
            animator.update(STEP);
            cells.add(Integer.valueOf(animator.getFrame()));
        }
    }

    /**
     * Result of the example.
     *
     * @param cells The cell drawn after each update step.
     * @param attackSteps The number of update steps the attack lasted.
     * @param end The state at the end: finished once the attack was shown.
     * @since 11.0.0
     */
    public record Result(List<Integer> cells, int attackSteps, AnimatorState end)
    {
        // Value type of the example.
    }
}
