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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.map.CollisionFormula;
import com.b3dgs.lionengine.game.map.CollisionFormulaLinear;
import com.b3dgs.lionengine.game.map.CollisionFormulaType;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision formula from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 */
public class ConfigCollisionFormula
{
    /** Formula node. */
    public static final String FORMULA = Configurer.PREFIX + "formula";
    /** Type attribute. */
    public static final String TYPE = "type";
    /** A attribute. */
    public static final String A = "a";
    /** B attribute. */
    public static final String B = "b";
    /** Type error. */
    private static final String ERROR_TYPE = "Unknown type: ";

    /**
     * Create the collision formula.
     * 
     * @param parent The parent reference.
     * @return The collision input instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static CollisionFormula create(XmlNode parent) throws LionEngineException
    {
        final XmlNode node = parent.getChild(FORMULA);
        final String name = node.readString(TYPE);
        try
        {
            final CollisionFormulaType type = CollisionFormulaType.valueOf(name);
            switch (type)
            {
                case LINEAR:
                    return new CollisionFormulaLinear(node.readDouble(A), node.readDouble(B));
                default:
                    throw new LionEngineException(ERROR_TYPE, name);
            }
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(ERROR_TYPE, name);
        }
    }
}
