package com.b3dgs.lionengine.game;

import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract entity factory. It performs a list of available entities from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class Factory
 *         extends FactoryGame&lt;TypeEntity, SetupGame&gt;
 * {
 *     public Factory()
 *     {
 *         super(TypeEntity.class);
 *         loadAll(TypeEntity.values());
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(TypeEntity id)
 *     {
 *         return new SetupGame(Media.get(&quot;directory&quot;, id + &quot;.xml&quot;));
 *     }
 * }
 * </pre>
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup entity type used.
 */
public abstract class FactoryGame<T extends Enum<T>, S extends SetupGame>
{
    /** Setups list. */
    private final Map<T, S> setups;

    /**
     * Create a new factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryGame(Class<T> keyType)
    {
        setups = new EnumMap<>(keyType);
    }

    /**
     * Load all setup from their list, considering an additional list of arguments for specific cases.
     * 
     * @param list The entities list from enumeration.
     */
    public void loadAll(T[] list)
    {
        for (final T type : list)
        {
            addSetup(type, createSetup(type));
        }
    }

    /**
     * Get setup instance.
     * 
     * @param id The entity id (as enumeration).
     * @return The setup instance.
     */
    protected abstract S createSetup(T id);

    /**
     * Add a setup reference at the specified id.
     * 
     * @param id The reference id.
     * @param setup The setup reference.
     */
    public void addSetup(T id, S setup)
    {
        setups.put(id, setup);
    }

    /**
     * Get a setup reference from its id.
     * 
     * @param id The reference id.
     * @return The setup reference.
     */
    public S getSetup(T id)
    {
        return setups.get(id);
    }
}
