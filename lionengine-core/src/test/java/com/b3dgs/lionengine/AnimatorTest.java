/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Animator}.
 */
final class AnimatorTest
{
    /**
     * Test constructor default.
     */
    @Test
    void testConstructor()
    {
        final Animator animator = new AnimatorModel();

        assertEquals(AnimState.STOPPED, animator.getAnimState());
        assertEquals(Animation.MINIMUM_FRAME, animator.getFrame());
        assertEquals(Animation.MINIMUM_FRAME, animator.getFrameAnim());
    }

    /**
     * Test play.
     */
    @Test
    void testPlay()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 3.0, false, false);
        final Animator animator = new AnimatorModel();

        assertEquals(1, animator.getFrames());

        animator.play(animation);

        assertEquals(animation, animator.getAnim());
        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());
        assertEquals(2, animator.getFrames());
        assertEquals(3.0, animator.getAnimSpeed());
    }

    /**
     * Test play with <code>null</code> argument.
     */
    @Test
    void testPlayNull()
    {
        final Animator animator = new AnimatorModel();

        assertThrows(() -> animator.play(null), "Unexpected null argument !");
    }

    /**
     * Test stop.
     */
    @Test
    void testStop()
    {
        final Animator animator = new AnimatorModel();
        animator.stop();

        assertEquals(AnimState.STOPPED, animator.getAnimState());
    }

    /**
     * Test update without loop nor reverse.
     */
    @Test
    void testUpdateNoLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 1.0, false, false);
        final Animator animator = new AnimatorModel();
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
    void testUpdateLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, true);
        final Animator animator = new AnimatorModel();
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
    void testUpdateNoLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, false);
        final Animator animator = new AnimatorModel();
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

        assertEquals(AnimState.FINISHED, animator.getAnimState());
        assertEquals(1, animator.getFrame());
        assertEquals(1, animator.getFrameAnim());
    }

    /**
     * Test update with loop and reverse.
     */
    @Test
    void testUpdateLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, true);
        final Animator animator = new AnimatorModel();
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
    }

    /**
     * Test set speed.
     */
    @Test
    void testSetSpeed()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final Animator animator = new AnimatorModel();
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
    void testSetSpeedNegative()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final Animator animator = new AnimatorModel();
        animator.play(animation);
        animator.setAnimSpeed(-1.0);
        animator.setFrame(3);
        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(2, animator.getFrame());
        assertEquals(2, animator.getFrameAnim());
    }

    /**
     * Test set frame.
     */
    @Test
    void testSetFrame()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final Animator animator = new AnimatorModel();
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
    void testFrameInvalid()
    {
        final Animator animator = new AnimatorModel();

        assertThrows(() -> animator.setFrame(0), "Invalid argument: 0 is not superior or equal to 1");
    }

    /**
     * Test reset.
     */
    @Test
    void testReset()
    {
        final AtomicReference<Animation> played = new AtomicReference<>();
        final AtomicReference<AnimState> stated = new AtomicReference<>();
        final AtomicReference<Integer> framed = new AtomicReference<>();
        final AnimatorListener listener = new AnimatorListener()
        {
            @Override
            public void notifyAnimPlayed(Animation anim)
            {
                played.set(anim);
            }

            @Override
            public void notifyAnimState(AnimState state)
            {
                stated.set(state);
            }

            @Override
            public void notifyAnimFrame(int frame)
            {
                framed.set(Integer.valueOf(frame));
            }
        };
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);
        animator.play(animation);
        animator.update(1.0);

        assertEquals(animation, played.get());
        assertEquals(AnimState.PLAYING, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        played.set(null);
        stated.set(null);
        framed.set(null);
        animator.update(1.0);
        animator.reset();

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());
    }

    /**
     * Test with listener.
     */
    @Test
    void testListener()
    {
        final AtomicReference<Animation> played = new AtomicReference<>();
        final AtomicReference<AnimState> stated = new AtomicReference<>();
        final AtomicReference<Integer> framed = new AtomicReference<>();
        final AnimatorListener listener = new AnimatorListener()
        {
            @Override
            public void notifyAnimPlayed(Animation anim)
            {
                played.set(anim);
            }

            @Override
            public void notifyAnimState(AnimState state)
            {
                stated.set(state);
            }

            @Override
            public void notifyAnimFrame(int frame)
            {
                framed.set(Integer.valueOf(frame));
            }
        };
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());

        animator.play(animation);

        assertEquals(animation, played.get());
        assertEquals(AnimState.PLAYING, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        played.set(null);
        stated.set(null);
        framed.set(null);
        animator.update(1.0);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());

        animator.update(1.0);
        animator.update(1.0);
        animator.update(1.0);

        assertEquals(Integer.valueOf(2), framed.get());

        for (int i = 0; i < 8; i++)
        {
            animator.update(1.0);
        }

        assertEquals(AnimState.REVERSING, stated.get());
        assertEquals(Integer.valueOf(2), framed.get());

        stated.set(null);
        framed.set(null);
        for (int i = 0; i < 9; i++)
        {
            animator.update(1.0);
        }

        assertEquals(AnimState.FINISHED, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        animator.stop();

        assertEquals(AnimState.STOPPED, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        animator.setFrame(2);

        assertEquals(Integer.valueOf(2), framed.get());
    }

    /**
     * Test with state listener.
     */
    @Test
    void testListenerState()
    {
        final AtomicReference<AnimState> stated = new AtomicReference<>();
        final AnimatorListener listener = (AnimatorStateListener) state -> stated.set(state);
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);

        assertNull(stated.get());

        animator.play(animation);

        assertEquals(AnimState.PLAYING, stated.get());

        stated.set(null);
        animator.removeListener(listener);

        animator.play(animation);
        animator.update(1.0);

        assertNull(stated.get());
    }

    /**
     * Test with frame listener.
     */
    @Test
    void testListenerFrame()
    {
        final AtomicReference<Integer> framed = new AtomicReference<>();
        final AnimatorListener listener = (AnimatorFrameListener) frame -> framed.set(Integer.valueOf(frame));
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);

        assertNull(framed.get());

        animator.play(animation);
        animator.update(1.0);

        assertEquals(Integer.valueOf(2), framed.get());

        framed.set(null);
        animator.removeListener(listener);

        animator.play(animation);
        animator.update(1.0);

        assertNull(framed.get());
    }

    /**
     * Test with anim listener.
     */
    @Test
    void testListenerAnim()
    {
        final AtomicReference<Animation> anim = new AtomicReference<>();
        final AnimatorListener listener = (AnimatorAnimListener) a -> anim.set(a);
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);

        assertNull(anim.get());

        animator.play(animation);
        animator.update(1.0);

        assertEquals(animation, anim.get());

        anim.set(null);
        animator.removeListener(listener);

        animator.play(animation);
        animator.update(1.0);

        assertNull(anim.get());
    }

    /**
     * Test with void listener.
     */
    @Test
    void testListenerVoid()
    {
        final AnimatorListener listener = AnimatorListenerVoid.getInstance();
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.25, true, false);
        final Animator animator = new AnimatorModel();
        animator.addListener(listener);
        animator.play(animation);
        animator.update(1.0);

        assertEquals(AnimState.PLAYING, animator.getAnimState());
        assertEquals(1, animator.getFrame());
    }

    /**
     * Test the constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(AnimatorListenerVoid.class);
    }
}
