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
package com.b3dgs.lionengine.editor.collisioncategory.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.dialog.EditorAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategoryConfig;

/**
 * Collision category editor dialog.
 */
public class CollisionCategoryEditDialog extends EditorAbstract
{
    /** Dialog icon. */
    private static final Image ICON = UtilIcon.get("collisioncategory-editor", "dialog.png");

    /** Configurer reference. */
    private final Configurer configurer;
    /** Properties. */
    private final CollisionCategoryProperties properties = new CollisionCategoryProperties();
    /** Collision category list. */
    private final CollisionCategoryList list;

    /**
     * Create a collision category editor and associate its configurer.
     * 
     * @param parent The parent reference.
     * @param configurer The entity configurer reference.
     */
    public CollisionCategoryEditDialog(Composite parent, Configurer configurer)
    {
        super(parent, Messages.Title, ICON);
        this.configurer = configurer;
        list = new CollisionCategoryList(configurer, properties);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        list.addListener(properties);

        final Composite area = new Composite(parent, SWT.NONE);
        area.setLayout(new GridLayout(2, false));
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        list.create(area);
        properties.create(area);
        list.loadCategories();
    }

    @Override
    protected void onExit()
    {
        list.save();

        final Xml root = configurer.getRoot();
        root.removeChildren(CollisionCategoryConfig.NODE_CATEGORY);

        for (final TreeItem item : list.getTree().getItems())
        {
            final CollisionCategory category = (CollisionCategory) item.getData();
            CollisionCategoryConfig.exports(root, category);
        }
        configurer.save();
    }
}
