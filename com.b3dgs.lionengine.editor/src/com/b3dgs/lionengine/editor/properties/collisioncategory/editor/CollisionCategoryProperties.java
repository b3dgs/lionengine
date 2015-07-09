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
package com.b3dgs.lionengine.editor.properties.collisioncategory.editor;

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
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.project.dialog.collision.CollisionList;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionGroup;

/**
 * Represents the collision category properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionCategoryProperties extends ObjectProperties<CollisionCategory>
                                         implements ObjectListListener<CollisionCategory>
{
    /** Groups. */
    private final CollisionList groups;
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
        groups = new CollisionList();
    }

    /**
     * Get the groups list.
     * 
     * @return The groups list.
     */
    public CollisionList getGroups()
    {
        return groups;
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite fields = new Composite(parent, SWT.NONE);
        fields.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fields.setLayout(new GridLayout(1, false));

        axis = UtilSwt.createCombo(Messages.CollisionCategoryEditor_Axis, fields, Axis.values());
        offsetX = UtilSwt.createText(Messages.CollisionCategoryEditor_OffsetX, fields);
        offsetX.addVerifyListener(UtilSwt.createVerify(offsetX, InputValidator.INTEGER_MATCH));
        offsetY = UtilSwt.createText(Messages.CollisionCategoryEditor_OffsetY, fields);
        offsetY.addVerifyListener(UtilSwt.createVerify(offsetY, InputValidator.INTEGER_MATCH));

        final Group groupsArea = new Group(parent, SWT.NONE);
        groupsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupsArea.setLayout(new GridLayout(1, false));
        groupsArea.setText(Messages.CollisionCategoryEditor_Groups);
        groups.createToolBar(groupsArea);
        groups.createTree(groupsArea);
    }

    @Override
    protected CollisionCategory createObject(String name)
    {
        return new CollisionCategory(name, (Axis) axis.getData(), Integer.parseInt(offsetX.getText()),
                Integer.parseInt(offsetY.getText()), groups.getObjects());
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
