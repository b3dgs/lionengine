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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Displayable feature implementation.
 */
public class DisplayableModel extends FeatureAbstract implements Displayable
{
    /** Renderable reference. */
    private final Renderable renderable;

    /**
     * Create feature.
     * 
     * @param renderable The renderable reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public DisplayableModel(Renderable renderable)
    {
        super();

        Check.notNull(renderable);

        this.renderable = renderable;
    }

    @Override
    public void render(Graphic g)
    {
        renderable.render(g);
    }
}
