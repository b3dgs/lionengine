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
package com.b3dgs.lionengine.editor;

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

import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;

/**
 * Represents the object properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <T> The object type handled by the properties.
 */
public abstract class ObjectProperties<T>
{
    /**
     * Create a text and its label.
     * 
     * @param parent The parent reference.
     * @param title The text title.
     * @return The text instance.
     */
    protected static Text createTextField(Composite parent, String title)
    {
        final Composite field = new Composite(parent, SWT.NONE);
        field.setLayout(new GridLayout(2, false));

        final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        field.setLayoutData(data);

        final Label label = new Label(field, SWT.NONE);
        label.setText(title);

        final Text text = new Text(field, SWT.SINGLE);
        final GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        text.setLayoutData(textData);

        return text;
    }

    /**
     * Set the text value by checking if it is not disposed.
     * 
     * @param text The text reference.
     * @param value The text value.
     */
    protected static void setTextValue(Text text, String value)
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
    protected static void setButtonSelection(Button button, boolean selected)
    {
        if (!button.isDisposed())
        {
            button.setSelection(selected);
        }
    }

    /** Object list. */
    ObjectList<T> objectList;

    /**
     * Constructor.
     */
    public ObjectProperties()
    {
        // Nothing to do
    }

    /**
     * Create the text fields.
     * 
     * @param parent The composite parent.
     */
    protected abstract void createTextFields(Composite parent);

    /**
     * Called when confirm button pressed.
     * 
     * @return The created object from the current properties values.
     */
    protected abstract T createObject();

    /**
     * Create the animation data area.
     * 
     * @param parent The composite parent.
     */
    public void create(Composite parent)
    {
        final Group objectProperties = new Group(parent, SWT.NONE);
        objectProperties.setLayout(new GridLayout(1, false));
        objectProperties.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
        objectProperties.setText(Messages.ObjectProperties_Properties);

        final Composite objectData = new Composite(objectProperties, SWT.NONE);
        objectData.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
        objectData.setLayout(new GridLayout(1, false));

        createTextFields(objectData);

        final Composite objectButtons = new Composite(objectProperties, SWT.NONE);
        objectButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        objectButtons.setLayout(new GridLayout(2, true));

        createResetButton(objectButtons);
        createConfirmButton(objectButtons);
    }

    /**
     * Set the object list.
     * 
     * @param objectList The objectList list reference.
     */
    public void setObjectList(ObjectList<T> objectList)
    {
        this.objectList = objectList;
    }

    /**
     * Create the confirm button.
     * 
     * @param parent The composite parent.
     */
    private void createConfirmButton(Composite parent)
    {
        final Button confirm = Tools.createButton(parent, Messages.ObjectProperties_Confirm, null);
        confirm.setImage(AbstractDialog.ICON_OK);
        confirm.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (objectList.getSelectedObject() != null)
                {
                    final TreeItem[] items = objectList.getTree().getSelection();
                    if (items.length > 0)
                    {
                        final TreeItem selection = items[0];
                        final T object = createObject();
                        objectList.update(selection, object);
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
        final Button reset = Tools.createButton(parent, Messages.ObjectProperties_Reset, null);
        reset.setImage(AbstractDialog.ICON_CANCEL);
        reset.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (objectList.getSelectedObject() != null)
                {
                    objectList.restoreSelectedObject();
                }
            }
        });
    }
}
