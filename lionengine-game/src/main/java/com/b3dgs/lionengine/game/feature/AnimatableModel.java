/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorListener;
import com.b3dgs.lionengine.AnimatorModel;

/**
 * Animatable model implementation.
 */
@FeatureInterface
public class AnimatableModel extends FeatureModel implements Animatable, Recyclable
{
    /** Animator reference. */
    private final Animator animator;

    /**
     * Create the feature with internal animator.
     */
    public AnimatableModel()
    {
        this(new AnimatorModel());
    }

    /**
     * Create the feature.
     * 
     * @param animator The animator reference.
     */
    public AnimatableModel(Animator animator)
    {
        super();

        this.animator = animator;
    }

    /*
     * Animatable
     */

    @Override
    public void addListener(AnimatorListener listener)
    {
        animator.addListener(listener);
    }

    @Override
    public void removeListener(AnimatorListener listener)
    {
        animator.removeListener(listener);
    }

    @Override
    public void update(double extrp)
    {
        animator.update(extrp);
    }

    @Override
    public void play(Animation animation)
    {
        animator.play(animation);
    }

    @Override
    public void stop()
    {
        animator.stop();
    }

    @Override
    public void reset()
    {
        animator.reset();
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        animator.setAnimSpeed(speed);
    }

    @Override
    public void setFrame(int frame)
    {
        animator.setFrame(frame);
    }

    @Override
    public int getFrame()
    {
        return animator.getFrame();
    }

    @Override
    public int getFrameAnim()
    {
        return animator.getFrameAnim();
    }

    @Override
    public AnimState getAnimState()
    {
        return animator.getAnimState();
    }

    @Override
    public boolean is(AnimState state)
    {
        return animator.getAnimState() == state;
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        animator.reset();
    }
}
