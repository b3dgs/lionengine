/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorListener;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Animatable model implementation.
 */
public class AnimatableModel extends FeatureModel implements Animatable, Recyclable
{
    /** Animator reference. */
    private final Animator animator;

    /**
     * Create feature with internal {@link AnimatorModel}.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public AnimatableModel(Services services, Setup setup)
    {
        this(services, setup, new AnimatorModel());
    }

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param animator The animator reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public AnimatableModel(Services services, Setup setup, Animator animator)
    {
        super(services, setup);

        Check.notNull(animator);

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
    public Animation getAnim()
    {
        return animator.getAnim();
    }

    @Override
    public double getAnimSpeed()
    {
        return animator.getAnimSpeed();
    }

    @Override
    public int getFrames()
    {
        return animator.getFrames();
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
