/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.configurer;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the action data from a configurer.
 *
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Action
 */
public class ConfigAction
{
    /** Action node name. */
    public static final String ACTION = Configurer.PREFIX + "action";
    /** Action attribute name. */
    public static final String NAME = "name";
    /** Action attribute description. */
    public static final String DESCRIPTION = "description";
    /** Action attribute x. */
    public static final String X = "x";
    /** Action attribute y. */
    public static final String Y = "y";
    /** Action attribute width. */
    public static final String WIDTH = "width";
    /** Action attribute height. */
    public static final String HEIGHT = "height";

    /**
     * Create the action data from node.
     *
     * @param configurer The configurer reference.
     * @return The action data.
     * @throws LionEngineException If unable to read node.
     */
    public static ConfigAction create(Configurer configurer) throws LionEngineException
    {
        final XmlNode node = configurer.getRoot();
        return new ConfigAction(node.readString(NAME), node.readString(DESCRIPTION), node.readInteger(X),
                node.readInteger(Y), node.readInteger(WIDTH), node.readInteger(HEIGHT));
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
     * @param name The action name.
     * @param description The action description.
     * @param x The horizontal location on screen.
     * @param y The vertical location on screen.
     * @param width The button width.
     * @param height The button height.
     * @throws LionEngineException If error when opening the media.
     */
    private ConfigAction(String name, String description, int x, int y, int width, int height)
    {
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
}
