/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.group.editor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.EditorAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;

/**
 * Represents the groups edition dialog.
 */
public class GroupsEditDialog extends EditorAbstract
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Group properties. */
    private final GroupProperties properties = new GroupProperties();
    /** Groups list. */
    private final GroupList list = new GroupList(properties);
    /** Groups media. */
    private final Media configGroups;

    /**
     * Create a groups edit dialog.
     * 
     * @param parent The parent shell.
     * @param configGroups The groups configuration.
     */
    public GroupsEditDialog(Shell parent, Media configGroups)
    {
        super(parent, Messages.Title, ICON);
        this.configGroups = configGroups;
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        list.addListener(list);
        list.addListener(properties);

        list.create(content);
        properties.create(content);

        list.loadGroups(configGroups);
    }

    @Override
    protected void onExit()
    {
        list.save();

        final TreeItem[] items = list.getTree().getItems();
        final Collection<TileGroup> groups = new ArrayList<>(items.length);
        for (final TreeItem item : items)
        {
            final TileGroup group = (TileGroup) item.getData();
            groups.add(group);
        }

        TileGroupsConfig.exports(configGroups, groups);

        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        mapGroup.loadGroups(configGroups);
    }
}
