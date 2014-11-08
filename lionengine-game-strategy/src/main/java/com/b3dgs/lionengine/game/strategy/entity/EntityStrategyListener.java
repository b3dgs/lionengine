/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.entity;

import java.util.Collection;

/**
 * Entity activities listener.
 * 
 * @param <E> The entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface EntityStrategyListener<E extends EntityStrategy>
{
    /**
     * Called when the entity has moved.
     * 
     * @param entity The entity reference.
     */
    void entityMoved(E entity);

    /**
     * Check the last selected entities. Selection filter can be done here. To apply the filter correctly, it is
     * recommended to unselect the entity with {@link EntityStrategy#setSelection(boolean)} and remove the entity from
     * the selection which is given as a parameter. Example: Ignore a certain type of entity.
     * 
     * @param selection The selected entities.
     */
    void notifyUpdatedSelection(Collection<E> selection);
}
