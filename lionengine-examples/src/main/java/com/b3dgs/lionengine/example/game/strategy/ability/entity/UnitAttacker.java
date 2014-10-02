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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.Weapon;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.strategy.ability.attacker.AttackerModel;
import com.b3dgs.lionengine.game.strategy.ability.attacker.AttackerServices;
import com.b3dgs.lionengine.game.strategy.ability.attacker.AttackerUsedServices;

/**
 * Mover attacker implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class UnitAttacker
        extends Unit
        implements AttackerUsedServices<Entity>, AttackerServices<Entity, UnitAttacker, Weapon>
{
    /** Animations. */
    protected final Animation animAttack;
    /** Mover model. */
    private final AttackerModel<Entity, UnitAttacker, Weapon> attacker;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected UnitAttacker(SetupSurfaceGame setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        animAttack = configAnimations.getAnimation("attack");
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
