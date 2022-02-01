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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Launchable model implementation.
 */
public class LaunchableModel extends FeatureModel implements Launchable, Recyclable
{
    /** Launcher listeners. */
    private final ListenableModel<LaunchableListener> listenable = new ListenableModel<>();
    /** Vector reference. */
    private Force vector;

    /** Transformable reference. */
    private Transformable transformable;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public LaunchableModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    /*
     * Launchable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof LaunchableListener)
        {
            addListener((LaunchableListener) listener);
        }
    }

    @Override
    public void addListener(LaunchableListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(LaunchableListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void launch()
    {
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyFired(this);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (vector != null)
        {
            vector.update(extrp);
            transformable.moveLocation(extrp, vector);
        }
    }

    @Override
    public void setLocation(double x, double y)
    {
        transformable.teleport(x, y);
    }

    @Override
    public void setVector(Force force)
    {
        vector = force;
    }

    @Override
    public Force getDirection()
    {
        return vector;
    }

    @Override
    public void recycle()
    {
        vector = null;
    }
}
