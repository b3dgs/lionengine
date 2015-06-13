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

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Entity collision editor.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityCollisionEditor
        extends AbstractEditor
{
    /** Dialog title. */
    public static final String DIALOG_TITLE = "Collisions Editor";
    /** Dialog icon. */
    public static final Image ICON = UtilEclipse.getIcon("collision-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
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
        entityCollisionList = new EntityCollisionList(configurer);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));

        final EntityCollisionProperties entityCollisionProperties = new EntityCollisionProperties(entityCollisionList);
        entityCollisionList.addListener(entityCollisionProperties);

        entityCollisionList.create(content);
        entityCollisionProperties.create(content);

        entityCollisionList.loadCollisions();
    }

    @Override
    protected void onExit()
    {
        final XmlNode root = configurer.getRoot();
        root.removeChildren(ConfigCollisions.COLLISION);
        for (final TreeItem item : entityCollisionList.getTree().getItems())
        {
            final Collision collision = (Collision) item.getData();
            final XmlNode nodeAnim = ConfigCollisions.createNode(collision);
            root.add(nodeAnim);
        }
        configurer.save();
    }
}
