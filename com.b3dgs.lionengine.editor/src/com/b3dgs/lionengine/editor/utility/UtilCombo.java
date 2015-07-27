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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;

/**
 * Series of tool functions around combo.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilCombo
{
    /**
     * Create a combo from an enumeration. Selected item can be accessed with {@link Combo#getData()}.
     * Combo items are enum names as title case, converted by {@link UtilConversion#toTitleCase(String)}.
     * 
     * @param parent The parent reference.
     * @param values The enumeration values.
     * @return The combo instance.
     */
    public static Combo create(Composite parent, Enum<?>[] values)
    {
        final String[] items = new String[values.length];
        final Map<String, Enum<?>> links = new HashMap<>();
        for (int i = 0; i < values.length; i++)
        {
            items[i] = UtilConversion.toTitleCase(values[i].name());
            links.put(items[i], values[i]);
        }
        final Combo combo = new Combo(parent, SWT.READ_ONLY);
        combo.setItems(items);
        if (items.length > 0)
        {
            combo.setText(items[0]);
            combo.setData(links.get(items[0]));
        }
        combo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                combo.setData(links.get(combo.getText()));
            }
        });
        combo.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                combo.setData(links.get(combo.getText()));
            }
        });
        return combo;
    }

    /**
     * Create a combo from an enumeration. Selected item can be accessed with {@link Combo#getData()}.
     * Combo items are enum names as title case, converted by {@link UtilConversion#toTitleCase(String)}.
     * Legend label will be added on left.
     * 
     * @param legend The combo legend.
     * @param parent The parent reference.
     * @param values The enumeration values.
     * @return The combo instance.
     */
    public static Combo create(String legend, Composite parent, Enum<?>[] values)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label textLegend = new Label(composite, SWT.HORIZONTAL);
        textLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        textLegend.setText(legend);

        final Combo combo = create(composite, values);
        return combo;
    }

    /**
     * Register dirty listener on combo modification. Reference will be the current combo value.
     * 
     * @param combo The combo reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Combo combo, boolean enable)
    {
        final Object oldListener = combo.getData(UtilSwt.KEY_DIRTY);
        if (oldListener instanceof ModifyListener)
        {
            combo.removeModifyListener((ModifyListener) oldListener);
        }
        if (enable)
        {
            final ModifyListener listener = new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent event)
                {
                    UtilSwt.setDirty(combo.getShell(), true);
                }
            };
            combo.setData(UtilSwt.KEY_DIRTY, listener);
            combo.addModifyListener(listener);
        }
    }

    /**
     * Set the default combo value, and register dirty.
     * 
     * @param combo The combo reference.
     * @param value The text default value.
     */
    public static void setDefaultValue(Combo combo, String value)
    {
        registerDirty(combo, false);
        combo.setText(value);
        registerDirty(combo, true);
    }

    /**
     * Private constructor.
     */
    private UtilCombo()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
