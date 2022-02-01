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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Abstract feature base.
 */
public class FeatureAbstract implements Feature
{
    /** The provider reference. */
    private FeatureProvider provider;

    /**
     * Create feature.
     */
    public FeatureAbstract()
    {
        super();
    }

    /*
     * Feature
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        Check.notNull(provider);

        this.provider = provider;

        if (this instanceof IdentifiableListener && provider.hasFeature(Identifiable.class))
        {
            provider.getFeature(Identifiable.class).addListener((IdentifiableListener) this);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        Check.notNull(listener);
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
