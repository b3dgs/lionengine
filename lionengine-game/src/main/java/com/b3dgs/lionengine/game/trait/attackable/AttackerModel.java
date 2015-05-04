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
package com.b3dgs.lionengine.game.trait.attackable;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.Range;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.TraitModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Attacker model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AttackerModel
        extends TraitModel
        implements Attacker
{
    /** Listener list. */
    private final Collection<AttackerListener> listeners = new HashSet<>(1);
    /** Attack timer. */
    private final Timing timer = new Timing();
    /** Damages. */
    private final Damages damages = new Damages();
    /** Attack distance allowed. */
    private final Range distAttack = new Range(1, 1);
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
     */
    public AttackerModel()
    {
        super();
        frameAttack = 1;
        attackPause = 1;
        state = AttackState.NONE;
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
            attacking = false;
        }
        else
        {
            final double dist = UtilMath.getDistance(transformable.getX(), transformable.getY(),
                    transformable.getWidth(), transformable.getHeight(), target.getX(), target.getY(),
                    target.getWidth(), target.getHeight());
            final boolean validRange = dist >= distAttack.getMin() && dist <= distAttack.getMax();

            // Target distance is correct
            if (validRange)
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
        if (attacking && animator.getFrame() >= frameAttack)
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
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        animator = owner.getTrait(Animator.class);
        transformable = owner.getTrait(Transformable.class);

        if (owner instanceof AttackerListener)
        {
            addListener((AttackerListener) owner);
        }
        checker = (AttackerChecker) owner;
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
                throw new RuntimeException();
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
        distAttack.setMin(min);
        distAttack.setMax(max);
    }

    @Override
    public void setAttackDamages(int min, int max)
    {
        damages.setMin(min);
        damages.setMax(max);
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
}
