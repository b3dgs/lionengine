package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;
import com.b3dgs.lionengine.example.d_rts.d_ability.weapon.Weapon;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerModel;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerServices;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;

/**
 * Mover attacker implementation.
 */
public abstract class UnitAttacker
        extends Unit
        implements AttackerUsedServices<Entity>, AttackerServices<Entity, Weapon>
{
    /** Animations. */
    protected final Animation animAttack;
    /** Mover model. */
    private final AttackerModel<Entity, Weapon> attacker;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected UnitAttacker(EntityType id, Context context)
    {
        super(id, context);
        animAttack = getDataAnimation("attack");
        attacker = new AttackerModel<>(this);
    }

    /*
     * Unit
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        attacker.updateAttack(extrp);
    }

    @Override
    public void stop()
    {
        super.stop();
        attacker.stopAttack();
    }

    /*
     * Attacker user
     */

    @Override
    public boolean canAttack()
    {
        return !isMoving() && getAnimState() == AnimState.FINISHED;
    }

    /*
     * Attacker model
     */

    @Override
    public void addWeapon(Weapon weapon, int id)
    {
        attacker.addWeapon(weapon, id);
    }

    @Override
    public void removeWeapon(int id)
    {
        attacker.removeWeapon(id);
    }

    @Override
    public void setWeapon(int id)
    {
        attacker.setWeapon(id);
    }

    @Override
    public void attack(Entity entity)
    {
        attacker.attack(entity);
    }

    @Override
    public void stopAttack()
    {
        attacker.stopAttack();
    }

    @Override
    public void updateAttack(double extrp)
    {
        attacker.updateAttack(extrp);
    }

    @Override
    public boolean isAttacking()
    {
        return attacker.isAttacking();
    }

    /*
     * Attacker listener
     */

    @Override
    public void notifyReachingTarget(Entity target)
    {
        if (!isMoving())
        {
            setDestination(target.getLocationInTileX(), target.getLocationInTileY());
        }
    }

    @Override
    public void notifyAttackStarted(Entity target)
    {
        stopMoves();
        stopAnimation();
        play(animAttack);
        pointTo(target);
    }

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        // Nothing to do
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        setFrame(animIdle.getFirst());
        play(animIdle);
    }

    @Override
    public void notifyPreparingAttack()
    {
        // play(animIdle);
    }

    @Override
    public void notifyTargetLost(Entity target)
    {
        play(animIdle);
    }
}
