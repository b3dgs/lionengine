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

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Object with features.
 */
public final class ObjectFeatures extends FeaturableModel implements Localizable, Layerable, LayerableListener
{
    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ObjectFeatures(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(LayerableModel.class, services, setup);
    }

    @Override
    public double getX()
    {
        return 0.0;
    }

    @Override
    public double getY()
    {
        return 0.0;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        // Mock
    }

    @Override
    public void checkListener(Object listener)
    {
        // Mock
    }

    @Override
    public void addListener(LayerableListener listener)
    {
        // Mock
    }

    @Override
    public void removeListener(LayerableListener listener)
    {
        // Mock
    }

    @Override
    public void setLayer(Integer layerRefresh, Integer layerDisplay)
    {
        // Mock
    }

    @Override
    public Integer getLayerRefresh()
    {
        return null;
    }

    @Override
    public Integer getLayerDisplay()
    {
        return null;
    }

    @Override
    public void notifyLayerChanged(FeatureProvider provider,
                                   Integer layerRefreshOld,
                                   Integer layerRefreshNew,
                                   Integer layerDisplayOld,
                                   Integer layerDisplayNew)
    {
        // Mock
    }
}
