package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon.Weapon;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerModel;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerServices;

/**
 * Mover attacker implementation.
 */
public abstract class UnitAttacker
        extends Unit
        implements Attacker, AttackerServices<Entity, Weapon>
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
    protected UnitAttacker(TypeEntity id, Context context)
    {
        super(id, context);
        animAttack = getDataAnimation("attack");
        attacker = new AttackerModel<>(this);
    }

    /**
     * Attack the specified entity, even if it is the same faction.
     * 
     * @param target The enemy to attack.
     */
    public void attackAny(Entity target)
    {
        // Ignore neutral target
        if (target.getPlayer() == null)
        {
            setDestination(target);
        }
        else
        {
            attacker.attack(target);
        }
    }

    /**
     * Add a weapon from its type.
     * 
     * @param context The context reference.
     * @param type The weapon type.
     * @param id The weapon id.
     */
    protected void addWeapon(Context context, TypeWeapon type, int id)
    {
        final Weapon weapon = context.factoryWeapon.createWeapon(type, this);
        addWeapon(weapon, id);
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

    @Override
    public void decreaseLife(int damages, Attacker attacker)
    {
        super.decreaseLife(damages, attacker);
        if (attacker != null && !isAttacking() && attacker instanceof Entity)
        {
            final Entity entity = (Entity) attacker;
            if (entity.getPlayerId() != getPlayerId())
            {
                attack(entity);
            }
        }
    }

    /*
     * Attacker
     */

    @Override
    public boolean canAttack()
    {
        return isActive() && !isMoving() && getAnimState() == AnimState.FINISHED;
    }

    /*
     * AttackerServices
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
    public void attack(Entity target)
    {
        // Ignore neutral & same faction target
        if (target.getPlayer() == null || target.getPlayerId() == getPlayerId())
        {
            setDestination(target);
        }
        else
        {
            attacker.attack(target);
        }
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
     * AttackerListener
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
