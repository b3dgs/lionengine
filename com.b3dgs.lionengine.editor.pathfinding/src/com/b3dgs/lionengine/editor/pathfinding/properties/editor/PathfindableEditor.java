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
package com.b3dgs.lionengine.editor.pathfinding.properties.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathData;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Pathfindable editor dialog.
 */
public class PathfindableEditor extends AbstractEditor
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("pathfindable-editor", "dialog.png");

    /** Path properties. */
    private final PathProperties properties = new PathProperties();
    /** Configurer reference. */
    private final Configurer configurer;
    /** Path list. */
    private final PathList list;

    /**
     * Create a pathfindable editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public PathfindableEditor(Composite parent, Configurer configurer)
    {
        super(parent, Messages.Title, ICON);
        this.configurer = configurer;
        list = new PathList(properties);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        list.create(content);
        properties.create(content);
        list.addListener(properties);
        list.loadPaths(configurer);
    }

    @Override
    protected void onExit()
    {
        list.save();

        final Xml root = configurer.getRoot();
        root.removeChildren(PathfindableConfig.PATHFINDABLE);

        final Map<String, PathData> data = new HashMap<>();
        for (final TreeItem item : list.getTree().getItems())
        {
            final PathData path = (PathData) item.getData();
            data.put(path.getName(), path);
        }
        root.add(PathfindableConfig.exports(data));

        configurer.save();
    }
}
