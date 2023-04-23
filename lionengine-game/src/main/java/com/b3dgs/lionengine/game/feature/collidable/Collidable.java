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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.List;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Represents something which can enter in collision with another.
 */
@FeatureInterface
public interface Collidable extends Feature, Renderable, Shape, CollidableListener, CollisionChecker,
                            Listenable<CollidableListener>
{
    /**
     * Remove all listeners.
     */
    void clearListeners();

    /**
     * Add a collision to use.
     * 
     * @param collision The collision to add.
     */
    void addCollision(Collision collision);

    /**
     * Add a group to accept list.
     * 
     * @param group The group to accept on {@link #collide(Collidable)}.
     */
    void addAccept(Integer group);

    /**
     * Remove a group from accept list.
     * 
     * @param group The group to remove on {@link #collide(Collidable)}.
     */
    void removeAccept(Integer group);

    /**
     * Force collision update.
     */
    void forceUpdate();

    /**
     * Check if the collidable entered in collision with another one.
     * 
     * @param collidable The collidable reference.
     * @return <code>true</code> if collided, <code>false</code> else.
     */
    boolean collide(Collidable collidable);

    /**
     * Set the associated group.
     * 
     * @param group The associated group.
     */
    void setGroup(Integer group);

    /**
     * Set the origin to use.
     * 
     * @param origin The origin to use.
     */
    void setOrigin(Origin origin);

    /**
     * Set the collision enabled flag.
     * 
     * @param enabled <code>true</code> to enable collision checking, <code>false</code> else.
     * @param collision The collision to change.
     */
    void setEnabled(boolean enabled, Collision collision);

    /**
     * Set the collision enabled flag.
     * 
     * @param enabled <code>true</code> to enable collision checking, <code>false</code> else.
     */
    void setEnabled(boolean enabled);

    /**
     * Set the collision visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    void setCollisionVisibility(boolean visible);

    /**
     * Check if collision is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    boolean isEnabled();

    /**
     * Get the declared collisions.
     * 
     * @return The declared collisions.
     */
    List<Collision> getCollisions();

    /**
     * Get the collisions bounds.
     * 
     * @return The collisions bounds.
     */
    List<Rectangle> getCollisionBounds();

    /**
     * Get the associated group.
     * 
     * @return The associated group.
     */
    Integer getGroup();

    /**
     * Get the accepted groups.
     * 
     * @return The accepted groups.
     */
    List<Integer> getAccepted();

    /**
     * Get the current max width.
     * 
     * @return The max width.
     */
    int getMinWidth();

    /**
     * Get the current max height.
     * 
     * @return The max height.
     */
    int getMinHeight();

    /**
     * Get the current max width.
     * 
     * @return The max width.
     */
    int getMaxWidth();

    /**
     * Get the current max height.
     * 
     * @return The max height.
     */
    int getMaxHeight();

    /**
     * Get the origin used.
     * 
     * @return The origin used.
     */
    Origin getOrigin();
}
