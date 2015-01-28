/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile collisions data from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Collision
 * @see XmlNode
 */
public class ConfigCollisionTile
{
    /** Tile collision node. */
    public static final String COLLISION = Configurer.PREFIX + "tileCollision";
    /** Collision name attribute. */
    public static final String NAME = "name";
    /** Tile pattern attribute. */
    public static final String PATTERN = "pattern";
    /** Starting tile number attribute. */
    public static final String START = "start";
    /** Ending tile number attribute. */
    public static final String END = "end";
    /** Tile collision ref node. */
    public static final String REF = Configurer.PREFIX + "tileCollisionRef";

    /**
     * Create the size node.
     * 
     * @param root The root reference.
     * @return The config collisions instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static Collection<ConfigCollisionTile> create(XmlNode root) throws LionEngineException
    {
        final Collection<ConfigCollisionTile> collisions = new ArrayList<>();
        for (final XmlNode node : root.getChildren(COLLISION))
        {
            final Collection<String> refs = new ArrayList<>();
            for (final XmlNode current : node.getChildren(REF))
            {
                refs.add(current.getText());
            }
            final ConfigCollisionTile collision = new ConfigCollisionTile(node.readString(NAME),
                    node.readInteger(PATTERN), node.readInteger(START), node.readInteger(END), refs);
            collisions.add(collision);
        }
        return collisions;
    }

    /** Name. */
    private final String name;
    /** Tile pattern. */
    private final int pattern;
    /** Starting tile. */
    private final int start;
    /** Ending tile. */
    private final int end;
    /** Tile collision ref. */
    private final Collection<String> ref;

    /**
     * Load collisions from configuration media.
     * 
     * @param name The tile collision name.
     * @param pattern The tile pattern.
     * @param start The starting tile number.
     * @param end The ending tile number.
     * @param ref The collisions ref names.
     */
    private ConfigCollisionTile(String name, int pattern, int start, int end, Collection<String> ref)
    {
        this.name = name;
        this.pattern = pattern;
        this.start = start;
        this.end = end;
        this.ref = ref;
    }

    /**
     * Get the collision name.
     * 
     * @return The collision name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the pattern value.
     * 
     * @return The pattern value.
     */
    public int getPattern()
    {
        return pattern;
    }

    /**
     * Get the starting tile number.
     * 
     * @return The starting tile number.
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Get the ending tile number.
     * 
     * @return The ending tile number.
     */
    public int getEnd()
    {
        return end;
    }

    /**
     * Get collisions references.
     * 
     * @return The collision references.
     */
    public Collection<String> getRef()
    {
        return ref;
    }
}
