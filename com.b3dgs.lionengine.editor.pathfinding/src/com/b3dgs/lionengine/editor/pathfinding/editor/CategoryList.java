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
package com.b3dgs.lionengine.editor.pathfinding.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.ObjectListAbstract;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathCategory;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;

/**
 * Represents the categories list, allowing to add and remove pathfinding categories.
 */
public class CategoryList extends ObjectListAbstract<PathCategory> implements ObjectListListener<PathCategory>
{
    /** Pathfinding config. */
    private Media config;

    /**
     * Create the category list.
     * 
     * @param properties The properties reference.
     */
    public CategoryList(CategoryProperties properties)
    {
        super(PathCategory.class, properties);
    }

    /**
     * Load the existing categories from the object configurer.
     * 
     * @param config The config file.
     */
    public void loadCategories(Media config)
    {
        this.config = config;
        final Collection<PathCategory> groups = PathfindingConfig.imports(config);
        loadObjects(groups);
    }

    @Override
    protected PathCategory copyObject(PathCategory category)
    {
        return new PathCategory(category.getName(), category.getGroups());
    }

    @Override
    protected PathCategory createObject(String name)
    {
        return new PathCategory(name, new HashSet<>());
    }

    @Override
    public void notifyObjectSelected(PathCategory category)
    {
        // Nothing to do
    }

    @Override
    public void notifyObjectDeleted(PathCategory category)
    {
        final Collection<Xml> toRemove = new ArrayList<>();

        final Xml node = new Xml(config);
        final Collection<Xml> children = node.getChildrenXml(PathfindingConfig.NODE_PATHFINDING);
        for (final Xml nodePath : children)
        {
            if (CollisionGroup.same(nodePath.getString(PathfindingConfig.ATT_CATEGORY), category.getName()))
            {
                toRemove.add(nodePath);
            }
        }
        children.clear();

        for (final Xml remove : toRemove)
        {
            node.removeChild(remove);
        }
        toRemove.clear();
        node.save(config);

        final MapTile map = WorldModel.INSTANCE.getMap();
        final MapTilePath mapPath = map.getFeature(MapTilePath.class);
        mapPath.loadPathfinding(config);
    }
}
