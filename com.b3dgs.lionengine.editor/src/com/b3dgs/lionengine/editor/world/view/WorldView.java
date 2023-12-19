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
package com.b3dgs.lionengine.editor.world.view;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the world view.
 */
public interface WorldView
{
    /**
     * Update the view.
     */
    void update();

    /**
     * Set the tool item text.
     * 
     * @param item The item id extract.
     * @param text The item text.
     */
    void setToolItemText(String item, String text);

    /**
     * Get the tool item.
     * 
     * @param <T> The element type.
     * @param item The item id extract.
     * @param clazz The element class.
     * @return The composite found.
     * @throws LionEngineException If not found.
     */
    <T> T getToolItem(String item, Class<T> clazz);
}
