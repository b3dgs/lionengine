/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait.producible;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.configurer.ConfigProducible;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.trait.TraitModel;

/**
 * Represents a producible object.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProducibleModel
        extends TraitModel
        implements Producible
{
    /** Producer listeners. */
    private final Collection<ProducibleListener> listeners;
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
     * The {@link Configurer} must provide a valid configuration compatible with {@link ConfigProducible}.
     * </p>
     * 
     * @param owner The owner reference.
     * @param configurer The configurer reference.
     */
    public ProducibleModel(ObjectGame owner, Configurer configurer)
    {
        super(owner);
        Check.notNull(configurer);
        listeners = new ArrayList<>();
        final ConfigProducible configProducible = ConfigProducible.create(configurer);
        media = owner.getMedia();
        steps = configProducible.getSteps();
        width = configProducible.getWidth();
        height = configProducible.getHeight();
    }

    /*
     * Producible
     */

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
