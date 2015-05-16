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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.ObjectProperties;
import com.b3dgs.lionengine.game.collision.TileGroup;

/**
 * Represents the group properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GroupProperties
        extends ObjectProperties<TileGroup>
{
    /** Sheet index. */
    private Text sheetText;
    /** Start number. */
    private Text startText;
    /** End number. */
    private Text endText;

    /**
     * Create the properties.
     */
    public GroupProperties()
    {
        // Nothing to do
    }

    /**
     * Set the selected group, and update the properties fields.
     * 
     * @param group The selected group.
     */
    public void setSelectedGroup(TileGroup group)
    {
        setTextValue(sheetText, String.valueOf(group.getSheet()));
        setTextValue(startText, String.valueOf(group.getStart()));
        setTextValue(endText, String.valueOf(group.getEnd()));
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite properties = new Composite(parent, SWT.NONE);
        properties.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        properties.setLayout(new GridLayout(1, false));

        sheetText = createTextField(properties, Messages.GroupProperties_Sheet);
        startText = createTextField(properties, Messages.GroupProperties_Start);
        endText = createTextField(properties, Messages.GroupProperties_End);
    }

    @Override
    protected TileGroup createObject(String name)
    {
        final int sheet = Integer.parseInt(sheetText.getText());
        final int start = Integer.parseInt(startText.getText());
        final int end = Integer.parseInt(endText.getText());
        final TileGroup group = new TileGroup(name, sheet, start, end);
        return group;
    }
}
