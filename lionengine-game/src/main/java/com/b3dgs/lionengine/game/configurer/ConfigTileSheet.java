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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile group data from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ConfigTileSheet
{
    /** Configuration file name. */
    public static final String FILENAME = "sheets.xml";
    /** Tile size node. */
    public static final String NODE_TILE_SIZE = Configurer.PREFIX + "tileSize";
    /** Tile width attribute. */
    public static final String ATTRIBUTE_TILE_WIDTH = "width";
    /** Tile height attribute. */
    public static final String ATTRIBUTE_TILE_HEIGHT = "height";
    /** Tile sheets node. */
    public static final String NODE_TILE_SHEETS = Configurer.PREFIX + "sheets";
    /** Tile sheet node. */
    public static final String NODE_TILE_SHEET = Configurer.PREFIX + "sheet";

    /**
     * Create the sheets data from node.
     * 
     * @param sheetsConfig The file that define the sheets configuration.
     * @return The tile sheet configuration.
     * @throws LionEngineException If unable to read node.
     */
    public static ConfigTileSheet create(Media sheetsConfig) throws LionEngineException
    {
        final Collection<String> sheets = new ArrayList<String>();
        final XmlNode root = Stream.loadXml(sheetsConfig);

        final XmlNode tileSize = root.getChild(NODE_TILE_SIZE);
        final int tileWidth = tileSize.readInteger(ATTRIBUTE_TILE_WIDTH);
        final int tileHeight = tileSize.readInteger(ATTRIBUTE_TILE_HEIGHT);

        for (final XmlNode child : root.getChildren(NODE_TILE_SHEET))
        {
            sheets.add(child.getText());
        }

        final ConfigTileSheet config = new ConfigTileSheet(tileWidth, tileHeight, sheets);
        return config;
    }

    /** Tile width. */
    private final int tileWidth;
    /** Tile height. */
    private final int tileHeight;
    /** Sheets defined. */
    private final Collection<String> sheets;

    /**
     * Create config.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param sheets The defined sheets.
     */
    private ConfigTileSheet(int tileWidth, int tileHeight, Collection<String> sheets)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sheets = sheets;
    }

    /**
     * Get the tile width.
     * 
     * @return The tile width.
     */
    public int getTileWidth()
    {
        return tileWidth;
    }

    /**
     * Get the tile height.
     * 
     * @return The tile height.
     */
    public int getTileHeight()
    {
        return tileHeight;
    }

    /**
     * Get the sheets defined.
     * 
     * @return The sheets defined.
     */
    public Collection<String> getSheets()
    {
        return sheets;
    }
}
