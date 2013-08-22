package com.b3dgs.lionengine.example.d_rts.e_skills.weapon;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeProjectile;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeWeapon;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.e_skills.projectile.LauncherProjectile;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;

/**
 * Spear weapon implementation.
 */
final class Spear
        extends Weapon
{
    /** Launcher instance. */
    private final LauncherProjectile launcher;

    /**
     * Constructor.
     * 
     * @param user The user reference.
     * @param context The context reference.
     */
    Spear(AttackerUsedServices<Entity> user, Context context)
    {
        super(TypeWeapon.spear, user, context);
        launcher = new LauncherProjectile(TypeProjectile.spear, context);
        launcher.setOwner(this);
        launcher.setCanHitTargetOnly(true);
    }

    /*
     * Weapon
     */

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        launcher.launch(target);
    }
}
