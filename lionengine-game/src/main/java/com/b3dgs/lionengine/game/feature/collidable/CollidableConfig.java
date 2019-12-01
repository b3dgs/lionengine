/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.collidable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the collidable data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Collidable
 */
public final class CollidableConfig
{
    /** Collidable node. */
    public static final String NODE_COLLIDABLE = Constant.XML_PREFIX + "collidable";
    /** Collidable group attribute name. */
    public static final String ATT_GROUP = "group";
    /** Default group. */
    public static final Integer DEFAULT_GROUP = Integer.valueOf(0);
    /** Error invalid group. */
    static final String ERROR_INVALID_GROUP = "Invalid group: ";

    /**
     * Create the collidable data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The associated group, {@link #DEFAULT_GROUP} if not defined.
     * @throws LionEngineException If unable to read node.
     */
    public static Integer imports(Configurer configurer)
    {
        Check.notNull(configurer);

        if (configurer.hasNode(NODE_COLLIDABLE))
        {
            try
            {
                return Integer.valueOf(configurer.getIntegerDefault(DEFAULT_GROUP.intValue(),
                                                                    ATT_GROUP,
                                                                    NODE_COLLIDABLE));
            }
            catch (final NumberFormatException exception)
            {
                throw new LionEngineException(exception, ERROR_INVALID_GROUP);
            }
        }
        return DEFAULT_GROUP;
    }

    /**
     * Create an XML node from a collidable.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param collidable The collidable reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public static void exports(Xml root, Collidable collidable)
    {
        Check.notNull(root);
        Check.notNull(collidable);

        final Xml node = root.createChild(NODE_COLLIDABLE);
        node.writeInteger(ATT_GROUP, collidable.getGroup().intValue());
    }

    /**
     * Private constructor.
     */
    private CollidableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
