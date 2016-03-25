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
package com.b3dgs.lionengine.editor.map.group.project;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;

/**
 * Represents the groups edition dialog.
 */
public class GroupsEditDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Groups list. */
    private final GroupList list = new GroupList();
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
        super(parent,
              Messages.EditGroupsDialog_Title,
              Messages.EditGroupsDialog_HeaderTitle,
              Messages.EditGroupsDialog_HeaderDesc,
              ICON);
        this.configGroups = configGroups;
        dialog.setMinimumSize(128, 320);
        createDialog();
        finish.setEnabled(true);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        list.create(content);
        list.loadGroups(configGroups);
        list.addListener(list);
    }

    @Override
    protected void onFinish()
    {
        final Collection<TileGroup> groups = new ArrayList<>();
        for (final TreeItem item : list.getTree().getItems())
        {
            final TileGroup group = (TileGroup) item.getData();
            groups.add(group);
        }

        TileGroupsConfig.exports(configGroups, groups);

        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        mapGroup.loadGroups(configGroups);
    }
}
