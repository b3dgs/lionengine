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
package com.b3dgs.lionengine.editor.pathfinding.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.PathCategory;
import com.b3dgs.lionengine.game.pathfinding.PathfindingConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Edit map tile path dialog.
 */
public class PathfindingEditDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Category properties. */
    private final CategoryProperties properties = new CategoryProperties();
    /** Categories list. */
    private final CategoryList list = new CategoryList(properties);
    /** Pathfinding config. */
    private BrowseWidget pathfinding;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public PathfindingEditDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON, SWT.SHELL_TRIM);

        createDialog();
        dialog.setMinimumSize(640, 448);
        finish.setEnabled(false);
    }

    /**
     * Set the save folder destination.
     * 
     * @param destination The destination folder.
     */
    public void setLocation(String destination)
    {
        pathfinding.setLocation(destination);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final String[] filter = UtilDialog.getXmlFilter();
        final String label = Messages.PathfindingConfig;
        pathfinding = new BrowseWidget(content, label, label + " (" + filter[0] + ")", filter, true);

        final Composite area = new Composite(content, SWT.NONE);
        area.setLayout(new GridLayout(2, false));
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        list.create(area);
        list.addListener(list);

        properties.create(area);
        list.addListener(properties);

        UtilSwt.setEnabled(area, false);
        pathfinding.addListener(media ->
        {
            if (media.exists())
            {
                list.loadCategories(media);
                UtilSwt.setEnabled(area, true);
                finish.setEnabled(true);
            }
        });
    }

    @Override
    protected void onFinish()
    {
        list.save();

        final XmlNode root = Xml.create(PathfindingConfig.PATHFINDING);
        root.writeString(Configurer.HEADER, Engine.WEBSITE);

        for (final TreeItem item : list.getTree().getItems())
        {
            final PathCategory category = (PathCategory) item.getData();
            final XmlNode nodeCategory = root.createChild(PathfindingConfig.TILE_PATH);
            nodeCategory.writeString(PathfindingConfig.CATEGORY, category.getName());

            for (final String group : category.getGroups())
            {
                final XmlNode nodeGroup = nodeCategory.createChild(TileGroupsConfig.NODE_GROUP);
                nodeGroup.setText(group);
            }
        }
        Xml.save(root, pathfinding.getMedia());

        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTilePath.class))
        {
            final MapTilePath mapPath = map.getFeature(MapTilePath.class);
            mapPath.loadPathfinding(pathfinding.getMedia());
        }
    }
}
