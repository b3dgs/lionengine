package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Abstract effect factory. It performs a list of available effects from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup type.
 * @param <E> The effect type.
 */
public abstract class FactoryEffectGame<T extends Enum<T>, S extends SetupSurfaceGame, E extends EffectGame>
        extends FactoryGame<T, S>
{
    /**
     * Create a new effect factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryEffectGame(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Get the effect instance from its id. It is recommended to use a switch on the id, and throw an exception for the
     * default case (instead of returning a <code>null</code> value).
     * 
     * @param id The effect id (as enumeration).
     * @return The effect instance.
     */
    public abstract E createEffect(T id);
}
