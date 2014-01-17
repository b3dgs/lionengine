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
package com.b3dgs.lionengine.game;

/**
 * Represents the list of object types that can be instantiated by a {@link FactoryObjectGame}.
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * public enum Type implements ObjectType
 * {
 *     TYPE_ONE(TypeOne.class),
 *     TYPE_TWO(TypeTwo.class);
 * 
 *     private final Class&lt;?&gt; target;
 *     private final String pathName;
 * 
 *     private Type(Class&lt;?&gt; target)
 *     {
 *         this.target = target;
 *         pathName = ObjectTypeUtility.getPathName(this);
 *     }
 * 
 *     &#064;Override
 *     public Class&lt;?&gt; getTargetClass()
 *     {
 *         return target;
 *     }
 * 
 *     &#064;Override
 *     public String getPathName()
 *     {
 *         return pathName;
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see SetupGame
 */
public interface ObjectType
{
    /**
     * Get the target class (which can be instantiated by a {@link FactoryObjectGame}). The target class should have a
     * constructor like the main one: {@link ObjectGame#ObjectGame(SetupGame)}.
     * 
     * @return The target class.
     */
    public Class<?> getTargetClass();

    /**
     * Get the name as a path (which is used to point the XML data file). The method
     * {@link ObjectTypeUtility#getPathName(Enum)} can provide a default implementation.
     * 
     * @return The path name.
     */
    public String getPathName();
}
