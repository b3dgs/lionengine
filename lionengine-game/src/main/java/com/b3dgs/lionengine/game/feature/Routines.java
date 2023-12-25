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
package com.b3dgs.lionengine.game.feature;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Handle the declared {@link Routine}.
 */
@FeatureInterface
public class Routines extends FeatureModel implements Updatable, Renderable
{
    /** Routines list. */
    private final List<Routine> routines = new ArrayList<>();
    /** Routines number. */
    private int routinesCount;

    /**
     * Create routines.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Routines(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        for (final Feature feature : provider.getFeatures())
        {
            if (feature instanceof final Routine routine)
            {
                routines.add(routine);
            }
        }
        routinesCount = routines.size();
    }

    @Override
    public void update(double extrp)
    {
        for (int i = 0; i < routinesCount; i++)
        {
            routines.get(i).update(extrp);
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (int i = 0; i < routinesCount; i++)
        {
            routines.get(i).render(g);
        }
    }
}
