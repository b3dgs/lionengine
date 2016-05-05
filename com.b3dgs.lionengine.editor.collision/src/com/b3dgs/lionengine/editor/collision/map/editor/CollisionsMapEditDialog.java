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
package com.b3dgs.lionengine.editor.collision.map.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroupConfig;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions map edition dialog.
 */
public class CollisionsMapEditDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Collisions properties. */
    private final CollisionGroupProperties collisionGroupProperties = new CollisionGroupProperties();
    /** Collisions list. */
    private final CollisionGroupList collisionGroupList = new CollisionGroupList(collisionGroupProperties);
    /** Collisions media. */
    private final Media media;

    /**
     * Create a collisions edit dialog.
     * 
     * @param parent The parent shell.
     * @param media The collisions media.
     */
    public CollisionsMapEditDialog(Shell parent, Media media)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        this.media = media;
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
        collisionGroupList.create(content);
        collisionGroupList.addListener(collisionGroupList);

        collisionGroupProperties.create(content);
        collisionGroupList.addListener(collisionGroupProperties);
        collisionGroupList.loadCollisions(media);
    }

    @Override
    protected void onFinish()
    {
        collisionGroupList.save();

        final XmlNode root = Xml.create(CollisionGroupConfig.COLLISIONS);
        root.writeString(Configurer.HEADER, Engine.WEBSITE);

        for (final TreeItem item : collisionGroupList.getTree().getItems())
        {
            final CollisionGroup collisionGroup = (CollisionGroup) item.getData();
            final XmlNode nodeCollision = root.createChild(CollisionGroupConfig.COLLISION);
            nodeCollision.writeString(CollisionGroupConfig.GROUP, collisionGroup.getName());

            for (final CollisionFormula formula : collisionGroup.getFormulas())
            {
                final XmlNode nodeFormula = nodeCollision.createChild(CollisionFormulaConfig.FORMULA);
                nodeFormula.setText(formula.getName());
            }
        }
        Xml.save(root, media);

        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(mapCollision.getFormulasConfig(), media);
        }
    }
}
