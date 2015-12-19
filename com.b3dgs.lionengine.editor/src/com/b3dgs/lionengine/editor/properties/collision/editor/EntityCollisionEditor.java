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
package com.b3dgs.lionengine.editor.properties.collision.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.collision.object.Collision;
import com.b3dgs.lionengine.game.collision.object.CollisionConfig;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Entity collision editor.
 */
public class EntityCollisionEditor extends AbstractEditor
{
    /** Dialog title. */
    public static final String DIALOG_TITLE = "Collisions Editor";
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("collision-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
    /** Properties. */
    private final EntityCollisionProperties entityCollisionProperties = new EntityCollisionProperties();
    /** Collisions list. */
    private final EntityCollisionList entityCollisionList;

    /**
     * Create an entity collision editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public EntityCollisionEditor(Composite parent, Configurer configurer)
    {
        super(parent, EntityCollisionEditor.DIALOG_TITLE, ICON);
        this.configurer = configurer;
        entityCollisionList = new EntityCollisionList(configurer, entityCollisionProperties);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));

        entityCollisionList.addListener(entityCollisionProperties);

        entityCollisionList.create(content);
        entityCollisionProperties.create(content);

        entityCollisionList.loadCollisions();
    }

    @Override
    protected void onExit()
    {
        entityCollisionList.save();

        final XmlNode root = configurer.getRoot();
        root.removeChildren(CollisionConfig.COLLISION);

        for (final TreeItem item : entityCollisionList.getTree().getItems())
        {
            final Collision collision = (Collision) item.getData();
            CollisionConfig.export(root, collision);
        }
        configurer.save();
    }
}
