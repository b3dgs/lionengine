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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Minimap;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the minimap data from an XML node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Minimap
 */
public final class ConfigMinimap
{
    /** Minimaps root node. */
    public static final String MINIMAPS = Configurer.PREFIX + "minimaps";
    /** Minimap node. */
    public static final String MINIMAP = Configurer.PREFIX + "minimap";
    /** Red name attribute. */
    public static final String R = "r";
    /** Green name attribute. */
    public static final String G = "g";
    /** Blue name attribute. */
    public static final String B = "b";

    /**
     * Create the minimap data from node.
     * 
     * @param root The node root reference.
     * @param map The map reference.
     * @return The minimap data.
     * @throws LionEngineException If unable to read node.
     */
    public static Map<String, ColorRgba> create(XmlNode root, MapTile map) throws LionEngineException
    {
        final Map<String, ColorRgba> colors = new HashMap<>();
        for (final XmlNode node : root.getChildren(MINIMAP))
        {
            final ColorRgba color = new ColorRgba(node.readInteger(R), node.readInteger(G), node.readInteger(B));
            final String group = node.getChild(ConfigTileGroup.GROUP).getText();
            colors.put(map.getGroup(group).getName(), color);
        }
        return colors;
    }

    /**
     * Disabled Constructor.
     */
    private ConfigMinimap()
    {
        throw new RuntimeException();
    }
}
