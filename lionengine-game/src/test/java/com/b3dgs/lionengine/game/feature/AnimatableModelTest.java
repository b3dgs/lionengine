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
package com.b3dgs.lionengine.game.feature;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.graphic.drawable.AnimatorMock;

/**
 * Test the animatable model class.
 */
public class AnimatableModelTest
{
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
        Assert.assertEquals(expectedState, animatable.getAnimState());
        Assert.assertEquals(expectedFrame, animatable.getFrame());
        Assert.assertEquals(expectedFrame - first + 1, animatable.getFrameAnim());
    }

    /**
     * Test the play case.
     */
    @Test
    public void testPlay()
    {
        final int first = 2;
        final int last = 4;
        final Animation animation = new Animation(Animation.DEFAULT_NAME, first, last, 1.0, false, false);
        final Animatable animatable = new AnimatableModel(new AnimatorMock());
        testAnimatorState(animatable, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME, AnimState.STOPPED);

        animatable.play(animation);
        testAnimatorState(animatable, first, first, AnimState.PLAYING);

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
     * Test the manipulations.
     */
    @Test
    public void testManipulation()
    {
        final int first = 2;
        final int last = 5;
        final double speed = 2.0;
        final Animation animation = new Animation(Animation.DEFAULT_NAME, first, last, speed, false, false);
        final Animatable animatable = new AnimatableModel(new AnimatorMock());
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
}
