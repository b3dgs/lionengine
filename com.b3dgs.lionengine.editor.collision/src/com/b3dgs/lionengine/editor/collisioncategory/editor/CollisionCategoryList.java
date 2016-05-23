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
package com.b3dgs.lionengine.editor.collisioncategory.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.tile.CollisionCategory;
import com.b3dgs.lionengine.game.collision.tile.CollisionCategoryConfig;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.object.Configurer;

/**
 * Represents the collision category list, allowing to add and remove {@link CollisionCategory}.
 */
public class CollisionCategoryList extends ObjectList<CollisionCategory>
{
    /** Configurer reference. */
    private final Configurer configurer;

    /**
     * Create category list and associate its properties.
     * 
     * @param configurer The configurer reference.
     * @param properties The properties reference.
     */
    public CollisionCategoryList(Configurer configurer, CollisionCategoryProperties properties)
    {
        super(CollisionCategory.class, properties);
        this.configurer = configurer;
    }

    /**
     * Load the existing categories from the object configurer.
     */
    public void loadCategories()
    {
        final Collection<CollisionCategory> categories = CollisionCategoryConfig.imports(configurer.getRoot());
        loadObjects(categories);
    }

    /*
     * ObjectList
     */

    @Override
    protected CollisionCategory copyObject(CollisionCategory category)
    {
        return new CollisionCategory(category.getName(),
                                     category.getAxis(),
                                     category.getOffsetX(),
                                     category.getOffsetY(),
                                     category.getGroups());
    }

    @Override
    protected CollisionCategory createObject(String name)
    {
        return new CollisionCategory(name, Axis.Y, 0, 0, new ArrayList<CollisionGroup>());
    }
}
