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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;

/**
 * Represents a producible object.
 */
public class ProducibleModel extends FeatureModel implements Producible, Recyclable
{
    /** Producer listeners. */
    private final ListenableModel<ProducibleListener> listenable = new ListenableModel<>();
    /** Producible media. */
    private final Media media;
    /** Production steps needed. */
    private final int steps;
    /** Production width. */
    private final int width;
    /** Production height. */
    private final int height;
    /** Production location x. */
    private double x;
    /** Production location y. */
    private double y;

    /**
     * Create feature.
     * <p>
     * The {@link Configurer} can provide a valid {@link ProducibleConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducibleListener}, it will automatically
     * {@link #addListener(ProducibleListener)} on it.
     * </p>
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public ProducibleModel(Configurer configurer)
    {
        super();

        Check.notNull(configurer);

        if (configurer.hasNode(ProducibleConfig.NODE_PRODUCIBLE))
        {
            final ProducibleConfig configProducible = ProducibleConfig.imports(configurer);
            media = configurer.getMedia();
            steps = configProducible.getSteps();
            width = configProducible.getWidth();
            height = configProducible.getHeight();
        }
        else
        {
            media = null;
            steps = 0;
            width = 0;
            height = 0;
        }
    }

    /*
     * Producible
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (provider instanceof ProducibleListener)
        {
            addListener((ProducibleListener) provider);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof ProducibleListener)
        {
            addListener((ProducibleListener) listener);
        }
    }

    @Override
    public void addListener(ProducibleListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(ProducibleListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public List<ProducibleListener> getListeners()
    {
        return listenable.get();
    }

    @Override
    public void setLocation(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public Media getMedia()
    {
        return media;
    }

    @Override
    public int getSteps()
    {
        return steps;
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        x = 0.0;
        y = 0.0;
    }
}
