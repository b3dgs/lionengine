package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * @param <T> The weapon enum type used.
 * @param <E> The entity type used.
 * @param <W> The weapon type used.
 * @param <A> The attacker type used.
 */
public abstract class FactoryWeaponRts<T extends Enum<T>, E extends EntityRts, W extends WeaponServices<E>, A extends AttackerUsedServices<E>>
        extends FactoryGame<T, SetupGame>
{
    /**
     * Create a new entity factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryWeaponRts(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Get the entity instance from its id. It is recommended to use a switch on the id, and throw an exception for the
     * default case (instead of returning a <code>null</code> value).
     * 
     * @param id The entity id (as enumeration).
     * @param user The weapon user.
     * @return The entity instance.
     */
    public abstract W createWeapon(T id, A user);
}
