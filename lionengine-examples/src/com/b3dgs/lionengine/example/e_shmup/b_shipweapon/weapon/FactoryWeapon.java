package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.game.projectile.FactoryLauncherGame;

/**
 * Weapon factory.
 */
public final class FactoryWeapon
        extends FactoryLauncherGame<WeaponType, Weapon>
{
    /** Factory reference. */
    private final FactoryProjectile factory;
    /** Handler reference. */
    private final HandlerProjectile handler;

    /**
     * Constructor.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     */
    public FactoryWeapon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super();
        this.factory = factory;
        this.handler = handler;
    }

    /*
     * FactoryLauncherGame
     */
    
    @Override
    public Weapon createLauncher(WeaponType type)
    {
        switch (type)
        {
            case PULSE_CANNON:
                return new PulseCannon(factory, handler);
            case MISSILE_LAUNCHER:
                return new MissileLauncher(factory, handler);
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }
}
