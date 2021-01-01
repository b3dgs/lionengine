/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the fovable data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FovableConfig
{
    /** Fovable node name. */
    public static final String NODE_FOVABLE = Constant.XML_PREFIX + "fovable";
    /** Field of view attribute. */
    public static final String ATT_FOV = "fov";
    /** Default field of view. */
    private static final int DEFAULT_FOV = 0;

    /**
     * Create data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static int imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the data from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static int imports(Xml root)
    {
        Check.notNull(root);

        if (root.hasChild(NODE_FOVABLE))
        {
            final Xml node = root.getChild(NODE_FOVABLE);
            return node.readInteger(DEFAULT_FOV, ATT_FOV);
        }
        return DEFAULT_FOV;
    }

    /**
     * Create the data from node.
     * 
     * @param fov The field of view value.
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(int fov)
    {
        final Xml node = new Xml(NODE_FOVABLE);
        node.writeInteger(ATT_FOV, fov);

        return node;
    }

    /**
     * Private.
     */
    private FovableConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
