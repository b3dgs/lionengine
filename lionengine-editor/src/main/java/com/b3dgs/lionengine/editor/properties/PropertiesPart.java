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
package com.b3dgs.lionengine.editor.properties;

import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";

    /**
     * Create the attribute class.
     * 
     * @param configurer The configurer reference.
     */
    private void createAttributeClass(final Configurer configurer)
    {
        final TreeItem classItem = new TreeItem(properties, SWT.NONE);
        classItem.setText(Messages.Properties_Class);

        final TreeItem className = new TreeItem(classItem, SWT.NONE);
        className.setText(configurer.getClassName());
        className.setData(Configurer.CLASS);
    }

    /**
     * Create the surface attribute.
     * 
     * @param configurer The configurer reference.
     */
    private void createAttributeSurface(Configurer configurer)
    {
        final TreeItem surfaceItem = new TreeItem(properties, SWT.NONE);
        surfaceItem.setText(Messages.Properties_Surface);

        final ConfigSurface surface = ConfigSurface.create(configurer);
        final TreeItem surfaceName = new TreeItem(surfaceItem, SWT.NONE);
        surfaceName.setText(surface.getImage());
        surfaceName.setData(ConfigSurface.SURFACE_IMAGE);

        final String icon = surface.getIcon();
        if (icon != null)
        {
            final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
            iconItem.setText(Messages.Properties_SurfaceIcon);

            final TreeItem iconName = new TreeItem(iconItem, SWT.NONE);
            iconName.setText(icon);
            iconName.setData(ConfigSurface.SURFACE_ICON);
        }
    }

    /** Properties tree. */
    Tree properties;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        properties = new Tree(composite, SWT.BORDER);
        properties.setLayout(new GridLayout(1, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        properties.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                final TreeItem[] items = properties.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    final Configurer configurer = (Configurer) properties.getData();
                    final Object data = item.getData();
                    final boolean updated;
                    if (Configurer.CLASS.equals(data))
                    {
                        updated = updateClass(item, configurer);
                    }
                    else if (ConfigSurface.SURFACE_IMAGE.equals(data))
                    {
                        updated = updateSurface(item, configurer);
                    }
                    else
                    {
                        updated = false;
                    }
                    if (updated)
                    {
                        configurer.save();
                    }
                }
            }
        });
    }

    /**
     * Update the class.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateClass(TreeItem item, Configurer configurer)
    {
        final File file = Tools.selectClassFile(properties.getShell());
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode classeNode = root.getChild(Configurer.CLASS);
            final String clazz = Tools.getClass(file).getName();
            classeNode.setText(clazz);
            item.setText(clazz);
            return true;
        }
        return false;
    }

    /**
     * Update the surface.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateSurface(TreeItem item, Configurer configurer)
    {
        final String file = Tools.selectFile(properties.getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
            surfaceNode.writeString(ConfigSurface.SURFACE_IMAGE, file);
            item.setText(file);
            return true;
        }
        return false;
    }

    /**
     * Set the properties input.
     * 
     * @param configurer The configurer reference.
     */
    public void setInput(Configurer configurer)
    {
        properties.clearAll(true);
        properties.setData(configurer);
        if (configurer != null)
        {
            createAttributeClass(configurer);
            if (configurer.getRoot().hasChild(ConfigSurface.SURFACE))
            {
                createAttributeSurface(configurer);
            }
        }
    }
}
