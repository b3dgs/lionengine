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
package com.b3dgs.lionengine.anim;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the animator.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AnimatorTest
{
    /**
     * Check the animator state.
     * 
     * @param animator The animator.
     * @param first The first frame.
     * @param expectedFrame The expected frame.
     * @param expectedState The expected state.
     */
    private static void testAnimatorState(Animator animator, int first, int expectedFrame, AnimState expectedState)
    {
        Assert.assertEquals(expectedState, animator.getAnimState());
        Assert.assertEquals(expectedFrame, animator.getFrame());
        Assert.assertEquals(expectedFrame - first + 1, animator.getFrameAnim());
    }

    /**
     * Test the play case.
     */
    @Test
    public void testPlay()
    {
        final int first = 2;
        final int last = 4;
        final Animation animation = Anim.createAnimation(first, last, 1.0, false, false);
        final Animator animator = Anim.createAnimator();

        testAnimatorState(animator, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME, AnimState.STOPPED);

        animator.play(animation);

        testAnimatorState(animator, first, first, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, last, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, last, AnimState.FINISHED);

        animator.stop();
        testAnimatorState(animator, first, last, AnimState.STOPPED);
    }

    /**
     * Test the play in reverse case.
     */
    @Test
    public void testPlayReverse()
    {
        final int first = 2;
        final int last = 4;
        final Animation animation = Anim.createAnimation(first, last, 1.0, true, false);
        final Animator animator = Anim.createAnimator();

        animator.play(animation);

        testAnimatorState(animator, first, first, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 2, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.REVERSING);

        animator.update(1.0);
        testAnimatorState(animator, first, first, AnimState.REVERSING);

        animator.update(1.0);
        testAnimatorState(animator, first, first, AnimState.FINISHED);
    }

    /**
     * Test the play in repeat case.
     */
    @Test
    public void testPlayRepeat()
    {
        final int first = 2;
        final int last = 3;
        final Animation animation = Anim.createAnimation(first, last, 1.0, false, true);
        final Animator animator = Anim.createAnimator();

        animator.play(animation);

        testAnimatorState(animator, first, first, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);
    }

    /**
     * Test the play in reverse repeat case.
     */
    @Test
    public void testPlayReverseRepeat()
    {
        final int first = 2;
        final int last = 3;
        final Animation animation = Anim.createAnimation(first, last, 1.0, true, true);
        final Animator animator = Anim.createAnimator();

        animator.play(animation);

        testAnimatorState(animator, first, first, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first, AnimState.REVERSING);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);
        testAnimatorState(animator, first, first, AnimState.REVERSING);
    }

    /**
     * Test the manipulations.
     */
    @Test
    public void testManipulation()
    {
        final int first = 2;
        final int last = 5;
        final double speed = 2.0;
        final Animation animation = Anim.createAnimation(first, last, speed, false, false);
        final Animator animator = Anim.createAnimator();

        animator.play(animation);

        animator.setAnimSpeed(speed - 1.0);

        animator.update(1.0);
        testAnimatorState(animator, first, first + 1, AnimState.PLAYING);

        animator.update(1.0);

        animator.setFrame(1);

        testAnimatorState(animator, first, 1, AnimState.PLAYING);

        animator.stop();
        testAnimatorState(animator, first, 1, AnimState.STOPPED);
    }

    /**
     * Test the animator with null argument.
     */
    @Test(expected = LionEngineException.class)
    public void testPlayNull()
    {
        final Animator animator = Anim.createAnimator();
        animator.play(null);
    }

    /**
     * Test the invalid speed setter.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeSpeed()
    {
        final Animator animator = Anim.createAnimator();
        animator.setAnimSpeed(-1.0);
        Assert.fail();
    }

    /**
     * Test the invalid frame setter.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidFrame()
    {
        final Animator animator = Anim.createAnimator();
        animator.setFrame(0);
        Assert.fail();
    }
}
