package com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ability.attacker.WeaponModel;

/**
 * Weapon base implementation.
 */
public abstract class Weapon
        extends WeaponModel<Entity, Attacker>
{
    /**
     * Constructor.
     * 
     * @param id The weapon id.
     * @param user The user reference.
     * @param context The context reference.
     */
    protected Weapon(TypeWeapon id, Attacker user, Context context)
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

    /**
     * Get the attacker reference.
     * 
     * @return The attacker reference.
     */
    public Attacker getAttacker()
    {
        return user;
    }
}
