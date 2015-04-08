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
package com.b3dgs.lionengine.editor.properties.clazz;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProvider;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesClass
        implements PropertiesProvider
{
    /** Class icon. */
    private static final Image ICON_CLASS = UtilEclipse.getIcon("properties", "class.png");

    /**
     * Create the attribute class.
     * 
     * @param properties The properties tree reference.
     * @param configurer The configurer reference.
     */
    private static void createAttributeClass(Tree properties, Configurer configurer)
    {
        final TreeItem classItem = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(classItem, Messages.Properties_Class, configurer.getClassName());
        classItem.setData(Configurer.CLASS);
        classItem.setImage(PropertiesClass.ICON_CLASS);
    }

    /**
     * Update the class.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateClass(TreeItem item, Configurer configurer)
    {
        final File file = Tools.selectClassFile(item.getParent().getShell());
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

    /*
     * PropertiesListener
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        createAttributeClass(properties, configurer);
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        boolean updated = false;
        if (Configurer.CLASS.equals(data))
        {
            updated = updateClass(item, configurer);
        }
        return updated;
    }
}
