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
package com.b3dgs.lionengine.editor.collisioncategory.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.collisioncategory.editor.CollisionCategoryEditDialog;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategoryConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Element properties part.
 */
public class PropertiesCollisionCategory implements PropertiesProviderObject
{
    /** Collision category icon. */
    private static final Image ICON_CATEGORY = UtilIcon.get("properties", "collisioncategory.png");

    /**
     * Create the attribute formulas.
     * 
     * @param properties The properties tree reference.
     */
    private static void createAttributeCollisionCategories(Tree properties)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        item.setText(Messages.CollisionCategory);
        item.setData(CollisionCategoryConfig.CATEGORY);
        item.setImage(ICON_CATEGORY);
    }

    /**
     * Create properties.
     */
    public PropertiesCollisionCategory()
    {
        super();
    }

    /*
     * PropertiesProviderObject
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (root.hasChild(CollisionCategoryConfig.CATEGORY))
        {
            createAttributeCollisionCategories(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (CollisionCategoryConfig.CATEGORY.equals(data))
        {
            final CollisionCategoryEditDialog editor = new CollisionCategoryEditDialog(item.getParent(), configurer);
            editor.create();
            editor.openAndWait();
        }
        return false;
    }
}
