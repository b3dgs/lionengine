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
package com.b3dgs.lionengine.editor.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the palette view, where the palette list is displayed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PalettePart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.palette";

    /** Palette list. */
    final Map<String, PaletteData> palettes = new HashMap<>();
    /** Part service. */
    @Inject
    EPartService partService;
    /** Palette combo box. */
    Combo comboPalette;
    /** Palette view composite. */
    Composite composite;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(final Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setLayout(new GridLayout(1, false));

        comboPalette = new Combo(content, SWT.READ_ONLY);
        comboPalette.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        final Label separator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        composite = new Composite(content, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        comboPalette.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                loadPaletteView();
            }
        });
    }

    /**
     * Add a factory.
     * 
     * @param name The palette name.
     * @param view The palette view reference.
     */
    public void addPalette(String name, PaletteView view)
    {
        final PaletteData data = new PaletteData(name, view.getId(), view);
        palettes.put(data.getId(), data);
        final Collection<String> names = new ArrayList<>(palettes.size());
        for (final PaletteData paletteData : palettes.values())
        {
            names.add(paletteData.getName());
        }
        final String[] items = new String[names.size()];
        names.toArray(items);
        comboPalette.setItems(items);
        comboPalette.setData(name, view.getId());
        comboPalette.setText(name);
        comboPalette.update();
        loadPaletteView();
    }

    /**
     * Get the current active palette ID.
     * 
     * @return The current active palette ID.
     */
    public String getActivePaletteId()
    {
        final String name = comboPalette.getText();
        return (String) comboPalette.getData(name);
    }

    /**
     * Get the current selected palette.
     * 
     * @param <C> The class type.
     * @param clazz The palette view class type.
     * @return The current selected palette (<code>null</code> if none).
     */
    public <C> C getPaletteView(Class<C> clazz)
    {
        final String name = comboPalette.getText();
        final String key = (String) comboPalette.getData(name);
        return clazz.cast(palettes.get(key).getView());
    }

    /**
     * Get a palette.
     * 
     * @param <C> The class type.
     * @param id The view ID.
     * @param clazz The palette view class type.
     * @return The current selected palette (<code>null</code> if none).
     */
    public <C> C getPaletteView(String id, Class<C> clazz)
    {
        return clazz.cast(palettes.get(id).getView());
    }

    /**
     * Load the selected palette view.
     */
    void loadPaletteView()
    {
        for (final Control child : composite.getChildren())
        {
            child.dispose();
        }
        final String name = comboPalette.getText();
        final String key = (String) comboPalette.getData(name);
        final PaletteView view = palettes.get(key).getView();
        view.create(composite);
        composite.layout(true, true);
    }
}
