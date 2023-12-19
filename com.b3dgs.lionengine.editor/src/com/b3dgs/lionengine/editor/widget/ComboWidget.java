/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.editor.utility.control.UtilCombo;

/**
 * Widget that provide a simple combo.
 * 
 * @param <T> Type handled.
 */
public class ComboWidget<T>
{
    /** Listeners. */
    private final Collection<ComboWidgetListener<T>> listeners = new HashSet<>();
    /** Selected value. */
    private final AtomicReference<T> value = new AtomicReference<>();
    /** Combo reference. */
    private final Combo combo;

    /**
     * Create widget.
     * 
     * @param parent The parent composite.
     * @param name The combo name.
     * @param editable <code>true</code> if manually editable, <code>false</code> else.
     * @param values List of element in combo.
     */
    @SafeVarargs
    public ComboWidget(Composite parent, String name, boolean editable, T... values)
    {
        combo = UtilCombo.create(name, parent, editable, values);
        combo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                @SuppressWarnings("unchecked")
                final T content = (T) combo.getData();
                value.set(content);
                for (final ComboWidgetListener<T> listener : listeners)
                {
                    listener.notifyComboChanged(content);
                }
            }
        });
        combo.addDisposeListener(event -> listeners.clear());
    }

    /**
     * Add a text listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(ComboWidgetListener<T> listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a text listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(ComboWidgetListener<T> listener)
    {
        listeners.remove(listener);
    }

    /**
     * Set current value.
     * 
     * @param type The type value.
     */
    public void setValue(T type)
    {
        combo.setText(type != null ? type.toString() : Constant.EMPTY_STRING);
        value.set(type);
    }

    /**
     * Get the text value.
     * 
     * @return The text value.
     */
    public T getValue()
    {
        return value.get();
    }

    /**
     * Listen to {@link ComboWidget} events.
     * 
     * @param <T> Type handled.
     */
    @FunctionalInterface
    public interface ComboWidgetListener<T>
    {
        /**
         * Called when combo has been modified with correct value.
         * 
         * @param text The current text value.
         */
        void notifyComboChanged(T text);
    }
}
