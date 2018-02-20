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
package com.b3dgs.lionengine.game.feature.attackable;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Attacker model implementation.
 */
public class AttackerModel extends FeatureModel implements Attacker, Recyclable
{
    /** Listener list. */
    private final Collection<AttackerListener> listeners = new HashSet<>(1);
    /** Attack timer. */
    private final Timing timer = new Timing();
    /** Damages. */
    private final Damages damages = new Damages();
    /** Attack distance allowed. */
    private Range distAttack = new Range(1, 1);
    /** Animator reference. */
    private Animator animator;
    /** Transformable reference. */
    private Transformable transformable;
    /** Attacker checker reference. */
    private AttackerChecker checker;
    /** Attacker target. */
    private Transformable target;
    /** Attack frame number. */
    private int frameAttack;
    /** Attack pause time. */
    private int attackPause;
    /** Attack state. */
    private AttackState state;
    /** True if stop attack is requested. */
    private boolean stop;
    /** Currently attacking flag. */
    private boolean attacking;
    /** Attacked flag. */
    private boolean attacked;

    /**
     * Create an attacker model.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Animatable}</li>
     * <li>{@link Transformable}</li>
     * </ul>
     */
    public AttackerModel()
    {
        super();

        recycle();
    }

    /**
     * Update the attack check case.
     */
    private void updateAttackCheck()
    {
        attacking = false;
        attacked = false;
        // Check if target is valid; exit if invalid
        if (target == null)
        {
            state = AttackState.NONE;
        }
        else
        {
            final double dist = UtilMath.getDistance(transformable.getX(),
                                                     transformable.getY(),
                                                     transformable.getWidth(),
                                                     transformable.getHeight(),
                                                     target.getX(),
                                                     target.getY(),
                                                     target.getWidth(),
                                                     target.getHeight());
            checkTargetDistance(dist);
        }
    }

    /**
     * Check the target distance and update the attack state.
     * 
     * @param dist The target distance.
     */
    private void checkTargetDistance(double dist)
    {
        if (distAttack.includes(dist))
        {
            if (checker.canAttack())
            {
                state = AttackState.ATTACKING;
            }
        }
        else if (timer.elapsed(attackPause))
        {
            for (final AttackerListener listener : listeners)
            {
                listener.notifyReachingTarget(target);
            }
        }
    }

    /**
     * Update the attacking case.
     */
    private void updateAttacking()
    {
        if (timer.elapsed(attackPause))
        {
            updateAttackHit();
        }
        else if (attacked)
        {
            if (AnimState.FINISHED == animator.getAnimState())
            {
                for (final AttackerListener listener : listeners)
                {
                    listener.notifyAttackAnimEnded();
                }
                attacked = false;
                state = AttackState.CHECK;
            }
        }
        else
        {
            for (final AttackerListener listener : listeners)
            {
                listener.notifyPreparingAttack();
            }
        }
    }

    /**
     * Update the attack state and hit when possible.
     */
    private void updateAttackHit()
    {
        if (!attacking)
        {
            for (final AttackerListener listener : listeners)
            {
                listener.notifyAttackStarted(target);
            }
            attacking = true;
            attacked = false;
        }
        // Hit when frame attack reached
        if (animator.getFrame() >= frameAttack)
        {
            attacking = false;
            for (final AttackerListener listener : listeners)
            {
                listener.notifyAttackEnded(damages.getRandom(), target);
            }
            attacked = true;
            timer.restart();
        }
    }

    /*
     * Attacker
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animator = provider.getFeature(Animatable.class);
        transformable = provider.getFeature(Transformable.class);

        if (provider instanceof AttackerListener)
        {
            addListener((AttackerListener) provider);
        }
        checker = (AttackerChecker) provider;
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof AttackerListener)
        {
            addListener((AttackerListener) listener);
        }
    }

    @Override
    public void addListener(AttackerListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void attack(Transformable target)
    {
        final boolean targetExist = target != null;
        final boolean targetDifferent = target != this.target;
        final boolean isNotAttacking = AttackState.NONE == state || stop;

        if (targetExist && targetDifferent || isNotAttacking)
        {
            this.target = target;
            state = AttackState.CHECK;
            attacking = false;
            stop = false;
            timer.start();
        }
    }

    @Override
    public void stopAttack()
    {
        stop = true;
    }

    @Override
    public void update(double extrp)
    {
        switch (state)
        {
            case NONE:
                // Nothing to do
                break;
            case CHECK:
                updateAttackCheck();
                break;
            case ATTACKING:
                updateAttacking();
                break;
            default:
                throw new LionEngineException(state);
        }
        if (stop)
        {
            attacking = false;
            state = AttackState.NONE;
        }
    }

    @Override
    public void setAttackTimer(int time)
    {
        attackPause = time;
    }

    @Override
    public void setAttackFrame(int frame)
    {
        frameAttack = frame;
    }

    @Override
    public void setAttackDistance(int min, int max)
    {
        distAttack = new Range(min, max);
    }

    @Override
    public void setAttackDamages(int min, int max)
    {
        damages.setDamages(min, max);
    }

    @Override
    public int getAttackDamages()
    {
        return damages.getRandom();
    }

    @Override
    public boolean isAttacking()
    {
        return AttackState.ATTACKING == state;
    }

    @Override
    public Transformable getTarget()
    {
        return target;
    }

    /*
     * Recyclable
     */

    @Override
    public final void recycle()
    {
        attacking = false;
        attacked = false;
        stop = false;
        frameAttack = 1;
        attackPause = 1;
        target = null;
        state = AttackState.NONE;
    }
}
