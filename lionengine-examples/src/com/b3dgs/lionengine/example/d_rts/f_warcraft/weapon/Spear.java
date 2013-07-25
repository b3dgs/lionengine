package com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile.LauncherProjectile;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeProjectile;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

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
    Spear(Attacker user, Context context)
    {
        super(TypeWeapon.spear, user, context);
        launcher = new LauncherProjectile(TypeProjectile.spear, context);
        launcher.setOwner(this);
        launcher.setCanHitTargetOnly(true);
    }

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        launcher.launch(target);
    }
}
