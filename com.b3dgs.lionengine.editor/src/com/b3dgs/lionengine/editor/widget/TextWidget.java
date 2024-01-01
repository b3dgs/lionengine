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
package com.b3dgs.lionengine.editor.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
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
    private final AtomicReference<Double> value = new AtomicReference<>();
    /** Text reference. */
    private final Text text;

    /**
     * Create widget.
     * 
     * @param parent The parent composite.
     * @param name The text name.
     * @param validRegex The validator regex.
     */
    public TextWidget(Composite parent, String name, String validRegex)
    {
        this(parent, name, validRegex, false);
    }

    /**
     * Create widget.
     * 
     * @param parent The parent composite.
     * @param name The text name.
     * @param validRegex The validator regex (<code>null</code> if none).
     * @param sameWidth <code>true</code> to make column same size, <code>false</code> else.
     */
    public TextWidget(Composite parent, String name, String validRegex, boolean sameWidth)
    {
        this(parent, name, validRegex, false, sameWidth);
    }

    /**
     * Create widget.
     * 
     * @param parent The parent composite.
     * @param name The text name.
     * @param validRegex The validator regex (<code>null</code> if none).
     * @param string <code>true</code> To use string, <code>false</code> else.
     * @param sameWidth <code>true</code> to make column same size, <code>false</code> else.
     */
    public TextWidget(Composite parent, String name, String validRegex, boolean string, boolean sameWidth)
    {
        text = UtilText.create(name, parent, sameWidth);
        if (validRegex != null)
        {
            UtilText.addVerify(text, validRegex);
        }
        text.addModifyListener(event ->
        {
            final String content = text.getText();
            if (content.isEmpty())
            {
                value.set(null);
            }
            else if (!string)
            {
                value.set(Double.valueOf(content));
            }
            for (final TextWidgetListener listener : listeners)
            {
                listener.notifyTextModified(content);
            }
        });
        text.addDisposeListener(event -> listeners.clear());
    }

    /**
     * Set the value.
     * 
     * @param value The value.
     */
    public void set(String value)
    {
        text.setText(value);
    }

    /**
     * Set the value.
     * 
     * @param value The value.
     */
    public void set(int value)
    {
        text.setText(Integer.toString(value));
    }

    /**
     * Set the value.
     * 
     * @param value The value.
     */
    public void set(double value)
    {
        text.setText(Double.toString(value));
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
    public Optional<String> getValueText()
    {
        return text.getText().isEmpty() ? Optional.empty() : Optional.of(text.getText());
    }

    /**
     * Get the text value.
     * 
     * @return The text value.
     */
    public OptionalInt getValue()
    {
        return Optional.ofNullable(value.get()).map(v -> OptionalInt.of(v.intValue())).orElse(OptionalInt.empty());
    }

    /**
     * Get the text value.
     * 
     * @return The text value.
     */
    public OptionalDouble getValueDouble()
    {
        return Optional.ofNullable(value.get()).map(OptionalDouble::of).orElse(OptionalDouble.empty());
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
