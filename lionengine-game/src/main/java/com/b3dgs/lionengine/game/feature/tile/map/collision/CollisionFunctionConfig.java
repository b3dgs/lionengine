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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Represents the collision function.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollisionFunction
 */
public final class CollisionFunctionConfig
{
    /** Function node. */
    public static final String FUNCTION = Constant.XML_PREFIX + "function";
    /** Type attribute. */
    public static final String TYPE = "type";
    /** A attribute. */
    public static final String A = "a";
    /** B attribute. */
    public static final String B = "b";
    /** Type error. */
    static final String ERROR_TYPE = "Unknown type: ";

    /**
     * Create the collision function from node.
     * 
     * @param parent The parent reference (must not be <code>null</code>).
     * @return The collision function data.
     * @throws LionEngineException If error when reading node.
     */
    public static CollisionFunction imports(XmlReader parent)
    {
        Check.notNull(parent);

        final XmlReader node = parent.getChild(FUNCTION);
        final CollisionFunctionType type = node.getEnum(CollisionFunctionType.class, TYPE);
        try
        {
            switch (type)
            {
                case LINEAR:
                    return new CollisionFunctionLinear(node.getDouble(A), node.getDouble(B));
                // $CASES-OMITTED$
                default:
                    throw new LionEngineException(type);
            }
        }
        catch (final IllegalArgumentException | NullPointerException exception)
        {
            throw new LionEngineException(exception, ERROR_TYPE + type);
        }
    }

    /**
     * Export the collision function data as a node.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param function The collision function to export (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionFunction function)
    {
        Check.notNull(root);
        Check.notNull(function);

        final Xml node = root.createChild(FUNCTION);
        if (function instanceof CollisionFunctionLinear)
        {
            final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
            node.writeEnum(TYPE, CollisionFunctionType.LINEAR);
            node.writeDouble(A, linear.getA());
            node.writeDouble(B, linear.getB());
        }
        else
        {
            node.writeEnum(TYPE, function.getType());
        }
    }

    /**
     * Disabled constructor.
     */
    private CollisionFunctionConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
