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
package com.b3dgs.lionengine.game.feature;

/**
 * Feature model implementation.
 * <p>
 * Any feature can override {@link #checkListener(Object)} to automatically add listener of object if implements
 * it/them.
 * </p>
 */
public class FeatureModel implements Feature
{
    /** The provider reference. */
    private FeatureProvider provider;

    /**
     * Create model.
     */
    public FeatureModel()
    {
        super();
    }

    /*
     * Feature
     */

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Does nothing by default.
     * </p>
     */
    @Override
    public void checkListener(Object listener)
    {
        // Nothing by default
    }

    @Override
    public <C extends Feature> C getFeature(Class<C> feature)
    {
        return provider.getFeature(feature);
    }

    @Override
    public Iterable<Feature> getFeatures()
    {
        return provider.getFeatures();
    }

    @Override
    public Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return provider.getFeaturesType();
    }

    @Override
    public boolean hasFeature(Class<? extends Feature> feature)
    {
        return provider.hasFeature(feature);
    }
}
