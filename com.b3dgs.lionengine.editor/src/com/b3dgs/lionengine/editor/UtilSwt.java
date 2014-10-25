/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.UtilConversion;

/**
 * Series of tool functions around the editor related to SWT.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilSwt
{
    /**
     * Center the shell on screen.
     * 
     * @param shell The shell to center.
     */
    public static void center(Shell shell)
    {
        final Monitor primary = shell.getMonitor();
        final Rectangle bounds = primary.getBounds();
        final Rectangle rect = shell.getBounds();
        final int x = bounds.x + (bounds.width - rect.width) / 2;
        final int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /**
     * Create a button with a text and an icon at a fixed width.
     * 
     * @param parent The composite parent.
     * @param name The button name.
     * @param icon The button icon.
     * @return The button instance.
     */
    public static Button createButton(Composite parent, String name, Image icon)
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
     * Get the selected item number from the tree.
     * 
     * @param tree The tree reference.
     * @param item The item to search.
     * @return The item index.
     */
    public static int getItemIndex(Tree tree, TreeItem item)
    {
        int i = 0;
        for (final TreeItem current : tree.getItems())
        {
            if (current.equals(item))
            {
                break;
            }
            i++;
        }
        return i;
    }

    /**
     * Create a combo from an enumeration. Selected item can be accessed with {@link Combo#getData()}.
     * Combo items are enum names as title case, converted by {@link UtilConversion#toTitleCase(String)}.
     * 
     * @param parent The parent reference.
     * @param values The enumeration values.
     * @return The combo instance.
     */
    public static Combo createCombo(Composite parent, Enum<?>[] values)
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
        combo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                combo.setData(links.get(combo.getText()));
            }
        });
        return combo;
    }

    /**
     * Install the mouse wheel scroll.
     * 
     * @param scrollable The scrollable component.
     */
    public static void installMouseWheelScroll(final ScrolledComposite scrollable)
    {
        final MouseWheelListener scroller = UtilSwt.createMouseWheelScroller(scrollable);
        if (scrollable.getParent() != null)
        {
            scrollable.getParent().addMouseWheelListener(scroller);
        }
        UtilSwt.installMouseWheelScrollRecursively(scroller, scrollable);
    }

    /**
     * Create the mouse wheel scroller.
     * 
     * @param scrollable The scrollable component.
     * @return The scroller instance.
     */
    private static MouseWheelListener createMouseWheelScroller(final ScrolledComposite scrollable)
    {
        return new MouseWheelListener()
        {
            @Override
            public void mouseScrolled(MouseEvent e)
            {
                final org.eclipse.swt.graphics.Point currentScroll = scrollable.getOrigin();
                scrollable.setOrigin(currentScroll.x, currentScroll.y - e.count * 5);
            }
        };
    }

    /**
     * Install the mouse wheel scroller for each sub component.
     * 
     * @param scroller The scroller listener.
     * @param control The control reference.
     */
    private static void installMouseWheelScrollRecursively(MouseWheelListener scroller, Control control)
    {
        control.addMouseWheelListener(scroller);
        if (control instanceof Composite)
        {
            final Composite comp = (Composite) control;
            for (final Control child : comp.getChildren())
            {
                UtilSwt.installMouseWheelScrollRecursively(scroller, child);
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilSwt()
    {
        throw new RuntimeException();
    }
}
