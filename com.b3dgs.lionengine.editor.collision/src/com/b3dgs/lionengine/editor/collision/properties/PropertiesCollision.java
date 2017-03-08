/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.collision.object.CollisionsObjectEditDialog;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.collidable.CollisionConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Element properties part.
 */
public class PropertiesCollision implements PropertiesProviderObject
{
    /** Collisions icon. */
    private static final Image ICON_COLLISIONS = UtilIcon.get("properties", "collisions.png");

    /**
     * Create the collisions attribute.
     * 
     * @param properties The properties tree reference.
     */
    public static void createAttributeCollisions(Tree properties)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        item.setText(Messages.Collisions);
        item.setData(CollisionConfig.COLLISION);
        item.setImage(PropertiesCollision.ICON_COLLISIONS);
    }

    /**
     * Create properties.
     */
    public PropertiesCollision()
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
        if (root.hasChild(CollisionConfig.COLLISION))
        {
            createAttributeCollisions(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (CollisionConfig.COLLISION.equals(data))
        {
            final CollisionsObjectEditDialog editor = new CollisionsObjectEditDialog(item.getParent(), configurer);
            editor.create();
            editor.openAndWait();
        }
        return false;
    }
}
