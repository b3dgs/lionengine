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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the action data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ActionConfig
{
    /** Action node name. */
    public static final String NODE_ACTION = Constant.XML_PREFIX + "action";
    /** Action attribute name. */
    public static final String ATT_NAME = "name";
    /** Action attribute description. */
    public static final String ATT_DESCRIPTION = "description";
    /** Action attribute x. */
    public static final String ATT_X = "x";
    /** Action attribute y. */
    public static final String ATT_Y = "y";
    /** Action attribute width. */
    public static final String ATT_WIDTH = "width";
    /** Action attribute height. */
    public static final String ATT_HEIGHT = "height";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 65;

    /**
     * Import the action data from setup.
     *
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The action data.
     * @throws LionEngineException If unable to read node.
     */
    public static ActionConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Import the action data from node.
     *
     * @param root The root node reference (must not be <code>null</code>).
     * @return The action data.
     * @throws LionEngineException If unable to read node.
     */
    public static ActionConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml nodeAction = root.getChild(NODE_ACTION);
        final String name = nodeAction.readString(ATT_NAME);
        final String description = nodeAction.readString(ATT_DESCRIPTION);
        final int x = nodeAction.readInteger(ATT_X);
        final int y = nodeAction.readInteger(ATT_Y);
        final int width = nodeAction.readInteger(ATT_WIDTH);
        final int height = nodeAction.readInteger(ATT_HEIGHT);

        return new ActionConfig(name, description, x, y, width, height);
    }

    /**
     * Export the action node from data.
     *
     * @param config The config reference (must not be <code>null</code>).
     * @return The action node.
     * @throws LionEngineException If unable to save.
     */
    public static Xml exports(ActionConfig config)
    {
        Check.notNull(config);

        final Xml nodeAction = new Xml(NODE_ACTION);
        nodeAction.writeString(ATT_NAME, config.getName());
        nodeAction.writeString(ATT_DESCRIPTION, config.getDescription());
        nodeAction.writeInteger(ATT_X, config.getX());
        nodeAction.writeInteger(ATT_Y, config.getY());
        nodeAction.writeInteger(ATT_WIDTH, config.getWidth());
        nodeAction.writeInteger(ATT_HEIGHT, config.getHeight());

        return nodeAction;
    }

    /** Action name. */
    private final String name;
    /** Action description. */
    private final String description;
    /** Horizontal location on screen. */
    private final int x;
    /** Vertical location on screen. */
    private final int y;
    /** Width on screen. */
    private final int width;
    /** Height on screen. */
    private final int height;

    /**
     * Create action from configuration media.
     *
     * @param name The action name (must not be <code>null</code>).
     * @param description The action description (must not be <code>null</code>).
     * @param x The horizontal location on screen.
     * @param y The vertical location on screen.
     * @param width The button width.
     * @param height The button height.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public ActionConfig(String name, String description, int x, int y, int width, int height)
    {
        super();

        Check.notNull(name);
        Check.notNull(description);

        this.name = name;
        this.description = description;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the action name.
     *
     * @return The action name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the action description.
     *
     * @return The action description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get the button horizontal location.
     *
     * @return The button horizontal location.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get the button vertical location.
     *
     * @return The button vertical location.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get the button width.
     *
     * @return The button width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the button height.
     *
     * @return The button height.
     */
    public int getHeight()
    {
        return height;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + description.hashCode();
        result = prime * result + height;
        result = prime * result + name.hashCode();
        result = prime * result + width;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final ActionConfig other = (ActionConfig) object;
        final boolean sameSize = other.getWidth() == getWidth() && other.getHeight() == getHeight();
        final boolean sameCoord = other.getX() == getX() && other.getY() == getY();
        return sameSize
               && sameCoord
               && other.getDescription().equals(getDescription())
               && other.getName().equals(getName());
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [name=")
                                            .append(name)
                                            .append(", description=")
                                            .append(description)
                                            .append(", x=")
                                            .append(x)
                                            .append(", y=")
                                            .append(y)
                                            .append(", width=")
                                            .append(width)
                                            .append(", height=")
                                            .append(height)
                                            .append("]")
                                            .toString();
    }
}
