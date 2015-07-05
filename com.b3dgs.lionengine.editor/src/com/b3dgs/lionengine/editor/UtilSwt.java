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
package com.b3dgs.lionengine.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.UtilConversion;

/**
 * Series of tool functions around the editor related to SWT.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilSwt
{
    /** Dirty key. */
    private static final String KEY_DIRTY = "dirty";

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
     * Create a text with its legend.
     * 
     * @param legend The legend text.
     * @param parent The composite parent.
     * @return The created text.
     */
    public static Text createText(String legend, Composite parent)
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
    public static Combo createCombo(String legend, Composite parent, Enum<?>[] values)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label textLegend = new Label(composite, SWT.HORIZONTAL);
        textLegend.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        textLegend.setText(legend);

        final Combo combo = createCombo(composite, values);
        return combo;
    }

    /**
     * Create a check box button with its legend.
     * 
     * @param legend The check legend.
     * @param parent The parent reference.
     * @return The check instance.
     */
    public static Button createCheck(String legend, Composite parent)
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
     * Create a verify listener.
     * 
     * @param text The text to verify.
     * @param match The expected match.
     * @return The verify listener.
     * @see InputValidator
     */
    public static VerifyListener createVerify(final Text text, final String match)
    {
        return new VerifyListener()
        {
            @Override
            public void verifyText(VerifyEvent event)
            {
                final String init = text.getText();
                final String newText = init.substring(0, event.start) + event.text + init.substring(event.end);
                event.doit = newText.matches(match) || newText.isEmpty();
            }
        };
    }

    /**
     * Auto size tree column and sub items.
     * 
     * @param item The item parent.
     */
    public static void autoSize(TreeItem item)
    {
        for (final TreeColumn column : item.getParent().getColumns())
        {
            column.pack();
        }
    }

    /**
     * Create the auto size listener which will auto size properties.
     * 
     * @return The created listener.
     */
    public static Listener createAutosizeListener()
    {
        return new Listener()
        {
            @Override
            public void handleEvent(Event e)
            {
                final TreeItem item = (TreeItem) e.item;
                item.getDisplay().asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        autoSize(item);
                    }
                });
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
        final MouseWheelListener scroller = UtilSwt.createMouseWheelScroller(scrollable);
        if (scrollable.getParent() != null)
        {
            scrollable.getParent().addMouseWheelListener(scroller);
        }
        UtilSwt.installMouseWheelScrollRecursively(scroller, scrollable);
    }

    /**
     * Register dirty listener on text modification. Reference will be the current text value.
     * 
     * @param text The text reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Text text, boolean enable)
    {
        final Object oldListener = text.getData(KEY_DIRTY);
        if (oldListener instanceof ModifyListener)
        {
            text.removeModifyListener((ModifyListener) oldListener);
        }
        if (enable)
        {
            final ModifyListener listener = new ModifyListener()
            {
                @Override
                public void modifyText(ModifyEvent event)
                {
                    setDirty(text.getShell(), true);
                }
            };
            text.setData(KEY_DIRTY, listener);
            text.addModifyListener(listener);
        }
    }

    /**
     * Register dirty listener on button modification. Reference will be the current text value.
     * 
     * @param button button text reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Button button, boolean enable)
    {
        final Object oldListener = button.getData(KEY_DIRTY);
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
                    setDirty(button.getShell(), true);
                }
            };
            button.setData(KEY_DIRTY, listener);
            button.addSelectionListener(listener);
        }
    }

    /**
     * Register dirty listener on combo modification. Reference will be the current combo value.
     * 
     * @param combo The combo reference.
     * @param enable <code>true</code> to enable dirty detection, <code>false</code> else.
     */
    public static void registerDirty(final Combo combo, boolean enable)
    {
        final Object oldListener = combo.getData(KEY_DIRTY);
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
                    setDirty(combo.getShell(), true);
                }
            };
            combo.setData(KEY_DIRTY, listener);
            combo.addModifyListener(listener);
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
        text.setText(value);
        registerDirty(text, true);
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
     * Set the dirty flag.
     * 
     * @param shell The shell reference.
     * @param dirty <code>true</code> if dirty, <code>false</code> else.
     */
    static void setDirty(Shell shell, boolean dirty)
    {
        final Object data = shell.getData();
        if (data instanceof MDirtyable)
        {
            ((MDirtyable) data).setDirty(dirty);
        }
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
