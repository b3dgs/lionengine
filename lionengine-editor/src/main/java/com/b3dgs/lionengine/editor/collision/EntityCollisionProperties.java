/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;
import com.b3dgs.lionengine.game.Collision;

/**
 * Represents the collisions properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityCollisionProperties
{
    /**
     * Create a text and its label.
     * 
     * @param parent The parent reference.
     * @param title The text title.
     * @return The text instance.
     */
    private static Text createTextField(Composite parent, String title)
    {
        final Composite field = new Composite(parent, SWT.NONE);
        field.setLayout(new GridLayout(2, false));
        final GridData fieldData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        field.setLayoutData(fieldData);

        final Label label = new Label(field, SWT.NONE);
        label.setText(title);

        final Text text = new Text(field, SWT.SINGLE);
        final GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        data.minimumWidth = 32;
        text.setLayoutData(data);

        return text;
    }

    /**
     * Set the text value by checking if it is not disposed.
     * 
     * @param text The text reference.
     * @param value The text value.
     */
    private static void setTextValue(Text text, String value)
    {
        if (!text.isDisposed())
        {
            text.setText(value);
        }
    }

    /**
     * Set the button selection by checking if it is not disposed.
     * 
     * @param button The button reference.
     * @param selected The selected state.
     */
    private static void setButtonSelection(Button button, boolean selected)
    {
        if (!button.isDisposed())
        {
            button.setSelection(selected);
        }
    }

    /** Collisions list. */
    EntityCollisionList entityCollisionList;
    /** Horizontal offset. */
    Text offsetX;
    /** Vertical offset. */
    Text offsetY;
    /** Collision width.. */
    Text width;
    /** Collision height. */
    Text height;
    /** Mirror flag. */
    Button mirror;

    /**
     * Constructor.
     */
    public EntityCollisionProperties()
    {
        // Nothing to do
    }

    /**
     * Set the collisions list.
     * 
     * @param entityCollisionList The collisions list reference.
     */
    public void setEntityCollisionList(EntityCollisionList entityCollisionList)
    {
        this.entityCollisionList = entityCollisionList;
    }

    /**
     * Set the selected collision, and update the properties fields.
     * 
     * @param collision The selected collision.
     */
    public void setSelectedCollision(Collision collision)
    {
        EntityCollisionProperties.setTextValue(offsetX, String.valueOf(collision.getOffsetX()));
        EntityCollisionProperties.setTextValue(offsetY, String.valueOf(collision.getOffsetY()));
        EntityCollisionProperties.setTextValue(width, String.valueOf(collision.getWidth()));
        EntityCollisionProperties.setTextValue(height, String.valueOf(collision.getHeight()));
        EntityCollisionProperties.setButtonSelection(mirror, collision.hasMirror());
    }

    /**
     * Set the collision range.
     * 
     * @param first The first frame.
     * @param last The last frame.
     */
    public void setAnimationRange(int first, int last)
    {
        EntityCollisionProperties.setTextValue(offsetX, String.valueOf(first));
        EntityCollisionProperties.setTextValue(offsetY, String.valueOf(last));
    }

    /**
     * Create the collision data area.
     * 
     * @param parent The composite parent.
     */
    public void createAnimationProperties(Composite parent)
    {
        final Group collisionProperties = new Group(parent, SWT.NONE);
        collisionProperties.setLayout(new GridLayout(1, false));
        collisionProperties.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        collisionProperties.setText("Properties");

        final Composite collisionData = new Composite(collisionProperties, SWT.NONE);
        collisionData.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, true));
        collisionData.setLayout(new GridLayout(1, false));

        createTextFields(collisionData);

        final Composite collisionButtons = new Composite(collisionProperties, SWT.NONE);
        collisionButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        collisionButtons.setLayout(new GridLayout(2, true));

        createResetButton(collisionButtons);
        createConfirmButton(collisionButtons);
    }

    /**
     * Create the text fields.
     * 
     * @param parent The composite parent.
     */
    private void createTextFields(Composite parent)
    {
        final Composite fields = new Composite(parent, SWT.NONE);
        fields.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        fields.setLayout(new GridLayout(1, false));

        offsetX = EntityCollisionProperties.createTextField(fields, "Offset X:");
        offsetY = EntityCollisionProperties.createTextField(fields, "Offset Y:");

        width = EntityCollisionProperties.createTextField(fields, "Width:");
        height = EntityCollisionProperties.createTextField(fields, "Height:");

        mirror = new Button(fields, SWT.CHECK | SWT.RIGHT_TO_LEFT);
        mirror.setText("Mirror");
    }

    /**
     * Create the confirm button.
     * 
     * @param parent The composite parent.
     */
    private void createConfirmButton(Composite parent)
    {
        final Button confirm = Tools.createButton(parent, "Confirm", null);
        confirm.setImage(AbstractDialog.ICON_OK);
        confirm.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (entityCollisionList.getSelectedCollision() != null)
                {
                    final TreeItem[] items = entityCollisionList.getTree().getSelection();
                    if (items.length > 0)
                    {
                        final TreeItem selection = items[0];
                        final Collision collision = new Collision(Integer.parseInt(offsetX.getText()), Integer
                                .parseInt(offsetY.getText()), Integer.parseInt(width.getText()), Integer
                                .parseInt(height.getText()), mirror.getSelection());
                        entityCollisionList.updateCollision(selection, collision);
                    }
                }
            }
        });
    }

    /**
     * Create the reset button.
     * 
     * @param parent The composite parent.
     */
    private void createResetButton(Composite parent)
    {
        final Button reset = Tools.createButton(parent, "Reset", null);
        reset.setImage(AbstractDialog.ICON_CANCEL);
        reset.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (entityCollisionList.getSelectedCollision() != null)
                {
                    entityCollisionList.restoreSelectedCollision();
                }
            }
        });
    }
}
