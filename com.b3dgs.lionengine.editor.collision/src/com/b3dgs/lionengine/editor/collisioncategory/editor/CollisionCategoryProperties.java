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
package com.b3dgs.lionengine.editor.collisioncategory.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectProperties;
import com.b3dgs.lionengine.editor.collision.map.CollisionGroupList;
import com.b3dgs.lionengine.editor.utility.UtilCombo;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.tile.CollisionCategory;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;

/**
 * Represents the collision category properties edition view.
 */
public class CollisionCategoryProperties extends ObjectProperties<CollisionCategory>
                                         implements ObjectListListener<CollisionCategory>
{
    /** Groups. */
    private final CollisionGroupList groups = new CollisionGroupList();
    /** Axis. */
    private Combo axis;
    /** Offset X. */
    private Text offsetX;
    /** Offset Y. */
    private Text offsetY;

    /**
     * Create a collision category properties.
     */
    public CollisionCategoryProperties()
    {
        super();
    }

    /**
     * Get the groups list.
     * 
     * @return The groups list.
     */
    public CollisionGroupList getGroups()
    {
        return groups;
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite fieldsArea = new Composite(parent, SWT.NONE);
        fieldsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fieldsArea.setLayout(new GridLayout(1, false));

        axis = UtilCombo.create(Messages.Axis, fieldsArea, Axis.values());
        offsetX = UtilText.create(Messages.OffsetX, fieldsArea);
        offsetX.addVerifyListener(UtilText.createVerify(offsetX, InputValidator.INTEGER_MATCH));
        offsetY = UtilText.create(Messages.OffsetY, fieldsArea);
        offsetY.addVerifyListener(UtilText.createVerify(offsetY, InputValidator.INTEGER_MATCH));

        final Group groupsArea = new Group(parent, SWT.NONE);
        groupsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupsArea.setLayout(new GridLayout(1, false));
        groupsArea.setText(Messages.Groups);

        groups.createToolBar(groupsArea);
        groups.createTree(groupsArea);
    }

    @Override
    protected CollisionCategory createObject(String name)
    {
        return new CollisionCategory(name,
                                     (Axis) axis.getData(),
                                     Integer.parseInt(offsetX.getText()),
                                     Integer.parseInt(offsetY.getText()),
                                     groups.getObjects());
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(CollisionCategory category)
    {
        axis.setText(category.getAxis().name());
        offsetX.setText(String.valueOf(category.getOffsetX()));
        offsetY.setText(String.valueOf(category.getOffsetY()));

        for (final TreeItem item : groups.getTree().getItems())
        {
            item.setData(null);
            item.dispose();
        }
        for (final CollisionGroup group : category.getGroups())
        {
            final TreeItem item = new TreeItem(groups.getTree(), SWT.NONE);
            item.setText(group.getName());
            item.setData(group);
        }
    }

    @Override
    public void notifyObjectDeleted(CollisionCategory category)
    {
        // Nothing to do
    }
}
