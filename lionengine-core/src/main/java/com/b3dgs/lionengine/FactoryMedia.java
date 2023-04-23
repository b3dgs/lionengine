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
package com.b3dgs.lionengine;

/**
 * Represents the media factory.
 */
public interface FactoryMedia
{
    /**
     * Create a media from an abstract path.
     * 
     * @param separator The separator used (must not be <code>null</code>).
     * @param resourcesDir The resources directory path (must not be <code>null</code>).
     * @param resourcesClass The class loader used (must not be <code>null</code>).
     * @param path The media path (must not be <code>null</code>).
     * @return The created media.
     * @throws LionEngineException If invalid arguments.
     */
    Media create(String separator, String resourcesDir, Class<?> resourcesClass, String... path);
}
