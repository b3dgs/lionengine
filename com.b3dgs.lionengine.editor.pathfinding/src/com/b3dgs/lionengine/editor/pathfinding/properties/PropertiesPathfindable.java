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
package com.b3dgs.lionengine.editor.pathfinding.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.pathfinding.properties.editor.PathfindableEditor;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableConfig;

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
        animationsItem.setData(PathfindableConfig.NODE_PATHFINDABLE);
        animationsItem.setImage(ICON_PATHFINDING);
    }

    /**
     * Create properties.
     */
    public PropertiesPathfindable()
    {
        super();
    }

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (root.hasNode(PathfindableConfig.NODE_PATHFINDABLE))
        {
            createAttributePathfindable(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (PathfindableConfig.NODE_PATHFINDABLE.equals(data))
        {
            final PathfindableEditor editor = new PathfindableEditor(item.getParent(), configurer);
            editor.create();
            editor.openAndWait();
        }
        return false;
    }
}
