/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the state data from a configurer.
 */
public final class StateConfig
{
    /** Rasterable root node. */
    public static final String NODE_STATE = Constant.XML_PREFIX + "state";

    /**
     * Create the data from configurer.
     *
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The data.
     * @throws LionEngineException If unable to read node.
     */
    public static Optional<String> imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the data from node.
     *
     * @param root The root reference (must not be <code>null</code>).
     * @return The state value.
     * @throws LionEngineException If unable to read node.
     */
    public static Optional<String> imports(Xml root)
    {
        Check.notNull(root);

        if (root.hasChild(NODE_STATE))
        {
            final Xml node = root.getChild(NODE_STATE);
            return Optional.of(node.getText());
        }
        return Optional.empty();
    }

    /**
     * Export the node from config.
     *
     * @param state The state reference (must not be <code>null</code>).
     * @return The node.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(Class<?> state)
    {
        Check.notNull(state);

        final Xml node = new Xml(NODE_STATE);
        node.setText(state.getName());

        return node;
    }

    /**
     * Private constructor.
     */
    private StateConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
