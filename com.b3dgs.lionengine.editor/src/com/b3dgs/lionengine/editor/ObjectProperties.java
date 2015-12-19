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
package com.b3dgs.lionengine.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Nameable;
import com.b3dgs.lionengine.editor.utility.UtilCombo;
import com.b3dgs.lionengine.editor.utility.UtilText;

/**
 * Represents the object properties edition view.
 * 
 * @param <T> The object type handled by the properties.
 */
public abstract class ObjectProperties<T extends Nameable>
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
    protected static void setValue(Text text, String value)
    {
        if (!text.isDisposed())
        {
            text.setText(value);
        }
    }

    /**
     * Set the combo value by checking if it is not disposed.
     * 
     * @param combo The combo reference.
     * @param value The combo value.
     */
    protected static void setValue(Combo combo, String value)
    {
        if (!combo.isDisposed())
        {
            combo.setText(value);
        }
    }

    /**
     * Set the text default value by checking if it is not disposed.
     * 
     * @param text The text reference.
     * @param value The text value.
     */
    protected static void setValueDefault(Text text, String value)
    {
        if (!text.isDisposed())
        {
            UtilText.setDefaultValue(text, value);
        }
    }

    /**
     * Set the combo default value by checking if it is not disposed.
     * 
     * @param combo The combo reference.
     * @param value The combo value.
     */
    protected static void setValueDefault(Combo combo, String value)
    {
        if (!combo.isDisposed())
        {
            UtilCombo.setDefaultValue(combo, value);
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

    /**
     * Create an object properties.
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
     * @param name The item name.
     * @return The created object from the current properties values.
     */
    protected abstract T createObject(String name);

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
    }
}
