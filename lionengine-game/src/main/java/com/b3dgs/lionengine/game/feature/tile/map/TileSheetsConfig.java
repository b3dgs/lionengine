/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;

/**
 * Represents the tile sheets data.
 */
public final class TileSheetsConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "sheets.xml";
    /** Tile sheets node. */
    public static final String NODE_TILE_SHEETS = Constant.XML_PREFIX + "sheets";
    /** Tile size node. */
    public static final String NODE_TILE_SIZE = Constant.XML_PREFIX + "tileSize";
    /** Tile width attribute. */
    public static final String ATT_TILE_WIDTH = "width";
    /** Tile height attribute. */
    public static final String ATT_TILE_HEIGHT = "height";
    /** Tile sheet node. */
    public static final String NODE_TILE_SHEET = Constant.XML_PREFIX + "sheet";

    /**
     * Import the sheets data from configuration.
     * 
     * @param configSheets The file that define the sheets configuration (must not be <code>null</code>).
     * @return The tile sheet configuration.
     * @throws LionEngineException If unable to read data.
     */
    public static TileSheetsConfig imports(Media configSheets)
    {
        final Xml nodeSheets = new Xml(configSheets);

        final Xml nodeTileSize = nodeSheets.getChild(NODE_TILE_SIZE);
        final int tileWidth = nodeTileSize.readInteger(ATT_TILE_WIDTH);
        final int tileHeight = nodeTileSize.readInteger(ATT_TILE_HEIGHT);

        final List<String> sheets = importSheets(nodeSheets);

        return new TileSheetsConfig(tileWidth, tileHeight, sheets);
    }

    /**
     * Export the sheets configuration.
     * 
     * @param configSheets The export media (must not be <code>null</code>).
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param sheets The sheets filename (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Media configSheets, int tileWidth, int tileHeight, List<String> sheets)
    {
        Check.notNull(configSheets);
        Check.notNull(sheets);

        final Xml nodeSheets = new Xml(NODE_TILE_SHEETS);
        nodeSheets.writeString(Constant.XML_HEADER, Constant.ENGINE_WEBSITE);

        final Xml tileSize = nodeSheets.createChild(NODE_TILE_SIZE);
        tileSize.writeString(ATT_TILE_WIDTH, String.valueOf(tileWidth));
        tileSize.writeString(ATT_TILE_HEIGHT, String.valueOf(tileHeight));

        exportSheets(nodeSheets, sheets);

        nodeSheets.save(configSheets);
    }

    /**
     * Import the defined sheets.
     * 
     * @param nodeSheets The sheets node (must not be <code>null</code>).
     * @return The sheets filename.
     */
    private static List<String> importSheets(Xml nodeSheets)
    {
        final Collection<Xml> children = nodeSheets.getChildren(NODE_TILE_SHEET);
        final List<String> sheets = new ArrayList<>(children.size());

        for (final Xml nodeSheet : children)
        {
            final String sheetFilename = nodeSheet.getText();
            sheets.add(sheetFilename);
        }
        children.clear();

        return sheets;
    }

    /**
     * Export the defined sheets.
     * 
     * @param nodeSheets Sheets node (must not be <code>null</code>).
     * @param sheets Sheets defined (must not be <code>null</code>).
     */
    private static void exportSheets(Xml nodeSheets, List<String> sheets)
    {
        for (final String sheet : sheets)
        {
            final Xml nodeSheet = nodeSheets.createChild(NODE_TILE_SHEET);
            nodeSheet.setText(sheet);
        }
    }

    /** Tile width. */
    private final int tileWidth;
    /** Tile height. */
    private final int tileHeight;
    /** Sheets filename. */
    private final List<String> sheets;

    /**
     * Create config.
     * 
     * @param tileWidth The tile width (strictly positive).
     * @param tileHeight The tile height (strictly positive).
     * @param sheets The defined sheets (must not be <code>null</code>).
     * @throws LionEngineException If invalid size or sheets is <code>null</code>.
     */
    public TileSheetsConfig(int tileWidth, int tileHeight, List<String> sheets)
    {
        super();

        Check.superiorStrict(tileWidth, 0);
        Check.superiorStrict(tileHeight, 0);
        Check.notNull(sheets);

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.sheets = new ArrayList<>(sheets);
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
    public List<String> getSheets()
    {
        return sheets;
    }
}
