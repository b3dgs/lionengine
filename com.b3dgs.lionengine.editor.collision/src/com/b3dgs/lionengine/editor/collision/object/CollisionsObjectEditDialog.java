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
package com.b3dgs.lionengine.editor.collision.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.dialog.EditorAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.CollisionConfig;

/**
 * Collision with object editor.
 */
public class CollisionsObjectEditDialog extends EditorAbstract
{
    /** Dialog icon. */
    private static final Image ICON = UtilIcon.get("collision-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
    /** Collision Properties. */
    private final CollisionProperties properties = new CollisionProperties();
    /** Collision list. */
    private final CollisionList list;

    /**
     * Create an entity collision editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public CollisionsObjectEditDialog(Composite parent, Configurer configurer)
    {
        super(parent, Messages.Title, ICON);
        this.configurer = configurer;
        list = new CollisionList(configurer, properties);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));

        list.addListener(properties);

        list.create(content);
        properties.create(content);

        list.loadCollisions();
    }

    @Override
    protected void onExit()
    {
        list.save();

        final Xml root = configurer.getRoot();
        root.removeChildren(CollisionConfig.NODE_COLLISION);

        for (final TreeItem item : list.getTree().getItems())
        {
            final Collision collision = (Collision) item.getData();
            CollisionConfig.exports(root, collision);
        }
        configurer.save();
    }
}
