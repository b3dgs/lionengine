/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Represents a producible object.
 */
public class ProducibleModel extends FeatureModel implements Producible
{
    /** Producer listeners. */
    private final Collection<ProducibleListener> listeners = new ArrayList<ProducibleListener>();
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
     * Create a producible and load its configuration.
     * <p>
     * The {@link Setup} must provide a valid {@link ProducibleConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducibleListener}, it will automatically
     * {@link #addListener(ProducibleListener)} on it.
     * </p>
     * 
     * @param setup The setup reference.
     */
    public ProducibleModel(Setup setup)
    {
        super();

        final ProducibleConfig configProducible = ProducibleConfig.imports(setup);
        media = setup.getMedia();
        steps = configProducible.getSteps();
        width = configProducible.getWidth();
        height = configProducible.getHeight();
    }

    /*
     * Producible
     */

    @Override
    public void prepare(Featurable owner, Services services)
    {
        super.prepare(owner, services);

        if (owner instanceof ProducibleListener)
        {
            addListener((ProducibleListener) owner);
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
        listeners.add(listener);
    }

    @Override
    public Collection<ProducibleListener> getListeners()
    {
        return listeners;
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
}
