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
package com.b3dgs.lionengine.game.feature.launchable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Launchable model implementation.
 */
public class LaunchableModel extends FeatureModel implements Launchable, Recyclable
{
    /** Transformable reference. */
    private final Transformable transformable;

    /** Launcher listeners. */
    private final ListenableModel<LaunchableListener> listenable = new ListenableModel<>();
    /** Update priority. */
    private final int priorityUpdate;

    /** Vector reference. */
    private Force vector;

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
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public LaunchableModel(Services services, Setup setup, Transformable transformable)
    {
        this(services, setup, XmlReader.EMPTY, transformable);
    }

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
     * @param config The feature configuration node (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public LaunchableModel(Services services, Setup setup, XmlReader config, Transformable transformable)
    {
        super(services, setup);

        Check.notNull(config);

        this.transformable = transformable;
        priorityUpdate = config.getInteger(RoutineUpdate.LAUNCHABLE, FeaturableConfig.ATT_PRIORITY_UPDATE);
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof final LaunchableListener l)
        {
            addListener(l);
        }
    }

    @Override
    public void addListener(LaunchableListener listener)
    {
        Check.notNull(listener);

        listenable.addListener(listener);
    }

    @Override
    public void removeListener(LaunchableListener listener)
    {
        Check.notNull(listener);

        listenable.removeListener(listener);
    }

    @Override
    public void launch()
    {
        final int n = listenable.size();
        for (int i = 0; i < n; i++)
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
    public int getPriotityUpdate()
    {
        return priorityUpdate;
    }

    @Override
    public void recycle()
    {
        vector = null;
    }
}
