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
package com.b3dgs.lionengine.editor.surface.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.object.SurfaceConfig;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 */
public class PropertiesSurface implements PropertiesProviderObject
{
    /** Surface icon. */
    private static final Image ICON_SURFACE = UtilIcon.get(FOLDER, "surface.png");
    /** Icon icon. */
    private static final Image ICON_ICON = UtilIcon.get(FOLDER, "icon.png");

    /**
     * Create the surface attribute.
     * 
     * @param properties The properties tree reference.
     * @param configurer The configurer reference.
     */
    public static void createAttributeSurface(Tree properties, Configurer configurer)
    {
        final SurfaceConfig surface = SurfaceConfig.imports(configurer);
        final TreeItem surfaceItem = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(surfaceItem, Messages.Surface, surface.getImage());
        surfaceItem.setData(SurfaceConfig.ATT_IMAGE);
        surfaceItem.setImage(ICON_SURFACE);

        final String icon = surface.getIcon();
        if (icon != null)
        {
            createAttributeIcon(properties, icon);
        }
    }

    /**
     * Create the surface attribute.
     * 
     * @param properties The properties tree reference.
     * @param icon The icon path.
     */
    public static void createAttributeIcon(Tree properties, String icon)
    {
        final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(iconItem, Messages.Icon, icon);
        iconItem.setData(SurfaceConfig.ATT_ICON);
        iconItem.setImage(ICON_ICON);
    }

    /**
     * Update the surface.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateSurface(TreeItem item, Configurer configurer)
    {
        final String file = UtilDialog.selectFile(item.getParent().getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(SurfaceConfig.NODE_SURFACE);
            surfaceNode.writeString(SurfaceConfig.ATT_IMAGE, file);
            item.setText(PropertiesPart.COLUMN_VALUE, file);
            return true;
        }
        return false;
    }

    /**
     * Update the icon.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateIcon(TreeItem item, Configurer configurer)
    {
        final String file = UtilDialog.selectFile(item.getParent().getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(SurfaceConfig.NODE_SURFACE);
            surfaceNode.writeString(SurfaceConfig.ATT_ICON, file);
            item.setText(PropertiesPart.COLUMN_VALUE, file);
            return true;
        }
        return false;
    }

    /**
     * Create properties.
     */
    public PropertiesSurface()
    {
        super();
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
        if (root.hasChild(SurfaceConfig.NODE_SURFACE))
        {
            createAttributeSurface(properties, configurer);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        boolean updated = false;
        if (SurfaceConfig.ATT_IMAGE.equals(data))
        {
            updated = updateSurface(item, configurer);
        }
        else if (SurfaceConfig.ATT_ICON.equals(data))
        {
            updated = updateIcon(item, configurer);
        }
        return updated;
    }
}
