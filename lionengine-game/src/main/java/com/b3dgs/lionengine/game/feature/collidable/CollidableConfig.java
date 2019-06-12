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
    /** Collidable group node. */
    public static final String NODE_GROUP = Constant.XML_PREFIX + "group";
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

        if (configurer.hasNode(NODE_GROUP))
        {
            final String group = configurer.getText(NODE_GROUP);
            try
            {
                return Integer.valueOf(group);
            }
            catch (final NumberFormatException exception)
            {
                throw new LionEngineException(exception, ERROR_INVALID_GROUP + group);
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

        final Xml node = root.createChild(NODE_GROUP);
        node.setText(collidable.getGroup().toString());
    }

    /**
     * Private constructor.
     */
    private CollidableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
