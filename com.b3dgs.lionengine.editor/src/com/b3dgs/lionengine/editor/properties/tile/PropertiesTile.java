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
package com.b3dgs.lionengine.editor.properties.tile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderTile;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.map.TileFeature;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesTile
        implements PropertiesProviderTile
{
    /** Class icon. */
    private static final Image ICON_CLASS = UtilEclipse.getIcon("properties", "class.png");

    /**
     * Create the attribute group.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileGroup(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileGroup, tile.getGroup());
        item.setData(ConfigTileGroup.GROUP);
        item.setImage(PropertiesTile.ICON_CLASS);
    }

    /**
     * Create the attribute sheet number.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileSheet(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileSheet, String.valueOf(tile.getSheet()));
        item.setData(ConfigTileGroup.SHEET);
        item.setImage(PropertiesTile.ICON_CLASS);
    }

    /**
     * Create the attribute tile number.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileNumber(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileNumber, String.valueOf(tile.getNumber()));
        item.setData(ConfigTileGroup.START);
        item.setImage(PropertiesTile.ICON_CLASS);
    }

    /**
     * Create the attribute tile size.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileSize(Tree properties, Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(item, Messages.Properties_TileSize, tile.getWidth() + " * " + tile.getHeight());
        item.setData(ConfigTileGroup.START);
        item.setImage(PropertiesTile.ICON_CLASS);
    }

    /**
     * Create the attribute tile features.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileFeatures(Tree properties, Tile tile)
    {
        final TreeItem features = new TreeItem(properties, SWT.NONE);
        features.setText(Messages.Properties_TileFeatures);
        features.setImage(PropertiesTile.ICON_CLASS);

        for (final TileFeature feature : tile.getFeatures())
        {
            final TreeItem item = new TreeItem(features, SWT.NONE);
            item.setText(Messages.Properties_TileFeatures);
            item.setImage(PropertiesTile.ICON_CLASS);

            final Class<?> clazz = feature.getClass();
            for (final Class<?> type : clazz.getInterfaces())
            {
                if (TileFeature.class.isAssignableFrom(type))
                {
                    PropertiesPart.createLine(item, type.getSimpleName(), clazz.getName());
                }
            }
        }
    }

    /*
     * PropertiesProviderTile
     */

    @Override
    public void setInput(Tree properties, Tile tile)
    {
        createAttributeTileGroup(properties, tile);
        createAttributeTileSheet(properties, tile);
        createAttributeTileNumber(properties, tile);
        createAttributeTileSize(properties, tile);
        createAttributeTileFeatures(properties, tile);
    }
}
