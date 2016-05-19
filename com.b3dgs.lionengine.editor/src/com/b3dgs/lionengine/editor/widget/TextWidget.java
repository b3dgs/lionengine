/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.utility.control.UtilText;

/**
 * Widget that provide a simple single line text area, with a validated input.
 */
public class TextWidget
{
    /** Listeners. */
    private final Collection<TextWidgetListener> listeners = new HashSet<>();
    /** Text value. */
    private final AtomicReference<Integer> value = new AtomicReference<>();
    /** Empty flag. */
    private final AtomicBoolean empty = new AtomicBoolean(true);

    /**
     * Create widget.
     * 
     * @param parent The parent composite.
     * @param name The text name.
     * @param validRegex The validator regex.
     */
    public TextWidget(Composite parent, String name, String validRegex)
    {
        final Text text = UtilText.create(name, parent);
        UtilText.addVerify(text, validRegex);
        text.addModifyListener(event ->
        {
            final String content = text.getText();
            empty.set(content.isEmpty());
            if (empty.get())
            {
                value.set(null);
            }
            else
            {
                value.set(Integer.valueOf(content));
            }
            for (final TextWidgetListener listener : listeners)
            {
                listener.notifyTextModified(content);
            }
        });
        text.addDisposeListener(event -> listeners.clear());
    }

    /**
     * Add a text listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(TextWidgetListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a text listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(TextWidgetListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Get the text value.
     * 
     * @return The text value.
     */
    public Optional<Integer> getValue()
    {
        return Optional.ofNullable(value.get());
    }

    /**
     * Check if text content is empty.
     * 
     * @return <code>true</code> if empty, <code>false</code> else.
     */
    public boolean isEmpty()
    {
        return empty.get();
    }

    /**
     * Listen to {@link TextWidget} events.
     */
    @FunctionalInterface
    public interface TextWidgetListener
    {
        /**
         * Called when text has been modified with correct value.
         * 
         * @param text The current text value.
         */
        void notifyTextModified(String text);
    }
}
