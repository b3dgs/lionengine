/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.Featurable;

/**
 * Represents something that can be delegated to perform specialized computing and reduce {@link Featurable} visible
 * complexity.
 */
public interface Feature extends FeatureProvider
{
    /**
     * Prepare the feature. Automatically called when added with {@link Featurable#addFeature(Feature)}.
     * 
     * @param provider The owner reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void prepare(FeatureProvider provider);

    /**
     * Check object interface listening and add them automatically. If the {@link Feature} provide listeners, this will
     * allow to add them automatically.
     * 
     * @param listener The listener to check (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void checkListener(Object listener);
}
