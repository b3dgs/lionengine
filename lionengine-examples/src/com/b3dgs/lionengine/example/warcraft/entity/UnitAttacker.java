/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.warcraft.entity;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.warcraft.weapon.FactoryWeapon;
import com.b3dgs.lionengine.example.warcraft.weapon.Weapon;
import com.b3dgs.lionengine.example.warcraft.weapon.WeaponType;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerModel;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerServices;

/**
 * Mover attacker implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class UnitAttacker
        extends Unit
        implements Attacker, AttackerServices<Entity, Attacker, Weapon>
{
    /** Animations. */
    protected final Animation animAttack;
    /** Mover model. */
    private final AttackerModel<Entity, Attacker, Weapon> attacker;
    /** Factory weapon. */
    private final FactoryWeapon factoryWeapon;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected UnitAttacker(SetupEntity setup)
    {
        super(setup);
        animAttack = getDataAnimation("attack");
        attacker = new AttackerModel<Entity, Attacker, Weapon>(this);
        factoryWeapon = setup.factoryWeapon;
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
     * @param type The weapon type.
     * @param id The weapon id.
     */
    protected void addWeapon(WeaponType type, int id)
    {
        final Weapon weapon = factoryWeapon.create(type);
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
    public Weapon getWeapon(int id)
    {
        return attacker.getWeapon(id);
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
        getWeapon(0).setFrame(getOrientation().ordinal() % Orientation.ORIENTATIONS_NUMBER);
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
