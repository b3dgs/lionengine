package com.b3dgs.lionengine.game.projectile;

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;

/**
 * Handle projectile factory. Projectiles are instantiated from a list. This way it is easy to define different kind of
 * projectile.
 * 
 * @param <T> enum containing all projectile type.
 * @param <P> The projectile type used.
 * @param <S> setup entity type.
 */
public abstract class FactoryProjectileGame<T extends Enum<T>, P extends ProjectileGame<?, ?>, S extends SetupEntityGame>
        extends FactoryGame<T, S>
{
    /**
     * Create a new projectile factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryProjectileGame(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Create a projectile.
     * 
     * @param type The projectile enum.
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     *            Can be -1 to ignore it.
     * @param frame The projectile tile number (from surface).
     * @return The created projectile.
     */
    public abstract P createProjectile(T type, int id, int frame);
}
