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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Series of tool functions around the texts.
 */
public final class UtilText
{
    /**
     * Create a text with its legend.
     * 
     * @param legend The legend text.
     * @param parent The composite parent.
     * @return The created text.
     */
    public static Text create(String legend, Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label textLegend = new Label(composite, SWT.HORIZONTAL);
        textLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        textLegend.setText(legend);

        final Text text = new Text(composite, SWT.SINGLE);
        final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.minimumWidth = 64;
        text.setLayoutData(data);

        return text;
    }

    /**
     * Create a verify listener.
     * 
     * @param text The text to verify.
     * @param match The expected match.
     * @return The verify listener.
     * @see com.b3dgs.lionengine.editor.InputValidator
     */
    public static VerifyListener createVerify(final Text text, final String match)
    {
        return event ->
        {
            final String init = text.getText();
            final String newText = init.substring(0, event.start) + event.text + init.substring(event.end);
            event.doit = newText.matches(match) || newText.isEmpty();
        };
    }

    /**
     * Register dirty listener on text modification. Reference will be the current text value.
     * 
     * @param text The text reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Text text, boolean enable)
    {
        final Object oldListener = text.getData(UtilSwt.KEY_DIRTY);
        if (oldListener instanceof ModifyListener)
        {
            text.removeModifyListener((ModifyListener) oldListener);
        }
        if (enable)
        {
            final ModifyListener listener = event -> UtilSwt.setDirty(text.getShell(), true);
            text.setData(UtilSwt.KEY_DIRTY, listener);
            text.addModifyListener(listener);
        }
    }

    /**
     * Set the default text value, and register dirty.
     * 
     * @param text The text reference.
     * @param value The text default value.
     */
    public static void setDefaultValue(Text text, String value)
    {
        registerDirty(text, false);
        if (value == null)
        {
            text.setText(Constant.EMPTY_STRING);
        }
        else
        {
            text.setText(value);
        }
        registerDirty(text, true);
    }

    /**
     * Private constructor.
     */
    private UtilText()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
