/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision.object;

import java.util.Collection;

import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.CollisionConfig;

/**
 * Represents the collisions list, allowing to add and remove {@link Collision}.
 */
public class CollisionList extends ObjectList<Collision>
{
    /** Configurer reference. */
    private final Configurer configurer;

    /**
     * Create a collision list and associate its configurer and properties.
     * 
     * @param configurer The configurer reference.
     * @param properties The properties reference.
     */
    public CollisionList(Configurer configurer, CollisionProperties properties)
    {
        super(Collision.class, properties);
        this.configurer = configurer;
    }

    /**
     * Load the existing collisions from the configurer.
     */
    public void loadCollisions()
    {
        final CollisionConfig configCollisions = CollisionConfig.imports(configurer);
        final Collection<Collision> collisions = configCollisions.getCollisions();
        loadObjects(collisions);
    }

    /*
     * ObjectList
     */

    @Override
    protected Collision copyObject(Collision collision)
    {
        return new Collision(collision.getName(),
                             collision.getOffsetX(),
                             collision.getOffsetY(),
                             collision.getWidth(),
                             collision.getHeight(),
                             collision.hasMirror());
    }

    @Override
    protected Collision createObject(String name)
    {
        return new Collision(name, 0, 0, 0, 0, false);
    }
}
