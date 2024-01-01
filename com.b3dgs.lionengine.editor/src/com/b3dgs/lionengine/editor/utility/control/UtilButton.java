/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.utility.Action;
import com.b3dgs.lionengine.editor.utility.UtilIcon;

/**
 * Series of tool functions around buttons.
 */
public final class UtilButton
{
    /** Browse icon. */
    private static final Image ICON_BROWSE = UtilIcon.get("dialog", "browse.png");

    /**
     * Create a button with a text and an icon.
     * 
     * @param parent The composite parent.
     * @param name The button name.
     * @param icon The button icon.
     * @return The button instance.
     */
    public static Button create(Composite parent, String name, Image icon)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        if (name != null)
        {
            button.setToolTipText(name);
            button.setText(name);
        }
        button.setImage(icon);
        return button;
    }

    /**
     * Create a browse button.
     * 
     * @param parent The composite parent.
     * @return The button instance.
     */
    public static Button createBrowse(Composite parent)
    {
        return create(parent, Messages.Browse, ICON_BROWSE);
    }

    /**
     * Create a check box button with its legend.
     * 
     * @param legend The check legend.
     * @param parent The parent reference.
     * @return The check instance.
     */
    public static Button createCheck(String legend, Composite parent)
    {
        return create(legend, parent, SWT.CHECK);
    }

    /**
     * Create a radio box button with its legend.
     * 
     * @param legend The check legend.
     * @param parent The parent reference.
     * @return The check instance.
     */
    public static Button createRadio(String legend, Composite parent)
    {
        return create(legend, parent, SWT.RADIO);
    }

    /**
     * Register dirty listener on button modification. Reference will be the current text value.
     * 
     * @param button button text reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Button button, boolean enable)
    {
        final Object oldListener = button.getData(UtilSwt.KEY_DIRTY);
        if (oldListener instanceof final SelectionListener listener)
        {
            button.removeSelectionListener(listener);
        }
        if (enable)
        {
            final SelectionListener listener = new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent event)
                {
                    UtilSwt.setDirty(button.getShell(), true);
                }
            };
            button.setData(UtilSwt.KEY_DIRTY, listener);
            button.addSelectionListener(listener);
        }
    }

    /**
     * Set the default button selection, and register dirty.
     * 
     * @param button The button reference.
     * @param selected The button default selection.
     */
    public static void setDefaultValue(Button button, boolean selected)
    {
        registerDirty(button, false);
        button.setSelection(selected);
        registerDirty(button, true);
    }

    /**
     * Set the button action.
     * 
     * @param button The button reference.
     * @param action The button action.
     */
    public static void setAction(Button button, Action action)
    {
        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                action.perform();
            }
        });
    }

    /**
     * Create a button with its legend.
     * 
     * @param legend The check legend.
     * @param parent The parent reference.
     * @param type The button type.
     * @return The check instance.
     */
    private static Button create(String legend, Composite parent, int type)
    {
        final Button button = new Button(parent, type);
        button.setText(legend);
        return button;
    }

    /**
     * Private constructor.
     */
    private UtilButton()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
