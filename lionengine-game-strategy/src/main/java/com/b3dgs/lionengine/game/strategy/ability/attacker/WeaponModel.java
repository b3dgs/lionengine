/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.ability.attacker;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.Range;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * Default weapon model implementation.
 * 
 * @param <E> The entity type used.
 * @param <A> The attacker type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class WeaponModel<E extends EntityStrategy, A extends AttackerUsedServices<E>>
        extends ObjectGame
        implements WeaponServices<E, A>, AttackerListener<E>
{
    /**
     * Attack state.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static enum State
    {
        /** None state. */
        NONE,
        /** Check for an attack state. */
        CHECK,
        /** Attacking state. */
        ATTACKING;
    }

    /** Listener list. */
    private final Set<AttackerListener<E>> listeners;
    /** Damages. */
    private final Damages damages;
    /** Attack distance allowed. */
    private final Range distAttack;
    /** User reference. */
    private A user;
    /** Attacker target. */
    private E target;
    /** Attack frame number. */
    private int frameAttack;
    /** Attack pause time. */
    private int attackPause;
    /** Attack state. */
    private State state;
    /** Attack timer. */
    private long timer;
    /** True if stop attack is requested. */
    private boolean stop;
    /** Currently attacking flag. */
    private boolean attacking;
    /** Attacked flag. */
    private boolean attacked;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public WeaponModel(SetupGame setup)
    {
        super(setup);
        listeners = new HashSet<>(1);
        damages = new Damages();
        target = null;
        frameAttack = 1;
        distAttack = new Range(1, 1);
        attackPause = 1;
        state = State.NONE;
        timer = 0L;
        stop = false;
        attacking = false;
        attacked = false;
    }

    /*
     * AttackerServices
     */

    @Override
    public void attack(E target)
    {
        final boolean targetExist = target != null;
        final boolean targetDifferent = target != this.target;
        final boolean isNotAttacking = State.NONE == this.state || this.stop;

        if (targetExist && targetDifferent || isNotAttacking)
        {
            this.target = target;
            state = State.CHECK;
            attacking = false;
            stop = false;
        }
    }

    @Override
    public void stopAttack()
    {
        stop = true;
    }

    @Override
    public void updateAttack(double extrp)
    {
        switch (state)
        {
            case CHECK:
                attacking = false;
                attacked = false;
                // Check if target is valid; exit if invalid
                if (target == null || !target.isAlive())
                {
                    state = State.NONE;
                    attacking = false;
                    break;
                }

                final int dist = user.getDistanceInTile(target, false);
                final boolean validRange = dist >= distAttack.getMin() && dist <= distAttack.getMax();

                // Target distance is correct
                if (validRange)
                {
                    if (user.canAttack())
                    {
                        state = State.ATTACKING;
                    }
                }
                else if (UtilMath.time() - timer > attackPause)
                {
                    notifyReachingTarget(target);
                }
                break;
            case ATTACKING:
                if (UtilMath.time() - timer > attackPause)
                {
                    if (!attacking)
                    {
                        notifyAttackStarted(target);
                        attacking = true;
                        attacked = false;
                    }
                    // Hit when frame attack reached
                    if (attacking && user.getFrame() >= frameAttack)
                    {
                        attacking = false;
                        for (final AttackerListener<E> listener : listeners)
                        {
                            listener.notifyAttackEnded(damages.getRandom(), target);
                        }
                        notifyAttackEnded(damages.getRandom(), target);
                        attacked = true;
                        timer = UtilMath.time();
                    }
                }
                else if (attacked)
                {
                    if (AnimState.FINISHED == user.getAnimState())
                    {
                        notifyAttackAnimEnded();
                        attacked = false;
                        state = State.CHECK;
                    }
                }
                else
                {
                    notifyPreparingAttack();
                }
                break;
            default:
                break;
        }
        if (stop)
        {
            attacking = false;
            state = State.NONE;
        }
    }

    @Override
    public void setUser(A user)
    {
        this.user = user;
        listeners.clear();
        listeners.add(user);
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
        return State.ATTACKING == state;
    }

    @Override
    public A getUser()
    {
        return user;
    }

    @Override
    public E getTarget()
    {
        return target;
    }

    /*
     * Surface
     */

    @Override
    public int getLocationIntX()
    {
        return user.getLocationIntX();
    }

    @Override
    public int getLocationIntY()
    {
        return user.getLocationIntY();
    }

    @Override
    public int getWidth()
    {
        return user.getWidth();
    }

    @Override
    public int getHeight()
    {
        return user.getHeight();
    }

    /*
     * AttackerListener
     */

    @Override
    public void notifyReachingTarget(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyReachingTarget(target);
        }
    }

    @Override
    public void notifyAttackStarted(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackStarted(target);
        }
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackAnimEnded();
        }
    }

    @Override
    public void notifyAttackEnded(int damages, E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackAnimEnded();
        }
    }

    @Override
    public void notifyPreparingAttack()
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyPreparingAttack();
        }
    }

    @Override
    public void notifyTargetLost(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyTargetLost(target);
        }
    }
}
