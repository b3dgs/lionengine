/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Default launchable model implementation.
 */
public class LaunchableModel extends FeatureModel implements Launchable
{
    /** Launcher listeners. */
    private final Collection<LaunchableListener> listeners = new HashSet<LaunchableListener>();
    /** Transformable reference. */
    private Transformable transformable;
    /** Vector reference. */
    private Force vector;

    /**
     * Create the launchable model.
     * <p>
     * The {@link Featurable} owner must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     */
    public LaunchableModel()
    {
        super();
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
        listeners.add(listener);
    }

    @Override
    public void launch()
    {
        for (final LaunchableListener listener : listeners)
        {
            listener.notifyFired(this);
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
}
