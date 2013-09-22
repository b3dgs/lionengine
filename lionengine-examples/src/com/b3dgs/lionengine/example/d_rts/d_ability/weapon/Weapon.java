package com.b3dgs.lionengine.example.d_rts.d_ability.weapon;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.WeaponType;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;
import com.b3dgs.lionengine.game.rts.ability.attacker.WeaponModel;

/**
 * Weapon base implementation.
 */
public abstract class Weapon
        extends WeaponModel<Entity, AttackerUsedServices<Entity>>
{
    /**
     * Constructor.
     * 
     * @param id The weapon id.
     * @param user The user reference.
     * @param context The context reference.
     */
    protected Weapon(WeaponType id, AttackerUsedServices<Entity> user, Context context)
    {
        super(user);

        final Configurable config = context.factoryWeapon.getSetup(id).configurable;
        setAttackFrame(config.getDataInteger("attackFrame"));
        setAttackTimer(config.getDataInteger("attackTimer"));

        final int distMin = config.getDataInteger("min", "distance");
        final int distMax = config.getDataInteger("max", "distance");
        setAttackDistance(distMin, distMax);

        final int dmgMin = config.getDataInteger("min", "damages");
        final int dmgMax = config.getDataInteger("max", "damages");
        setAttackDamages(dmgMin, dmgMax);
    }
}
