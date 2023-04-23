/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents the action references data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ActionsConfig
{
    /** Actions root node. */
    public static final String NODE_ACTIONS = Constant.XML_PREFIX + "actions";
    /** Action reference node reference. */
    public static final String NODE_ACTION_REF = Constant.XML_PREFIX + "actionRef";
    /** Action path attribute name. */
    public static final String ATT_PATH = "path";
    /** Action cancel attribute name. */
    public static final String ATT_CANCEL = "cancel";
    /** Action unique attribute name. */
    public static final String ATT_UNIQUE = "unique";

    /**
     * Create the action data from configurer.
     *
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @param id The id supplier to handle unique instance if needed (must not be <code>null</code>).
     * @return The actions data.
     * @throws LionEngineException If unable to read node.
     */
    public static List<ActionRef> imports(Configurer configurer, Function<Class<? extends Feature>, Feature> id)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot(), id);
    }

    /**
     * Create the action data from node.
     *
     * @param root The root reference (must not be <code>null</code>).
     * @param id The id supplier to handle unique instance if needed (must not be <code>null</code>).
     * @return The allowed actions.
     * @throws LionEngineException If unable to read node.
     */
    public static List<ActionRef> imports(XmlReader root, Function<Class<? extends Feature>, Feature> id)
    {
        Check.notNull(root);

        if (!root.hasNode(NODE_ACTIONS))
        {
            return Collections.emptyList();
        }
        final XmlReader node = root.getChild(NODE_ACTIONS);

        return getRefs(node, id);
    }

    /**
     * Export the action node from config.
     *
     * @param actions The allowed actions (must not be <code>null</code>).
     * @return The actions node.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(Collection<ActionRef> actions)
    {
        Check.notNull(actions);

        final Xml node = new Xml(NODE_ACTIONS);

        for (final ActionRef action : actions)
        {
            final Xml nodeAction = node.createChild(NODE_ACTION_REF);
            nodeAction.writeString(ATT_PATH, action.getPath());
            if (action.hasCancel())
            {
                nodeAction.writeBoolean(ATT_CANCEL, true);
            }
            if (action.hasCancel())
            {
                nodeAction.writeBoolean(ATT_UNIQUE, true);
            }
            for (final ActionRef ref : action.getRefs())
            {
                exports(nodeAction, ref);
            }
        }

        return node;
    }

    /**
     * Get all actions and their references.
     * 
     * @param node The current node to check (must not be <code>null</code>).
     * @param id The id supplier to handle unique instance if needed (must not be <code>null</code>).
     * @return The actions found.
     */
    private static List<ActionRef> getRefs(XmlReader node, Function<Class<? extends Feature>, Feature> id)
    {
        final Collection<XmlReader> children = node.getChildren(NODE_ACTION_REF);
        final List<ActionRef> actions = new ArrayList<>(children.size());

        for (final XmlReader action : children)
        {
            final String path = action.getString(ATT_PATH);
            final boolean cancel = action.getBoolean(false, ATT_CANCEL);
            final boolean unique = action.getBoolean(false, ATT_UNIQUE);
            actions.add(new ActionRef(path, cancel, getRefs(action, id), unique ? id : null));
        }
        children.clear();

        return actions;
    }

    /**
     * Export the action.
     *
     * @param node The xml node (must not be <code>null</code>).
     * @param action The action to export (must not be <code>null</code>).
     * @throws LionEngineException If unable to write node.
     */
    private static void exports(Xml node, ActionRef action)
    {
        final Xml nodeAction = node.createChild(NODE_ACTION_REF);
        nodeAction.writeString(ATT_PATH, action.getPath());

        for (final ActionRef ref : action.getRefs())
        {
            exports(nodeAction, ref);
        }
    }

    /**
     * Private constructor.
     */
    private ActionsConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
