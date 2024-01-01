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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
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
import com.b3dgs.lionengine.editor.utility.Action;

/**
 * Series of tool functions around combo.
 */
public final class UtilCombo
{
    /**
     * Create a combo from a list of elements. Selected item can be accessed with {@link Combo#getData()}.
     * Combo items are elements {@link Object#toString()}. Legend label will be added on left.
     * 
     * @param <T> The combo object type value.
     * @param legend The combo legend.
     * @param parent The parent reference.
     * @param editable <code>true</code> if combo can be manually edited, <code>false</code> else.
     * @param values The objects values.
     * @return The combo instance.
     */
    @SafeVarargs
    public static <T> Combo create(String legend, Composite parent, boolean editable, T... values)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label textLegend = new Label(composite, SWT.HORIZONTAL);
        textLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        textLegend.setText(legend);

        return create(composite, legend, editable, values);
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
        return create(legend, parent, false, values);
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
        if (oldListener instanceof final ModifyListener listener)
        {
            combo.removeModifyListener(listener);
        }
        if (enable)
        {
            final ModifyListener listener = event -> UtilSwt.setDirty(combo.getShell(), true);
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
     * Set the combo action.
     * 
     * @param combo The combo reference.
     * @param action The combo action.
     */
    public static void setAction(Combo combo, Action action)
    {
        combo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                action.perform();
            }
        });
    }

    /**
     * Create a combo from a list of elements. Selected item can be accessed with {@link Combo#getData()}.
     * Combo items are elements {@link Object#toString()}.
     * 
     * @param <T> The combo object type value.
     * @param parent The parent reference.
     * @param legend The combo legend.
     * @param editable <code>true</code> if combo can be manually edited, <code>false</code> else.
     * @param values The objects values.
     * @return The combo instance.
     */
    @SafeVarargs
    private static <T> Combo create(Composite parent, String legend, boolean editable, T... values)
    {
        final String[] items = new String[values.length];
        final Map<String, T> links = new HashMap<>(values.length);
        for (int i = 0; i < values.length; i++)
        {
            items[i] = values[i].toString();
            links.put(items[i], values[i]);
        }
        final int flag;
        if (editable)
        {
            flag = SWT.DROP_DOWN;
        }
        else
        {
            flag = SWT.READ_ONLY;
        }
        final Combo combo = new Combo(parent, flag);
        combo.setToolTipText(legend);
        combo.setItems(items);
        if (items.length > 0)
        {
            combo.setText(items[0]);
            combo.setData(links.get(items[0]));
        }
        UtilCombo.setAction(combo, () -> combo.setData(links.get(combo.getText())));
        combo.addModifyListener(event -> combo.setData(links.get(combo.getText())));
        return combo;
    }

    /**
     * Private constructor.
     */
    private UtilCombo()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
