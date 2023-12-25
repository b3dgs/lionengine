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
package com.b3dgs.lionengine.editor.collision.map.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormula;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;

/**
 * Represents the collisions map edition dialog.
 */
public class CollisionsMapEditDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 128;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 320;

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
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        createDialog();
        finish.setEnabled(true);
    }

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

        final Xml root = new Xml(CollisionGroupConfig.NODE_COLLISIONS);
        root.writeString(Constant.XML_HEADER, Constant.ENGINE_WEBSITE);

        for (final TreeItem item : collisionGroupList.getTree().getItems())
        {
            final CollisionGroup collisionGroup = (CollisionGroup) item.getData();
            final Xml nodeCollision = root.createChild(CollisionGroupConfig.NODE_COLLISION);
            nodeCollision.writeString(CollisionGroupConfig.ATT_GROUP, collisionGroup.getName());

            for (final CollisionFormula formula : collisionGroup.getFormulas())
            {
                final Xml nodeFormula = nodeCollision.createChild(CollisionFormulaConfig.NODE_FORMULA);
                nodeFormula.setText(formula.getName());
            }
        }
        root.save(media);

        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(mapCollision.getFormulasConfig(), media);
        }
    }
}
