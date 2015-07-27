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
package com.b3dgs.lionengine.editor.utility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Series of tool functions around buttons.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilButton
{
    /**
     * Create a button with a text and an icon at a fixed width.
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
            button.setText(name);
        }
        button.setImage(icon);
        return button;
    }

    /**
     * Create a check box button with its legend.
     * 
     * @param legend The check legend.
     * @param parent The parent reference.
     * @return The check instance.
     */
    public static Button create(String legend, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label textLegend = new Label(composite, SWT.HORIZONTAL);
        textLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        textLegend.setText(legend);

        final Button check = new Button(composite, SWT.CHECK);
        return check;
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
        if (oldListener instanceof SelectionListener)
        {
            button.removeSelectionListener((SelectionListener) oldListener);
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
     * Private constructor.
     */
    private UtilButton()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
