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
package com.b3dgs.lionengine.editor.properties.collision.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectProperties;
import com.b3dgs.lionengine.game.collision.object.Collision;

/**
 * Represents the collisions properties edition view.
 */
public class EntityCollisionProperties extends ObjectProperties<Collision> implements ObjectListListener<Collision>
{
    /** Horizontal offset. */
    private Text offsetX;
    /** Vertical offset. */
    private Text offsetY;
    /** Collision width.. */
    private Text width;
    /** Collision height. */
    private Text height;
    /** Mirror flag. */
    private Button mirror;

    /**
     * Create an entity collision properties.
     */
    public EntityCollisionProperties()
    {
        super();
    }

    /**
     * Set the collision range.
     * 
     * @param first The first frame.
     * @param last The last frame.
     */
    public void setAnimationRange(int first, int last)
    {
        setValue(offsetX, String.valueOf(first));
        setValue(offsetY, String.valueOf(last));
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

        offsetX = createTextField(fields, Messages.EntityCollisionProperties_OffsetX);
        offsetY = createTextField(fields, Messages.EntityCollisionProperties_OffsetY);

        width = createTextField(fields, Messages.EntityCollisionProperties_Width);
        height = createTextField(fields, Messages.EntityCollisionProperties_Height);

        mirror = new Button(fields, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        mirror.setText(Messages.EntityCollisionProperties_Mirror);
    }

    @Override
    protected Collision createObject(String name)
    {
        return new Collision(name,
                             Integer.parseInt(offsetX.getText()),
                             Integer.parseInt(offsetY.getText()),
                             Integer.parseInt(width.getText()),
                             Integer.parseInt(height.getText()),
                             mirror.getSelection());
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(Collision collision)
    {
        setValueDefault(offsetX, String.valueOf(collision.getOffsetX()));
        setValueDefault(offsetY, String.valueOf(collision.getOffsetY()));
        setValueDefault(width, String.valueOf(collision.getWidth()));
        setValueDefault(height, String.valueOf(collision.getHeight()));
        setButtonSelection(mirror, collision.hasMirror());
    }

    @Override
    public void notifyObjectDeleted(Collision collision)
    {
        // Nothing to do
    }
}
