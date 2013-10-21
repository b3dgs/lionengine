/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Abstract entity factory. It performs a list of available entities from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class FactoryEntity
 *         extends FactoryEntityGame&lt;EntityType, SetupSurfaceGame, EntityGame&gt;
 * {
 *     public FactoryEntity()
 *     {
 *         super(EntityType.class, EntityType.values(), &quot;entities&quot;);
 *         load();
 *     }
 * 
 *     &#064;Override
 *     public EntityGame createEntity(EntityType id)
 *     {
 *         switch (id)
 *         {
 *             default:
 *                 throw new LionEngineException(&quot;Unknown entity: &quot; + id);
 *         }
 *     }
 * 
 *     &#064;Override
 *     protected SetupSurfaceGame createSetup(EntityType key, Media config)
 *     {
 *         return new SetupSurfaceGame(config);
 *     }
 * }
 * </pre>
 * 
 * @param <T> The enum containing all type.
 * @param <S> The setup type used.
 * @param <E> The entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryEntityGame<T extends Enum<T> & ObjectType, S extends SetupSurfaceGame, E extends EntityGame>
        extends FactoryGame<T, S>
{
    /** Entities folder. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param types The types list.
     * @param folder The entities folder.
     */
    public FactoryEntityGame(Class<T> keyType, T[] types, String folder)
    {
        super(keyType, types);
        this.folder = folder;
    }

    /**
     * Get the entity instance from its key. It is recommended to use a switch on the key, and throw an exception for
     * the default case (instead of returning a <code>null</code> value).
     * 
     * @param type The entity type.
     * @return The entity instance.
     */
    public abstract E createEntity(T type);

    /**
     * Create a setup from its media.
     * 
     * @param type The entity type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract S createSetup(T type, Media config);

    /*
     * FactoryGame
     */

    @Override
    protected S createSetup(T type)
    {
        final Media config = Media.get(folder, type.asPathName() + ".xml");
        return createSetup(type, config);
    }
}
