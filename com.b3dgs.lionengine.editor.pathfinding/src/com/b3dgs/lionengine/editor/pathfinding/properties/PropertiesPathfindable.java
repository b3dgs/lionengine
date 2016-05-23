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
package com.b3dgs.lionengine.editor.pathfinding.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.pathfinding.properties.editor.PathfindableEditor;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.object.Configurer;
import com.b3dgs.lionengine.game.pathfinding.PathfindableConfig;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 */
public class PropertiesPathfindable implements PropertiesProviderObject
{
    /** Pathfinding icon. */
    private static final Image ICON_PATHFINDING = UtilIcon.get("properties", "pathfindable.png");

    /**
     * Create the pathfinding attribute.
     * 
     * @param properties The properties tree reference.
     */
    public static void createAttributePathfindable(Tree properties)
    {
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Pathfindable);
        animationsItem.setData(PathfindableConfig.PATHFINDABLE);
        animationsItem.setImage(ICON_PATHFINDING);
    }

    /**
     * Create properties.
     */
    public PropertiesPathfindable()
    {
        super();
    }

    /*
     * PropertiesProviderObject
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
        if (root.hasChild(PathfindableConfig.PATHFINDABLE))
        {
            createAttributePathfindable(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (PathfindableConfig.PATHFINDABLE.equals(data))
        {
            final PathfindableEditor editor = new PathfindableEditor(item.getParent(), configurer);
            editor.create();
            editor.openAndWait();
        }
        return false;
    }
}
