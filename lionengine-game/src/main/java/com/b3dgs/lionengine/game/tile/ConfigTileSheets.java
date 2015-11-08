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
package com.b3dgs.lionengine.game.tile;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile sheets data from a configurer.
 */
public final class ConfigTileSheets
{
    /** Configuration file name. */
    public static final String FILENAME = "sheets.xml";
    /** Tile sheets node. */
    public static final String NODE_TILE_SHEETS = Configurer.PREFIX + "sheets";
    /** Tile size node. */
    public static final String NODE_TILE_SIZE = Configurer.PREFIX + "tileSize";
    /** Tile width attribute. */
    public static final String ATTRIBUTE_TILE_WIDTH = "width";
    /** Tile height attribute. */
    public static final String ATTRIBUTE_TILE_HEIGHT = "height";
    /** Tile sheet node. */
    public static final String NODE_TILE_SHEET = Configurer.PREFIX + "sheet";

    /**
     * Import the sheets data from configuration.
     * 
     * @param configSheets The file that define the sheets configuration.
     * @return The tile sheet configuration.
     * @throws LionEngineException If unable to read data.
     */
    public static ConfigTileSheets imports(Media configSheets)
    {
        final XmlNode nodeSheets = Xml.load(configSheets);

        final XmlNode nodeTileSize = nodeSheets.getChild(NODE_TILE_SIZE);
        final int tileWidth = nodeTileSize.readInteger(ATTRIBUTE_TILE_WIDTH);
        final int tileHeight = nodeTileSize.readInteger(ATTRIBUTE_TILE_HEIGHT);

        final Collection<String> sheets = importSheets(nodeSheets);

        return new ConfigTileSheets(tileWidth, tileHeight, sheets);
    }

    /**
     * Export the sheets configuration.
     * 
     * @param configSheets The export media.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param sheets The sheets filename.
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Media configSheets, int tileWidth, int tileHeight, Collection<String> sheets)
    {
        final XmlNode nodeSheets = Xml.create(NODE_TILE_SHEETS);
        nodeSheets.writeString(Configurer.HEADER, Engine.WEBSITE);

        final XmlNode tileSize = nodeSheets.createChild(NODE_TILE_SIZE);
        tileSize.writeString(ATTRIBUTE_TILE_WIDTH, String.valueOf(tileWidth));
        tileSize.writeString(ATTRIBUTE_TILE_HEIGHT, String.valueOf(tileHeight));

        exportSheets(nodeSheets, sheets);

        Xml.save(nodeSheets, configSheets);
    }

    /**
     * Import the defined sheets.
     * 
     * @param nodeSheets The sheets node.
     * @return The sheets filename.
     */
    private static Collection<String> importSheets(XmlNode nodeSheets)
    {
        final Collection<String> sheets = new ArrayList<String>();
        for (final XmlNode nodeSheet : nodeSheets.getChildren(NODE_TILE_SHEET))
        {
            final String sheetFilename = nodeSheet.getText();
            sheets.add(sheetFilename);
        }
        return sheets;
    }

    /**
     * Export the defined sheets.
     * 
     * @param nodeSheets Sheets node.
     * @param sheets Sheets defined.
     */
    private static void exportSheets(XmlNode nodeSheets, Collection<String> sheets)
    {
        for (final String sheet : sheets)
        {
            final XmlNode nodeSheet = nodeSheets.createChild(NODE_TILE_SHEET);
            nodeSheet.setText(sheet);
        }
    }

    /** Tile width. */
    private final int tileWidth;
    /** Tile height. */
    private final int tileHeight;
    /** Sheets filename. */
    private final Collection<String> sheets;

    /**
     * Create config.
     * 
     * @param tileWidth The tile width (strictly positive).
     * @param tileHeight The tile height (strictly positive).
     * @param sheets The defined sheets.
     * @throws LionEngineException If invalid size or sheets is <code>null</code>.
     */
    private ConfigTileSheets(int tileWidth, int tileHeight, Collection<String> sheets)
    {
        Check.range(Range.INT_POSITIVE_STRICT, tileWidth);
        Check.range(Range.INT_POSITIVE_STRICT, tileHeight);
        Check.notNull(sheets);

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
     * Get the sheets filename defined.
     * 
     * @return The sheets filename defined.
     */
    public Collection<String> getSheets()
    {
        return sheets;
    }
}
