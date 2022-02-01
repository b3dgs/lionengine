/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Represents a resource which must be loaded before be used. Usually heavy, it may takes some time to load it.
 * 
 * @see ResourceLoader
 */
public interface Resource
{
    /**
     * Load resource. This function must be called if the resource is loaded from a file. It will only load data from
     * source to memory.
     * <p>
     * Must be called only one time.
     * </p>
     * 
     * @throws LionEngineException If an error occurred when loading the resource or already loaded.
     */
    void load();

    /**
     * Check if resource has been loaded.
     * 
     * @return <code>true</code> if loaded, <code>false</code> else.
     */
    boolean isLoaded();

    /**
     * Dispose the associated resources if needed.
     */
    void dispose();
}
