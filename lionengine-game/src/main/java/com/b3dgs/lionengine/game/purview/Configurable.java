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
package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Purview representing an object which can be externally configured. When data are loaded, the object can used
 * internally theses data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Configurable
{
    /**
     * Load data from configuration media.
     * 
     * @param media The xml media.
     */
    void loadData(Media media);

    /**
     * Get the data root container for raw access.
     * 
     * @return The root node.
     */
    XmlNode getDataRoot();

    /**
     * Get a string in the xml tree.
     * 
     * @param attribute The attribute to get as string.
     * @param path The node path (child list)
     * @return The string value.
     */
    String getDataString(String attribute, String... path);

    /**
     * Get an integer in the xml tree.
     * 
     * @param attribute The attribute to get as integer.
     * @param path The node path (child list)
     * @return The integer value.
     */
    int getDataInteger(String attribute, String... path);

    /**
     * Get a boolean in the xml tree.
     * 
     * @param attribute The attribute to get as boolean.
     * @param path The node path (child list)
     * @return The boolean value.
     */
    boolean getDataBoolean(String attribute, String... path);

    /**
     * Get a double in the xml tree.
     * 
     * @param attribute The attribute to get as double.
     * @param path The node path (child list)
     * @return The double value.
     */
    double getDataDouble(String attribute, String... path);

    /**
     * Get an animation data from its name.
     * 
     * @param name The animation name.
     * @return The animation reference.
     */
    Animation getDataAnimation(String name);

    /**
     * Get a collision data from its name.
     * 
     * @param name The collision name.
     * @return The collision reference.
     */
    CollisionData getDataCollision(String name);
}
