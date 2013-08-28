package com.b3dgs.lionengine.game.entity;

import com.b3dgs.lionengine.game.FactoryGame;

/**
 * Abstract entity factory. It performs a list of available entities from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class FactoryEntity
 *         extends FactoryEntityGame&lt;TypeEntity, SetupEntityGame, EntityGame&gt;
 * {
 *     public FactoryEntity()
 *     {
 *         super(TypeEntity.class);
 *         loadAll(TypeEntity.values());
 *     }
 * 
 *     &#064;Override
 *     public EntityGame createEntity(TypeEntity id)
 *     {
 *         switch (id)
 *         {
 *             default:
 *                 throw new LionEngineException(&quot;Unknown entity: &quot; + id);
 *         }
 *     }
 * 
 *     &#064;Override
 *     protected SetupEntityGame createSetup(TypeEntity id)
 *     {
 *         return new SetupEntityGame(Media.get(&quot;directory&quot;, id + &quot;.xml&quot;));
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
