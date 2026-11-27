/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphics;

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Key of an image in an {@link ImageCache}: the resource key and a discriminator, empty for the image decoded from the
 * resource (ADR-0007, ADR-0026).
 *
 * @param key The resource key.
 * @param discriminator The discriminator, empty for the decoded image.
 */
record ImageKey(ResourceKey key, String discriminator)
{
    // Value type of the cache.
}
