package com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile.LauncherProjectile;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeProjectile;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Bow weapon implementation.
 */
final class Bow
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
    Bow(Attacker user, Context context)
    {
        super(TypeWeapon.spear, user, context);
        launcher = new LauncherProjectile(TypeProjectile.arrow, context);
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
