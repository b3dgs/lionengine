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
package com.b3dgs.lionengine.editor.properties.collisioncategory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.properties.collisioncategory.editor.CollisionCategoryEditor;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionCategory;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesCollisionCategory
        implements PropertiesProviderObject
{
    /** Collision category icon. */
    private static final Image ICON_CATEGORY = UtilEclipse.getIcon("properties", "collisioncategory.png");

    /**
     * Create the attribute formulas.
     * 
     * @param properties The properties tree reference.
     */
    private static void createAttributeCollisionCategories(Tree properties)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        item.setText(Messages.Properties_CollisionCategory);
        item.setData(ConfigCollisionCategory.CATEGORY);
        item.setImage(ICON_CATEGORY);
    }

    /*
     * PropertiesProviderObject
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
        if (root.hasChild(ConfigCollisionCategory.CATEGORY))
        {
            createAttributeCollisionCategories(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (ConfigCollisionCategory.CATEGORY.equals(data))
        {
            final CollisionCategoryEditor editor = new CollisionCategoryEditor(item.getParent(), configurer);
            editor.create();
            editor.openAndWait();
        }
        return false;
    }
}
