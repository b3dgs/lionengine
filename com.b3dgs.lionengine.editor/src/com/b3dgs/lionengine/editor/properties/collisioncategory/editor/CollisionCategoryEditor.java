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
package com.b3dgs.lionengine.editor.properties.collisioncategory.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.collision.tile.CollisionCategory;
import com.b3dgs.lionengine.game.collision.tile.ConfigCollisionCategory;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Collision category editor dialog.
 */
public class CollisionCategoryEditor extends AbstractEditor
{
    /** Dialog icon. */
    public static final Image ICON = UtilIcon.get("collisioncategory-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
    /** Properties. */
    private final CollisionCategoryProperties categoryProperties = new CollisionCategoryProperties();
    /** Collision category list. */
    private final CollisionCategoryList categoryList;

    /**
     * Create a collision category editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public CollisionCategoryEditor(Composite parent, Configurer configurer)
    {
        super(parent, Messages.CollisionCategoryEditor_Title, ICON);
        this.configurer = configurer;
        categoryList = new CollisionCategoryList(configurer, categoryProperties);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        categoryList.addListener(categoryProperties);

        final Composite properties = new Composite(parent, SWT.NONE);
        properties.setLayout(new GridLayout(2, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        categoryList.create(properties);
        categoryProperties.create(properties);
        categoryList.loadCategories();
    }

    @Override
    protected void onExit()
    {
        categoryList.save();

        final XmlNode root = configurer.getRoot();
        root.removeChildren(ConfigCollisionCategory.CATEGORY);

        for (final TreeItem item : categoryList.getTree().getItems())
        {
            final CollisionCategory category = (CollisionCategory) item.getData();
            ConfigCollisionCategory.export(root, category);
        }
        configurer.save();
    }
}
