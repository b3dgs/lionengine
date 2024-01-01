/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;

/**
 * Element properties part.
 */
public class PropertiesTile implements PropertiesProviderTile
{
    /** Tile group icon. */
    private static final Image ICON_GROUP = UtilIcon.get(FOLDER, "tilegroup.png");
    /** Tile number icon. */
    private static final Image ICON_NUMBER = UtilIcon.get(FOLDER, "tilenumber.png");
    /** Tile size icon. */
    private static final Image ICON_SIZE = UtilIcon.get(FOLDER, "tilesize.png");

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
        final Collection<String> values = new ArrayList<>(mapGroup.getGroups());
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
     * Create properties.
     */
    public PropertiesTile()
    {
        super();
    }

    @Override
    public void setInput(Tree properties, Tile tile)
    {
        createAttributeTileGroup(properties, tile);
        createAttributeTileNumber(properties, tile);
        createAttributeTileSize(properties, tile);
    }
}
