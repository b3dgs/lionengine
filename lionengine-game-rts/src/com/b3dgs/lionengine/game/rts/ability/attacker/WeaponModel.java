package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.Range;
import com.b3dgs.lionengine.game.Surface;
import com.b3dgs.lionengine.game.rts.EntityRts;
import com.b3dgs.lionengine.game.rts.ability.AbilityModel;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Default attacker model implementation.
 * 
 * @param <E> The entity type used.
 * @param <A> The attacker type used.
 */
public abstract class WeaponModel<E extends EntityRts, A extends AttackerUsedServices<E>>
        extends AbilityModel<AttackerListener<E>, A>
        implements WeaponServices<E>, AttackerListener<E>, Surface
{
    /** Attack state. */
    private static enum State
    {
        /** None state. */
        NONE,
        /** Check for an attack state. */
        CHECK,
        /** Attacking state. */
        ATTACKING;
    }

    /** Damages. */
    private final Damages damages;
    /** Attack distance allowed. */
    private final Range distAttack;
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
     * Create a new attacker ability.
     * 
     * @param user The ability user reference.
     */
    public WeaponModel(A user)
    {
        super(user);
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
     * Attacker services
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
                else if (UtilityMath.time() - timer > attackPause)
                {
                    notifyReachingTarget(target);
                }
                break;
            case ATTACKING:
                if (UtilityMath.time() - timer > attackPause)
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
                        timer = UtilityMath.time();
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
     * Attacker listener
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
