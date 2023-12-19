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
package com.b3dgs.lionengine.editor.utility;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Series of tool functions around the editor related to eclipse.
 */
public final class UtilToolbar
{
    /** Item not found. */
    public static final String ERROR_ITEM = "Item not found: ";

    /**
     * Set the tool item selection.
     * 
     * @param toolbar The tool bar reference.
     * @param selected The selection state.
     * @param prefix The elements prefix (relative to the tool bar ID).
     */
    public static void setToolItemSelectionPrefix(MToolBar toolbar, boolean selected, String prefix)
    {
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final String id = element.getElementId().substring(toolbar.getElementId().length() + 1);
            if (element instanceof MDirectToolItem && id.startsWith(prefix))
            {
                ((MDirectToolItem) element).setSelected(selected);
            }
        }
    }

    /**
     * Set the tool item selection.
     * 
     * @param toolbar The tool bar reference.
     * @param selected The selection state.
     * @param names The elements names (relative to the tool bar ID). None for all.
     */
    public static void setToolItemSelection(MToolBar toolbar, boolean selected, String... names)
    {
        final Collection<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final String id = element.getElementId().substring(toolbar.getElementId().length() + 1);
            if (element instanceof MDirectToolItem && (items.isEmpty() || items.contains(id)))
            {
                ((MDirectToolItem) element).setSelected(selected);
            }
        }
    }

    /**
     * Set the tool item enabled.
     * 
     * @param toolbar The tool bar reference.
     * @param enabled The enabled state.
     * @param names The elements names (relative to the tool bar ID).
     */
    public static void setToolItemEnabled(MToolBar toolbar, boolean enabled, String... names)
    {
        final Collection<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final Object widget = element.getWidget();
            if (widget instanceof ToolItem && (items.isEmpty() || toolbarElementContained(element, items)))
            {
                ((ToolItem) widget).setEnabled(enabled);
            }
        }
    }

    /**
     * Set the tool item text.
     * 
     * @param toolbar The tool bar reference.
     * @param name The element name (relative to the tool bar ID).
     * @param text The text value.
     */
    public static void setToolItemText(MToolBar toolbar, String name, String text)
    {
        for (final MToolBarElement element : toolbar.getChildren())
        {
            if (element.getElementId().contains(name))
            {
                setToolItemText(element, text);
            }
        }
    }

    /**
     * Get the tool item.
     * 
     * @param <T> The element type.
     * @param toolbar The tool bar reference.
     * @param name The element name (relative to the tool bar ID).
     * @param clazz The element class.
     * @return The composite found.
     * @throws LionEngineException If not found.
     */
    public static <T> T getToolItem(MToolBar toolbar, String name, Class<T> clazz)
    {
        for (final MToolBarElement element : toolbar.getChildren())
        {
            if (element.getElementId().contains(name) && element instanceof MToolControl)
            {
                return clazz.cast(((MToolControl) element).getObject());
            }
        }
        throw new LionEngineException(ERROR_ITEM + name);
    }

    /**
     * Set the tool item text.
     * 
     * @param element The tool bar element reference.
     * @param text The text value.
     */
    private static void setToolItemText(MToolBarElement element, String text)
    {
        final Object object = element.getWidget();
        if (object instanceof final Composite composite)
        {
            for (final Control control : composite.getChildren())
            {
                if (control instanceof Text)
                {
                    ((Text) control).setText(text);
                }
            }
        }
    }

    /**
     * Check if tool bar element is contained in the list.
     * 
     * @param element The tool bar element.
     * @param items The items list.
     * @return <code>true</code> if contained, <code>false</code> else.
     */
    private static boolean toolbarElementContained(MToolBarElement element, Collection<String> items)
    {
        final String id = element.getElementId();
        for (final String item : items)
        {
            if (id.contains(item))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Private constructor.
     */
    private UtilToolbar()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
