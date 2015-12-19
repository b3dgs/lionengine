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
package com.b3dgs.lionengine.editor.properties.collision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.properties.collision.editor.EntityCollisionEditor;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.collision.object.CollisionConfig;
import com.b3dgs.lionengine.stream.XmlNode;

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
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Properties_Collisions);
        animationsItem.setData(CollisionConfig.COLLISION);
        animationsItem.setImage(PropertiesCollision.ICON_COLLISIONS);
    }

    /**
     * Create properties.
     */
    public PropertiesCollision()
    {
        // Nothing to do
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
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
            final EntityCollisionEditor collisionsEditor = new EntityCollisionEditor(item.getParent(), configurer);
            collisionsEditor.create();
            collisionsEditor.openAndWait();
        }
        return false;
    }
}
