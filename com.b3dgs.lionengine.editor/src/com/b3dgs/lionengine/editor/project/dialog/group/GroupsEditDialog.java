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
package com.b3dgs.lionengine.editor.project.dialog.group;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGroup;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the groups edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GroupsEditDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Groups media. */
    final Media groups;
    /** Groups list. */
    private final GroupList list = new GroupList();

    /**
     * Create a groups edit dialog.
     * 
     * @param parent The parent shell.
     * @param groups The groups media.
     */
    public GroupsEditDialog(Shell parent, Media groups)
    {
        super(parent,
              Messages.EditGroupsDialog_Title,
              Messages.EditGroupsDialog_HeaderTitle,
              Messages.EditGroupsDialog_HeaderDesc,
              ICON);
        this.groups = groups;
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
        list.loadGroups(groups);
        list.addListener(list);
    }

    @Override
    protected void onFinish()
    {
        final XmlNode root = Stream.createXmlNode(ConfigTileGroup.GROUPS);
        root.writeString(Configurer.HEADER, Engine.WEBSITE);

        for (final TreeItem item : list.getTree().getItems())
        {
            final TileGroup group = (TileGroup) item.getData();
            ConfigTileGroup.export(root, group);
        }
        Stream.saveXml(root, groups);

        final MapTile map = WorldModel.INSTANCE.getMap();
        map.loadGroups(groups);
    }
}
