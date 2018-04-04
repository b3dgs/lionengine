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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.LionEngineException;

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

        Assert.assertEquals(AnimState.STOPPED, animator.getAnimState());
        Assert.assertEquals(Animation.MINIMUM_FRAME, animator.getFrame());
        Assert.assertEquals(Animation.MINIMUM_FRAME, animator.getFrameAnim());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());
    }

    /**
     * Test play with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testPlayNull()
    {
        final Animator animator = new AnimatorImpl();
        animator.play(null);
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        final Animator animator = new AnimatorImpl();
        animator.stop();

        Assert.assertEquals(AnimState.STOPPED, animator.getAnimState());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());
        Assert.assertEquals(1, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());

        animator.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(2, animator.getFrameAnim());
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());
    }

    /**
     * Test invalid speed setter.
     */
    @Test(expected = LionEngineException.class)
    public void testSetSpeedNegative()
    {
        final Animator animator = new AnimatorImpl();
        animator.setAnimSpeed(-1.0);
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

        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(3, animator.getFrame());
        Assert.assertEquals(3, animator.getFrameAnim());
    }

    /**
     * Test set frame invalid.
     */
    @Test(expected = LionEngineException.class)
    public void testFrameInvalid()
    {
        final Animator animator = new AnimatorImpl();
        animator.setFrame(0);
    }
}
