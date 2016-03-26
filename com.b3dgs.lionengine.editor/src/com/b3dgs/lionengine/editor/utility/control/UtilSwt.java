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
package com.b3dgs.lionengine.editor.utility.control;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.utility.Focusable;

/**
 * Series of tool functions around the editor related to SWT.
 */
public final class UtilSwt
{
    /** Dirty key. */
    static final String KEY_DIRTY = "dirty";

    /**
     * Pack, center and open the shell.
     * 
     * @param shell The shell to open.
     */
    public static void open(Shell shell)
    {
        shell.pack();
        center(shell);
        shell.open();
    }

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
     * Create a border less grid layout.
     * 
     * @return The border less layout.
     */
    public static GridLayout borderless()
    {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    /**
     * Create the focusable listener.
     * 
     * @param focusable The focusable element.
     * @return The listener instance.
     */
    public static MouseTrackListener createFocusListener(final Focusable focusable)
    {
        return new MouseTrackListener()
        {
            @Override
            public void mouseEnter(MouseEvent event)
            {
                focusable.focus();
            }

            @Override
            public void mouseExit(MouseEvent event)
            {
                // Nothing to do
            }

            @Override
            public void mouseHover(MouseEvent event)
            {
                // Nothing to do
            }
        };
    }

    /**
     * Install the mouse wheel scroll.
     * 
     * @param scrollable The scrollable component.
     */
    public static void installMouseWheelScroll(final ScrolledComposite scrollable)
    {
        final MouseWheelListener scroller = createMouseWheelScroller(scrollable);
        if (scrollable.getParent() != null)
        {
            scrollable.getParent().addMouseWheelListener(scroller);
        }
        installMouseWheelScrollRecursively(scroller, scrollable);
    }

    /**
     * Set the dirty flag.
     * 
     * @param shell The shell reference.
     * @param dirty <code>true</code> if dirty, <code>false</code> else.
     */
    public static void setDirty(Shell shell, boolean dirty)
    {
        final Object data = shell.getData();
        if (data instanceof MDirtyable)
        {
            ((MDirtyable) data).setDirty(dirty);
        }
    }

    /**
     * Set the enabled state for parent and all its children.
     * 
     * @param parent The parent reference.
     * @param enabled The enabled flag.
     */
    public static void setEnabled(Composite parent, boolean enabled)
    {
        for (final Control control : parent.getChildren())
        {
            if (control instanceof Composite)
            {
                setEnabled((Composite) control, enabled);
            }
            else
            {
                if (parent instanceof ToolBar)
                {
                    for (final ToolItem item : ((ToolBar) parent).getItems())
                    {
                        item.setEnabled(enabled);
                    }
                }
                else if (parent instanceof Tree)
                {
                    for (final TreeItem item : ((Tree) parent).getItems())
                    {
                        item.setGrayed(!enabled);
                    }
                }
                control.setEnabled(enabled);
            }
        }
        parent.setEnabled(enabled);
    }

    /**
     * Create the mouse wheel scroller.
     * 
     * @param scrollable The scrollable component.
     * @return The scroller instance.
     */
    private static MouseWheelListener createMouseWheelScroller(final ScrolledComposite scrollable)
    {
        return e ->
        {
            final Point currentScroll = scrollable.getOrigin();
            scrollable.setOrigin(currentScroll.x, currentScroll.y - e.count * 5);
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
                installMouseWheelScrollRecursively(scroller, child);
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilSwt()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
