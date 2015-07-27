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
package com.b3dgs.lionengine.editor.project.dialog.collision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionsEditDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Collisions media. */
    final Media collisions;
    /** Collisions properties. */
    private final CollisionsProperties properties = new CollisionsProperties();
    /** Collisions list. */
    private final CollisionList list = new CollisionList(properties);

    /**
     * Create a collisions edit dialog.
     * 
     * @param parent The parent shell.
     * @param collisions The collisions media.
     */
    public CollisionsEditDialog(Shell parent, Media collisions)
    {
        super(parent, Messages.EditCollisionsDialog_Title, Messages.EditCollisionsDialog_HeaderTitle,
                Messages.EditCollisionsDialog_HeaderDesc, ICON);
        this.collisions = collisions;
        dialog.setMinimumSize(128, 320);
        createDialog();
        finish.setEnabled(true);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        list.create(content);
        list.addListener(list);

        properties.create(content);
        list.addListener(properties);
        list.loadCollisions(collisions);
    }

    @Override
    protected void onFinish()
    {
        list.save();

        final XmlNode root = Stream.createXmlNode(ConfigCollisionGroup.COLLISIONS);
        root.writeString(Configurer.HEADER, EngineCore.WEBSITE);

        for (final TreeItem item : list.getTree().getItems())
        {
            final CollisionGroup collision = (CollisionGroup) item.getData();
            final XmlNode nodeGroup = root.createChild(ConfigCollisionGroup.COLLISION);
            nodeGroup.writeString(ConfigCollisionGroup.GROUP, collision.getName());

            for (final CollisionFormula formula : collision.getFormulas())
            {
                final XmlNode nodeFormula = nodeGroup.createChild(ConfigCollisionFormula.FORMULA);
                nodeFormula.setText(formula.getName());
            }
        }
        Stream.saveXml(root, collisions);

        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(mapCollision.getFormulasConfig(), collisions);
        }
    }
}
