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
package com.b3dgs.lionengine.editor.pathfinding.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectPropertiesAbstract;
import com.b3dgs.lionengine.editor.map.group.editor.GroupList;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathCategory;

/**
 * Represents the path category properties edition view.
 */
public class CategoryProperties extends ObjectPropertiesAbstract<PathCategory>
                                implements ObjectListListener<PathCategory>
{
    /** Groups tree. */
    private final GroupList groups = new GroupList();

    /**
     * Create category properties.
     */
    public CategoryProperties()
    {
        super();
    }

    @Override
    protected void createTextFields(Composite parent)
    {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setLayout(new GridLayout(1, false));
        group.setText(Messages.Groups);

        groups.createToolBar(group);
        groups.createTree(group);
    }

    @Override
    protected PathCategory createObject(String name)
    {
        return new PathCategory(name, groups.getObjectsName());
    }

    @Override
    public void notifyObjectSelected(PathCategory category)
    {
        for (final TreeItem item : groups.getTree().getItems())
        {
            item.setData(null);
            item.dispose();
        }
        for (final String group : category.getGroups())
        {
            final TreeItem item = new TreeItem(groups.getTree(), SWT.NONE);
            item.setText(group);
            item.setData(group);
        }
    }

    @Override
    public void notifyObjectDeleted(PathCategory category)
    {
        // Nothing to do
    }
}
