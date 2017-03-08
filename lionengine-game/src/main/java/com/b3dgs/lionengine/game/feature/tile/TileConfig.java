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
package com.b3dgs.lionengine.game.feature.tile;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the tile reference from a configurer.
 * 
 * @see TileRef
 */
public final class TileConfig
{
    /** Tile node. */
    public static final String NODE_TILE = Constant.XML_PREFIX + "tile";
    /** Sheet attribute. */
    public static final String ATT_TILE_SHEET = "sheet";
    /** Number attribute. */
    public static final String ATT_TILE_NUMBER = "number";

    /**
     * Create the tile data from node.
     * 
     * @param nodeTile The node reference.
     * @return The tile data.
     * @throws LionEngineException If <code>null</code> argument or error when reading.
     */
    public static TileRef create(Xml nodeTile)
    {
        Check.notNull(nodeTile);

        final int sheet = nodeTile.readInteger(ATT_TILE_SHEET);
        final int number = nodeTile.readInteger(ATT_TILE_NUMBER);
        return new TileRef(sheet, number);
    }

    /**
     * Export the tile as a node.
     * 
     * @param tileRef The tile to export.
     * @return The exported node.
     * @throws LionEngineException If <code>null</code> argument or error on writing.
     */
    public static Xml export(TileRef tileRef)
    {
        Check.notNull(tileRef);

        final Xml node = new Xml(NODE_TILE);
        node.writeInteger(ATT_TILE_SHEET, tileRef.getSheet().intValue());
        node.writeInteger(ATT_TILE_NUMBER, tileRef.getNumber());

        return node;
    }

    /**
     * Disabled constructor.
     */
    private TileConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
