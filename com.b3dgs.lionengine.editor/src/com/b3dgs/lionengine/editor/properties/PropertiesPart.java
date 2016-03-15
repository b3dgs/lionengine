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
package com.b3dgs.lionengine.editor.properties;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilExtension;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.utility.UtilTree;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Element properties part.
 */
public class PropertiesPart implements Focusable, PropertiesProviderObject, PropertiesProviderTile
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";
    /** Menu ID. */
    public static final String MENU_ID = ID + ".menu";
    /** Property key column. */
    public static final int COLUMN_KEY = 0;
    /** Property value column. */
    public static final int COLUMN_VALUE = 1;

    /**
     * Create a line property for tree table.
     * 
     * @param item The item representing the line.
     * @param key The key column.
     * @param property The property column.
     */
    public static void createLine(TreeItem item, String key, String property)
    {
        item.setText(new String[]
        {
            key, property
        });
    }

    /** Properties tree. */
    Tree properties;
    /** Extensions point object. */
    private Collection<PropertiesProviderObject> providersObject;
    /** Extensions point tile. */
    private Collection<PropertiesProviderTile> providersTile;

    /**
     * Create part.
     */
    public PropertiesPart()
    {
        super();
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        properties = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        properties.setLayout(new FillLayout());
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        properties.setHeaderVisible(true);
        properties.addMouseTrackListener(UtilSwt.createFocusListener(this));

        final Listener listener = UtilTree.createAutosizeListener();
        properties.addListener(SWT.Collapse, listener);
        properties.addListener(SWT.Expand, listener);

        final TreeColumn key = new TreeColumn(properties, SWT.LEFT);
        key.setText(Messages.Properties_Key);

        final TreeColumn property = new TreeColumn(properties, SWT.LEFT);
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        property.setText(Messages.Properties_Value);

        addListeners(menuService);
        PropertiesModel.INSTANCE.setTree(properties);

        providersObject = UtilExtension.get(PropertiesProviderObject.class, PropertiesProviderObject.EXTENSION_ID);
        providersTile = UtilExtension.get(PropertiesProviderTile.class, PropertiesProviderTile.EXTENSION_ID);
    }

    /**
     * Clear properties.
     */
    public void clear()
    {
        for (final TreeItem item : properties.getItems())
        {
            clear(item);
        }
        properties.setData(null);
    }

    /**
     * Clear all sub items.
     * 
     * @param item The item root.
     */
    public void clear(TreeItem item)
    {
        item.setData(null);
        for (final TreeItem current : item.getItems())
        {
            clear(current);
        }
        item.dispose();
    }

    /**
     * Add a property provider.
     * 
     * @param provider The provider reference.
     */
    public void addProvider(PropertiesProviderObject provider)
    {
        providersObject.add(provider);
    }

    /**
     * Add a property provider.
     * 
     * @param provider The provider reference.
     */
    public void addProvider(PropertiesProviderTile provider)
    {
        providersTile.add(provider);
    }

    /**
     * Get the tree properties.
     * 
     * @return The tree properties.
     */
    public Tree getTree()
    {
        return properties;
    }

    /**
     * Set the focus.
     */
    @Override
    @Focus
    public void focus()
    {
        properties.setFocus();
    }

    /**
     * Called on double click.
     * 
     * @param item The selected item.
     * @param configurer The associated configurer.
     */
    void onDoubleClick(TreeItem item, Configurer configurer)
    {
        if (updateProperties(item, configurer))
        {
            configurer.save();
        }
    }

    /**
     * Add mouse tree listener.
     * 
     * @param menuService The menu service reference.
     */
    private void addListeners(EMenuService menuService)
    {
        properties.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent event)
            {
                final TreeItem[] items = properties.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    if (properties.getData() instanceof Configurer)
                    {
                        final Configurer configurer = (Configurer) properties.getData();
                        onDoubleClick(item, configurer);
                    }
                }
            }
        });
        properties.addMenuDetectListener(menuDetectEvent ->
        {
            properties.getMenu().setVisible(false);
            properties.update();
        });
        menuService.registerContextMenu(properties, MENU_ID);
    }

    /*
     * PropertiesProviderObject
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        for (final TreeItem item : properties.getItems())
        {
            clear(item);
        }
        properties.setData(configurer);
        if (configurer != null)
        {
            for (final PropertiesProviderObject provider : providersObject)
            {
                provider.setInput(properties, configurer);
            }
        }
        for (final TreeItem item : properties.getItems())
        {
            UtilTree.autoSize(item);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        boolean updated = false;
        for (final PropertiesProviderObject provider : providersObject)
        {
            if (provider.updateProperties(item, configurer))
            {
                updated = true;
            }
        }
        return updated;
    }

    /*
     * PropertiesProviderTile
     */

    @Override
    public void setInput(Tree properties, Tile tile)
    {
        for (final TreeItem item : properties.getItems())
        {
            clear(item);
        }
        properties.setData(tile);
        for (final PropertiesProviderTile provider : providersTile)
        {
            provider.setInput(properties, tile);
        }
        for (final TreeItem item : properties.getItems())
        {
            UtilTree.autoSize(item);
        }
    }
}
