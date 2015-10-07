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
import com.b3dgs.lionengine.game.map.TileRef;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile reference from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileRef
 */
public final class ConfigTileRef
{
    /** Tile ref node. */
    public static final String TILE_REF = Configurer.PREFIX + "tileRef";
    /** Sheet attribute. */
    public static final String SHEET = "sheet";
    /** Number attribute. */
    public static final String NUMBER = "number";

    /**
     * Create the tile ref data from node.
     * 
     * @param node The node reference.
     * @return The tile ref data.
     * @throws LionEngineException If error when reading node.
     */
    public static TileRef create(XmlNode node) throws LionEngineException
    {
        final Integer sheet = Integer.valueOf(node.readInteger(SHEET));
        final int number = node.readInteger(NUMBER);
        return new TileRef(sheet, number);
    }

    /**
     * Export the tile ref as a node.
     * 
     * @param tileRef The tile ref to export.
     * @return The exported node.
     * @throws LionEngineException If error on writing.
     */
    public static XmlNode export(TileRef tileRef) throws LionEngineException
    {
        final XmlNode node = Stream.createXmlNode(TILE_REF);
        node.writeInteger(SHEET, tileRef.getSheet().intValue());
        node.writeInteger(NUMBER, tileRef.getNumber());
        return node;
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileRef()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
