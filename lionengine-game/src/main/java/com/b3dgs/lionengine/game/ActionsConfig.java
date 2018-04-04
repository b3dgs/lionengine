/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;

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
    /** Action node reference. */
    public static final String NODE_ACTION = Constant.XML_PREFIX + "action";
    /** Action path attribute name. */
    public static final String ATT_PATH = "path";
    /** Action cancel attribute name. */
    public static final String ATT_CANCEL = "cancel";

    /**
     * Create the action data from configurer.
     *
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The actions data.
     * @throws LionEngineException If unable to read node.
     */
    public static List<ActionRef> imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the action data from node.
     *
     * @param root The root reference (must not be <code>null</code>).
     * @return The allowed actions.
     * @throws LionEngineException If unable to read node.
     */
    public static List<ActionRef> imports(Xml root)
    {
        Check.notNull(root);

        if (!root.hasChild(NODE_ACTIONS))
        {
            return Collections.emptyList();
        }
        final Xml node = root.getChild(NODE_ACTIONS);

        return getRefs(node);
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
            final Xml nodeAction = node.createChild(NODE_ACTION);
            nodeAction.writeString(ATT_PATH, action.getPath());
            if (action.hasCancel())
            {
                nodeAction.writeBoolean(ATT_CANCEL, true);
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
     * @return The actions found.
     */
    private static List<ActionRef> getRefs(Xml node)
    {
        final Collection<Xml> children = node.getChildren(NODE_ACTION);
        final List<ActionRef> actions = new ArrayList<>(children.size());

        for (final Xml action : children)
        {
            final String path = action.readString(ATT_PATH);
            final boolean cancel = action.readBoolean(false, ATT_CANCEL);
            actions.add(new ActionRef(path, cancel, getRefs(action)));
        }

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
        final Xml nodeAction = node.createChild(NODE_ACTION);
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
