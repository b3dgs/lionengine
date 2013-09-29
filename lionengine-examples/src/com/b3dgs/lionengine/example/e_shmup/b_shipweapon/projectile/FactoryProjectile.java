package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.FactoryEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.HandlerEffect;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;

/**
 * Factory projectile.
 */
public final class FactoryProjectile
        extends FactoryProjectileGame<ProjectileType, Projectile, SetupSurfaceGame>
{
    /** Weapon surfaces. */
    private final SetupSurfaceGame setup;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The factory effect reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryProjectile(FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(ProjectileType.class);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        setup = new SetupSurfaceGame(Media.get("sprites", "weapons.xml"));
        loadAll(ProjectileType.values());
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(ProjectileType type, int id, int frame)
    {
        switch (type)
        {
            case BULLET:
                return new Bullet(getSetup(type), id, frame);
            case MISSILE:
                return new Missile(factoryEffect, handlerEffect, getSetup(type), id, frame);
            default:
                throw new LionEngineException("Unknown type: " + type);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(ProjectileType id)
    {
        return setup;
    }
}
