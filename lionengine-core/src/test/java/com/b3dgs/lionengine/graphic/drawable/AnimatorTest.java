/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;

/**
 * Test {@link Animator}.
 */
public final class AnimatorTest
{
    /**
     * Test constructor default.
     */
    @Test
    public void testConstructor()
    {
        final Animator animator = new AnimatorImpl();

        assertEquals(AnimState.STOPPED, animator.getAnimState());
        assertEquals(Animation.MINIMUM_FRAME, animator.getFrame());
        assertEquals(Animation.MINIMUM_FRAME, animator.getFrameAnim());
    }

    /**
     * Test play.
     */
    @Test
    public void testPlay()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 3.0, false, false);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());
    }

    /**
     * Test play with <code>null</code> argument.
     */
    @Test
    public void testPlayNull()
    {
        final Animator animator = new AnimatorImpl();

        assertThrows(() -> animator.play(null), "Unexpected null argument !");
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        final Animator animator = new AnimatorImpl();
        animator.stop();

        assertEquals(AnimState.STOPPED, animator.getAnimState());
    }

    /**
     * Test update without loop nor reverse.
     */
    @Test
    public void testUpdateNoLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 1.0, false, false);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.FINISHED, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.FINISHED, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());
    }

    /**
     * Test update with loop but no reverse.
     */
    @Test
    public void testUpdateLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, true);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());
    }

    /**
     * Test update without loop but reverse.
     */
    @Test
    public void testUpdateNoLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, false);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.REVERSING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.REVERSING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.FINISHED, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());
    }

    /**
     * Test update with loop and reverse.
     */
    @Test
    public void testUpdateLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, true);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.REVERSING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.REVERSING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        assertEquals(AnimState.REVERSING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());
    }

    /**
     * Test set speed.
     */
    @Test
    public void testSetSpeed()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);
        animator.setAnimSpeed(2.0);
        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());
    }

    /**
     * Test invalid speed setter.
     */
    @Test
    public void testSetSpeedNegative()
    {
        final Animator animator = new AnimatorImpl();

        assertThrows(() -> animator.setAnimSpeed(-1.0), "Invalid argument: -1.0 is not superior or equal to 0.0");
    }

    /**
     * Test set frame.
     */
    @Test
    public void testSetFrame()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final Animator animator = new AnimatorImpl();
        animator.play(animation);
        animator.setFrame(2);
        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(3, animator.getFrame());
        assertEquals(3, animator.getFrameAnim());
    }

    /**
     * Test set frame invalid.
     */
    @Test
    public void testFrameInvalid()
    {
        final Animator animator = new AnimatorImpl();

        assertThrows(() -> animator.setFrame(0), "Invalid argument: 0 is not superior or equal to 1");
    }
}
