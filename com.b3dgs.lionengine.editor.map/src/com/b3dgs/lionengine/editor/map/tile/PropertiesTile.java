/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.tile;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderTile;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.handler.Feature;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;

/**
 * Element properties part.
 */
public class PropertiesTile implements PropertiesProviderTile
{
    /** Tile group icon. */
    private static final Image ICON_GROUP = UtilIcon.get(FOLDER, "tilegroup.png");
    /** Tile sheet icon. */
    private static final Image ICON_SHEET = UtilIcon.get(FOLDER, "tilesheet.png");
    /** Tile number icon. */
    private static final Image ICON_NUMBER = UtilIcon.get(FOLDER, "tilenumber.png");
    /** Tile size icon. */
    private static final Image ICON_SIZE = UtilIcon.get(FOLDER, "tilesize.png");
    /** Tile features icon. */
    private static final Image ICON_FEATURES = UtilIcon.get(FOLDER, "tilefeatures.png");
    /** Tile feature icon. */
    private static final Image ICON_FEATURE = UtilIcon.get(FOLDER, "tilefeature.png");

    /**
     * Called on double click.
     * 
     * @param properties The tree properties.
     * @param selection The selected item.
     * @param tile The selected tile.
     */
    private static void onDoubleClick(Tree properties, TreeItem selection, Tile tile)
    {
        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        final Collection<String> values = new ArrayList<>();
        for (final String group : mapGroup.getGroups())
        {
            values.add(group);
        }
        if (!values.contains(TileGroupsConfig.REMOVE_GROUP_NAME))
        {
            values.add(TileGroupsConfig.REMOVE_GROUP_NAME);
        }
        final GroupChooser chooser = new GroupChooser(properties.getShell(), values);
        chooser.open();
        final String oldGroup = mapGroup.getGroup(tile);
        final String newGroup = chooser.getChoice();
        if (newGroup != null)
        {
            UtilWorld.changeTileGroup(mapGroup, oldGroup, newGroup, tile);
            selection.setText(PropertiesPart.COLUMN_VALUE, newGroup);

            final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
            part.update();
        }
    }

    /**
     * Create the attribute group.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileGroup(final Tree properties, final Tile tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        PropertiesPart.createLine(item, Messages.TileGroup, mapGroup.getGroup(tile));
        item.setData(TileGroupsConfig.NODE_GROUP);
        item.setImage(PropertiesTile.ICON_GROUP);

        properties.addListener(SWT.MouseDoubleClick, event ->
        {
            final org.eclipse.swt.graphics.Point point = new org.eclipse.swt.graphics.Point(event.x, event.y);
            final TreeItem selection = properties.getItem(point);
            if (item.equals(selection))
            {
                onDoubleClick(properties, selection, tile);
            }
        });
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
        PropertiesPart.createLine(item, Messages.TileSheet, String.valueOf(tile.getSheet()));
        item.setData(TileConfig.ATT_TILE_SHEET);
        item.setImage(PropertiesTile.ICON_SHEET);
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
        PropertiesPart.createLine(item, Messages.TileNumber, String.valueOf(tile.getNumber()));
        item.setData(TileConfig.ATT_TILE_NUMBER);
        item.setImage(PropertiesTile.ICON_NUMBER);
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
        PropertiesPart.createLine(item, Messages.TileSize, tile.getWidth() + " * " + tile.getHeight());
        item.setData(TileConfig.NODE_TILE);
        item.setImage(PropertiesTile.ICON_SIZE);
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
        features.setText(Messages.TileFeatures);
        features.setImage(PropertiesTile.ICON_FEATURES);

        for (final Feature feature : tile.getFeatures())
        {
            final TreeItem item = new TreeItem(features, SWT.NONE);
            item.setText(Messages.TileFeature);
            item.setImage(PropertiesTile.ICON_FEATURE);

            final Class<?> clazz = feature.getClass();
            for (final Class<?> type : clazz.getInterfaces())
            {
                if (Feature.class.isAssignableFrom(type))
                {
                    PropertiesPart.createLine(item, type.getSimpleName(), clazz.getName());
                }
            }
        }
    }

    /**
     * Create properties.
     */
    public PropertiesTile()
    {
        super();
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
