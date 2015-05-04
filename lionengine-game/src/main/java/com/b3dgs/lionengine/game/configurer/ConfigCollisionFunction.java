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
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.CollisionFunctionType;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision function from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionFunction
 */
public final class ConfigCollisionFunction
{
    /** Function node. */
    public static final String FUNCTION = Configurer.PREFIX + "function";
    /** Type attribute. */
    public static final String TYPE = "type";
    /** A attribute. */
    public static final String A = "a";
    /** B attribute. */
    public static final String B = "b";
    /** Type error. */
    private static final String ERROR_TYPE = "Unknown type: ";

    /**
     * Create the collision function from node.
     * 
     * @param parent The parent reference.
     * @return The collision function data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionFunction create(XmlNode parent) throws LionEngineException
    {
        final XmlNode node = parent.getChild(FUNCTION);
        final String name = node.readString(TYPE);
        try
        {
            final CollisionFunctionType type = CollisionFunctionType.valueOf(name);
            switch (type)
            {
                case LINEAR:
                    return new CollisionFunctionLinear(node.readDouble(A), node.readDouble(B));
                default:
                    throw new LionEngineException(ERROR_TYPE, name);
            }
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(ERROR_TYPE, name);
        }
    }

    /**
     * Export the collision function data as a node.
     * 
     * @param function The collision function to export.
     * @return The node reference.
     */
    public static XmlNode export(CollisionFunction function)
    {
        final XmlNode node = Stream.createXmlNode(FUNCTION);
        if (function instanceof CollisionFunctionLinear)
        {
            final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
            node.writeString(TYPE, CollisionFunctionType.LINEAR.name());
            node.writeDouble(A, linear.getA());
            node.writeDouble(B, linear.getB());
        }
        return node;
    }

    /**
     * Disabled constructor.
     */
    private ConfigCollisionFunction()
    {
        throw new RuntimeException();
    }
}
