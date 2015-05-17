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

import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the groups edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GroupsEditDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "groups-edit.png");

    /** Groups media. */
    final Media groups;
    /** Groups list. */
    private final GroupList list = new GroupList();
    /** Group properties. */
    private final GroupProperties properties = new GroupProperties(list);

    /**
     * Create a groups edit dialog.
     * 
     * @param parent The parent shell.
     * @param tilesheets The groups media.
     */
    public GroupsEditDialog(Shell parent, Media tilesheets)
    {
        super(parent, Messages.EditGroupsDialog_Title, Messages.EditGroupsDialog_HeaderTitle,
                Messages.EditGroupsDialog_HeaderDesc, ICON);
        groups = tilesheets;
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
        list.addListener(properties);
        properties.create(content);

        list.loadGroups(groups);
    }

    @Override
    protected void onFinish()
    {
        final XmlNode root = Stream.createXmlNode(ConfigTileGroup.GROUPS);
        root.writeString(Configurer.HEADER, EngineCore.WEBSITE);

        for (final TreeItem item : list.getTree().getItems())
        {
            final TileGroup group = (TileGroup) item.getData();
            final XmlNode nodeGroup = ConfigTileGroup.export(group);
            root.add(nodeGroup);
        }
        Stream.saveXml(root, groups);
    }
}
