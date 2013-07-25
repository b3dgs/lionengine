package com.b3dgs.lionengine.game.entity;

import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.SetupEntityGame;

/**
 * Abstract entity factory. It performs a list of available entities from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class FactoryEntity
 *         extends FactoryEntityGame&lt;TypeEntity, SetupGame, Entity&gt;
 * {
 *     public FactoryEntity()
 *     {
 *         super();
 *         loadAll(TypeEntity.values());
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(TypeEntity id)
 *     {
 *         return new SetupGame(new ConfigurableModel(), Media.get(&quot;directory&quot;, id + &quot;.xml&quot;), false);
 *     }
 * 
 *     &#064;Override
 *     public Entity createEntity(TypeEntity type)
 *     {
 *         switch (type)
 *         {
 *             default:
 *                 throw new LionEngineException(&quot;Entity not found: &quot; + type.name());
 *         }
 *     }
 * }
 * </pre>
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup type.
 * @param <E> The entity type.
 */
public abstract class FactoryEntityGame<T extends Enum<T>, S extends SetupEntityGame, E extends EntityGame>
        extends FactoryGame<T, S>
{
    /**
     * Create a new entity factory.
     * 
     * @param keyType The class of the enum type defined.
     */
    public FactoryEntityGame(Class<T> keyType)
    {
        super(keyType);
    }

    /**
     * Get the entity instance from its id. It is recommended to use a switch on the id, and throw an exception for the
     * default case (instead of returning a <code>null</code> value).
     * 
     * @param id The entity id (as enumeration).
     * @return The entity instance.
     */
    public abstract E createEntity(T id);
}
