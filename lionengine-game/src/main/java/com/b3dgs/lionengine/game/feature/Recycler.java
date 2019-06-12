/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.game.Feature;

/**
 * Recycler implementation, searching for {@link Recyclable} {@link Feature}s.
 */
@FeatureInterface
public class Recycler extends FeatureModel
{
    /**
     * Create recycler.
     */
    public Recycler()
    {
        super();
    }

    /**
     * Recycle features, to make it ready for reuse.
     */
    public void recycle()
    {
        for (final Feature feature : getFeatures())
        {
            if (feature instanceof Recyclable)
            {
                ((Recyclable) feature).recycle();
            }
        }
    }
}
