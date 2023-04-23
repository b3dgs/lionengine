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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorListener;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link AnimatableModel}.
 */
final class AnimatableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilTransformable.createMedia(AnimatableModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Check the animator state.
     * 
     * @param animatable The animatable.
     * @param first The first frame.
     * @param expectedFrame The expected frame.
     * @param expectedState The expected state.
     */
    private static void testAnimatorState(Animatable animatable, int first, int expectedFrame, AnimState expectedState)
    {
        assertTrue(animatable.is(expectedState));
        assertEquals(expectedState, animatable.getAnimState());
        assertEquals(expectedFrame, animatable.getFrame());
        assertEquals(expectedFrame - first + 1, animatable.getFrameAnim());
    }

    /**
     * Test constructor with null animator.
     */
    @Test
    void testConstructorNullAnimator()
    {
        assertThrows(() -> new AnimatableModel(services, setup, null), "Unexpected null argument !");
    }

    /**
     * Test the play case.
     */
    @Test
    void testPlay()
    {
        final int first = 2;
        final int last = 4;
        final Animation animation = new Animation(Animation.DEFAULT_NAME, first, last, 1.0, false, false);
        final Animatable animatable = new AnimatableModel(services, setup);
        testAnimatorState(animatable, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME, AnimState.STOPPED);

        assertEquals(1, animatable.getFrames());

        animatable.play(animation);

        assertEquals(animation, animatable.getAnim());
        testAnimatorState(animatable, first, first, AnimState.PLAYING);
        assertFalse(animatable.is(AnimState.FINISHED));
        assertEquals(last - first + 1, animatable.getFrames());

        animatable.update(1.0);
        testAnimatorState(animatable, first, first + 1, AnimState.PLAYING);

        animatable.update(1.0);
        testAnimatorState(animatable, first, last, AnimState.PLAYING);

        animatable.update(1.0);
        testAnimatorState(animatable, first, last, AnimState.FINISHED);

        animatable.stop();
        testAnimatorState(animatable, first, last, AnimState.STOPPED);
    }

    /**
     * Test the reset case.
     */
    @Test
    void testReset()
    {
        final int first = 2;
        final int last = 4;
        final Animation animation = new Animation(Animation.DEFAULT_NAME, first, last, 1.0, false, false);
        final Animatable animatable = new AnimatableModel(services, setup);
        testAnimatorState(animatable, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME, AnimState.STOPPED);

        animatable.play(animation);
        testAnimatorState(animatable, first, first, AnimState.PLAYING);

        animatable.reset();

        testAnimatorState(animatable, 1, 1, AnimState.STOPPED);
    }

    /**
     * Test the manipulations.
     */
    @Test
    void testManipulation()
    {
        final int first = 2;
        final int last = 5;
        final double speed = 2.0;
        final Animation animation = new Animation(Animation.DEFAULT_NAME, first, last, speed, false, false);
        final Animatable animatable = new AnimatableModel(services, setup, new AnimatorModel());
        testAnimatorState(animatable, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME, AnimState.STOPPED);

        animatable.play(animation);
        testAnimatorState(animatable, first, first, AnimState.PLAYING);
        animatable.setAnimSpeed(speed - 1.0);

        animatable.update(1.0);
        testAnimatorState(animatable, first, first + 1, AnimState.PLAYING);

        animatable.update(1.0);
        animatable.setFrame(1);

        testAnimatorState(animatable, first, 1, AnimState.PLAYING);

        animatable.stop();
        testAnimatorState(animatable, first, 1, AnimState.STOPPED);
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
        final Animatable animatable = new AnimatableModel(services, setup, new AnimatorModel());
        animatable.addListener(listener);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());

        animatable.play(animation);

        assertEquals(animation, played.get());
        assertEquals(AnimState.PLAYING, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        played.set(null);
        stated.set(null);
        framed.set(null);

        animatable.removeListener(listener);
        animatable.play(animation);
        animatable.update(1.0);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());
    }
}
