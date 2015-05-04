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
package com.b3dgs.lionengine.editor.properties;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
        implements PropertiesProviderObject, PropertiesProviderTile
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

    /**
     * Check the properties extension point object.
     * 
     * @param <P> The properties type.
     * @param clazz The class type.
     * @param id The extension id.
     * @param extension The extension attribute.
     * @return The properties instance from extension point or default one.
     */
    private static <P> Collection<P> checkPropertiesExtensionPoint(Class<P> clazz, String id, String extension)
    {
        final IConfigurationElement[] nodes = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
        final Collection<P> extensions = new ArrayList<>();
        for (final IConfigurationElement node : nodes)
        {
            final String properties = node.getAttribute(extension);
            if (properties != null)
            {
                try
                {
                    final P provider = UtilEclipse.createClass(properties, clazz);
                    extensions.add(provider);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(PropertiesPart.class, "checkPropertiesExtensionPoint", exception);
                }
            }
        }
        return extensions;
    }

    /** Properties tree. */
    Tree properties;
    /** Extensions point object. */
    private Collection<PropertiesProviderObject> providersObject;
    /** Extensions point tile. */
    private Collection<PropertiesProviderTile> providersTile;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        final Listener listener = UtilSwt.createAutosizeListener();
        properties = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        properties.setLayout(new FillLayout());
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        properties.setHeaderVisible(true);
        properties.addListener(SWT.Collapse, listener);
        properties.addListener(SWT.Expand, listener);

        final TreeColumn key = new TreeColumn(properties, SWT.LEFT);
        key.setText(Messages.Properties_Key);

        final TreeColumn property = new TreeColumn(properties, SWT.LEFT);
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        property.setText(Messages.Properties_Value);

        addListeners(menuService);
        PropertiesModel.INSTANCE.setTree(properties);

        providersObject = checkPropertiesExtensionPoint(PropertiesProviderObject.class,
                PropertiesProviderObject.EXTENSION_ID, PropertiesProviderObject.EXTENSION_PROPERTIES);
        providersTile = checkPropertiesExtensionPoint(PropertiesProviderTile.class,
                PropertiesProviderTile.EXTENSION_ID, PropertiesProviderTile.EXTENSION_PROPERTIES);
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
    @Focus
    public void setFocus()
    {
        properties.setFocus();
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
            public void mouseDoubleClick(MouseEvent e)
            {
                final TreeItem[] items = properties.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    if (properties.getData() instanceof Configurer)
                    {
                        final Configurer configurer = (Configurer) properties.getData();
                        if (updateProperties(item, configurer))
                        {
                            configurer.save();
                        }
                    }
                }
            }
        });
        properties.addMenuDetectListener(new MenuDetectListener()
        {
            @Override
            public void menuDetected(MenuDetectEvent menuDetectEvent)
            {
                properties.getMenu().setVisible(false);
                properties.update();
            }
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
            UtilSwt.autoSize(item);
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
            UtilSwt.autoSize(item);
        }
    }
}
