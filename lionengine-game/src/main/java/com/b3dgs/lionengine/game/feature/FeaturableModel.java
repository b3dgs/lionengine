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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handlable;

/**
 * Featurable model default implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Features to prepare. */
    private final Collection<Feature> featuresToPrepare = new ArrayList<Feature>();
    /** Features provider. */
    private final Features features = new Features();

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();
    }

    @Override
    public void prepareFeatures(Handlable owner, Services services)
    {
        for (final Feature feature : featuresToPrepare)
        {
            feature.prepare(owner, services);
        }
        featuresToPrepare.clear();
    }

    /*
     * Featurable
     */

    @Override
    public void addFeature(Feature feature)
    {
        featuresToPrepare.add(feature);
        features.add(feature);
    }

    @Override
    public <C extends Feature> C getFeature(Class<C> feature)
    {
        final C found;
        if (feature.isAssignableFrom(getClass()))
        {
            found = feature.cast(this);
        }
        else
        {
            found = features.get(feature);
        }
        return found;
    }

    @Override
    public Iterable<Feature> getFeatures()
    {
        return features.getFeatures();
    }

    @Override
    public Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.getFeaturesType();
    }

    @Override
    public boolean hasFeature(Class<? extends Feature> feature)
    {
        final boolean hasTrait;
        if (feature.isAssignableFrom(getClass()))
        {
            hasTrait = true;
        }
        else
        {
            hasTrait = features.contains(feature);
        }
        return hasTrait;
    }
}
