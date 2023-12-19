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
package com.b3dgs.lionengine.editor.surface.properties;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SurfaceConfig;

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
        surface.getIcon().ifPresent(icon -> createAttributeIcon(properties, icon));
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
        final AtomicBoolean updated = new AtomicBoolean(false);
        UtilDialog.selectResourceFile(item.getParent().getShell(), true, UtilDialog.getImageFilter()).ifPresent(media ->
        {
            final Xml root = configurer.getRoot();
            final Xml surfaceNode = root.getChildXml(SurfaceConfig.NODE_SURFACE);
            final String path = media.getPath();
            surfaceNode.writeString(SurfaceConfig.ATT_IMAGE, path);
            item.setText(PropertiesPart.COLUMN_VALUE, path);
            updated.set(true);
        });
        return updated.get();
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
        final AtomicBoolean updated = new AtomicBoolean(false);
        UtilDialog.selectResourceFile(item.getParent().getShell(), true, UtilDialog.getImageFilter()).ifPresent(media ->
        {
            final Xml root = configurer.getRoot();
            final Xml surfaceNode = root.getChildXml(SurfaceConfig.NODE_SURFACE);
            final String path = media.getPath();
            surfaceNode.writeString(SurfaceConfig.ATT_ICON, path);
            item.setText(PropertiesPart.COLUMN_VALUE, path);
            updated.set(true);
        });
        return updated.get();
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
        final Xml root = configurer.getRoot();
        if (root.hasNode(SurfaceConfig.NODE_SURFACE))
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
