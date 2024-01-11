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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Feature;

/**
 * Abstract featurable base.
 */
public abstract class FeaturableAbstract implements Featurable
{
    /** Features provider. */
    private final Features features = new Features();

    /**
     * Create featurable.
     */
    protected FeaturableAbstract()
    {
        super();

        addFeatureInternal(new IdentifiableModel(), false);
    }

    /**
     * Add feature and prepare it.
     * 
     * @param feature The feature to add.
     * @param overwrite <code>true</code> to overwrite existing feature, <code>false</code> else.
     */
    private void addFeatureInternal(Feature feature, boolean overwrite)
    {
        if (feature instanceof final Recyclable recyclable)
        {
            recyclable.recycle();
        }
        feature.prepare(this);
        features.add(feature, overwrite);
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
    public final <T extends Feature> T addFeature(T feature)
    {
        addFeatureInternal(feature, false);
        return feature;
    }

    @Override
    public final <T extends Feature> T addFeature(T feature, boolean overwrite)
    {
        addFeatureInternal(feature, overwrite);
        return feature;
    }

    @Override
    public final <T extends Feature> T addFeature(Class<T> feature, Services services, Setup setup)
    {
        return FeaturableConfig.createAndAdd(feature, this, services, setup, XmlReader.EMPTY);
    }

    /**
     * {@inheritDoc} Does nothing by default.
     */
    @Override
    public void prepare()
    {
        // Nothing
    }

    @Override
    public final <C extends Feature> C getFeature(Class<C> feature)
    {
        return features.get(feature);
    }

    @Override
    public final Iterable<Feature> getFeatures()
    {
        return features.getFeatures();
    }

    @Override
    public final Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.getFeaturesType();
    }

    @Override
    public final boolean hasFeature(Class<? extends Feature> feature)
    {
        return features.contains(feature);
    }
}
